package web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Enumeration;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class QuoteServlet, obtains a stock quote from Yahoo
 * Financial Services and returns it as an XML document. The resulting document
 * is of the form:<br>
 * <code>
 * &lt;quote&gt;<br>
 * &nbsp;&nbsp;&lt;symbol&gt;symbol&lt;/symbol&gt;<br>
 * &nbsp;&nbsp;&lt;price&gt;price&lt;/price&gt;<br>
 * &lt;/quote&gt;<br>
 * </code>
 * <br>
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public class StockQuoteServlet extends HttpServlet { //  implements Serializable, Servlet, ServletConfig {
	private static final long serialVersionUID = -1684837528876551081L;
	private ServletContext ctx;

	private final String replyFmt = "<html><head><title>Greetings</title>"
			+ "<body><h1>%s</h1></body></html>";
	private String greeting = "hello";
	
	
	/**
	 * Default constructor.
	 */
	public StockQuoteServlet() {
	}
	
	/**
	 * Called by the servlet container when the servlet is loaded.
	 * 
	 * @param servletCfg a ServletConfig object containing the servlet's configuration and initialization parameters
	 * 
	 * @throws javax.servlet.ServletException if one is raised
	 */
	public void init(ServletConfig servletCfg) throws ServletException {
		ctx = servletCfg.getServletContext();
		
		greeting = servletCfg.getInitParameter("greeting");
	}
	
	/**
	 * Delegates GET requests to the serviceRequest method.
	 * 
	 * @param request a GET request. 
	 * @param response a GET response.
	 * @throws IOException 
	 */
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        serviceRequest(request, response);
	}
	
	/**
	 * Delegates POST requests to the serviceRequest method.
	 * 
	 * @param request a POST request.
	 * @param response a POST response.
	 * @throws IOException 
	 */
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        serviceRequest(request, response);
	}

	void serviceRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String format = request.getParameter("format");
        String responseXml = null;
        switch (format) {
            case "html":
                responseXml = "html";
                break;
            case "json":
                responseXml = "json";
                break;
            case "plain":
                responseXml = "plain";
                break;
            default:
                responseXml = "xml";
        }

        response.setContentType("text/xml");
        response.setContentLength(responseXml.length());
        response.getWriter().print(responseXml);
    }

}
