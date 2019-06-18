package edu.uw.nrs.web;

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
public class StockQuoteServlet implements Serializable, Servlet, ServletConfig {
	private static final long serialVersionUID = -1684837528876551081L;

	private final String replyFmt = "<html><head><title>Greetings</title>"
			+ "<body><h1>%s</h1></body></html>";
			private String greeting = "hello";
	
	
	/**
	 * Default constructor.
	 */
	public StockQuoteServlet() {}
	
	/**
	 * Called by the servlet container when the servlet is loaded.
	 * 
	 * @param servletCfg a ServletConfig object containing the servlet's configuration and initialization parameters
	 * 
	 * @throws javax.servlet.ServletException if one is raised
	 */
	public void init(ServletConfig servletCfg) throws ServletException {
		//super.init(servletCfg);
		greeting = servletCfg.getInitParameter("greeting");
	}
	
	/**
	 * Delegates GET requests to the serviceRequest method.
	 * 
	 * @param request a GET request. 
	 * @param response a GET response.
	 * @throws IOException 
	 */
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		String reply = String.format(replyFmt, greeting);
		response.setContentType("text/html");
		response.setContentLength(reply.length());
		PrintWriter wtr = response.getWriter();
		wtr.print(reply);
		wtr.close();
	}
	
	/**
	 * Delegates POST requests to the serviceRequest method.
	 * 
	 * @param request a POST request.
	 * @param response a POST response.
	 */
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) {
		
	}

	// All the other required methods.
	@Override public String getInitParameter(String arg0) { return null; }
	@Override public Enumeration<String> getInitParameterNames() { return null; }
	@Override public ServletContext getServletContext() { return null; }
	@Override public String getServletName() { return null; }
	@Override public void destroy() { }
	@Override public ServletConfig getServletConfig() { return null; }
	@Override public String getServletInfo() { return null; }
	@Override public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException { }
}
