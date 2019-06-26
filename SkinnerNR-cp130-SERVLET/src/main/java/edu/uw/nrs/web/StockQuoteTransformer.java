package edu.uw.nrs.web;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class StockQuoteTransformer implements Filter {
	private static final int PRICE_INDEX = 2;
	private static final int SYMBOL_INDEX = 1;
	private static final int FORMAT_INDEX = 0;
	private ServletContext ctx;
	private String toHtmlFormat;
	private String toJsonFormat;
	private String toPlainFormat;
	private String toXmlFormat;

	public StockQuoteTransformer() {
	}
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
	    CharResponseWrapper wrapper = new CharResponseWrapper((HttpServletResponse)response);
	    chain.doFilter(request, wrapper);

	    String[] responseParts = wrapper.toString().split(":");
	    
	    String format = responseParts[FORMAT_INDEX];
	    String symbol = responseParts[SYMBOL_INDEX];
	    String price = responseParts[PRICE_INDEX];
	    
		String responseOut = "";
		
		switch (format) {
		case "html":
			responseOut = String.format(toHtmlFormat, symbol, price);
			response.setContentType("text/html");
			break;
		case "json":
			responseOut = String.format(toJsonFormat, symbol, price);
			response.setContentType("text/plain");
			break;
		case "plain":
			responseOut = String.format(toPlainFormat, symbol, price);
			response.setContentType("text/plain");
			break;
		case "xml":
			responseOut = String.format(toXmlFormat, symbol, price);
			response.setContentType("text/xml");
			break;
		default:
			responseOut = "Format was not found.";
			response.setContentType("text/plain");
		}
		
        response.setContentLength(responseOut.length());
        response.getWriter().print(responseOut);
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ctx = filterConfig.getServletContext();
        
		toHtmlFormat = loadFormat(filterConfig, "htmlFormat");
		toJsonFormat = loadFormat(filterConfig, "jsonFormat");
		toPlainFormat = loadFormat(filterConfig, "plainFormat");
		toXmlFormat = loadFormat(filterConfig, "xmlFormat");
	}

	private String loadFormat(FilterConfig filterConfig, final String format) {
		String path = filterConfig.getInitParameter(format);
        String realPath = ctx.getRealPath(path);
        File f = new File(realPath);
        try (DataInputStream dataIn = new DataInputStream(new FileInputStream(f))) {
            byte[] bytes = new byte[(int)f.length()];
            dataIn.readFully(bytes);
            String readString = new String(bytes, "UTF8");
            return readString;
        } catch (IOException e) {
            ctx.log("Error reading HTML transform file.", e);
        }
        return null;
	}
}
