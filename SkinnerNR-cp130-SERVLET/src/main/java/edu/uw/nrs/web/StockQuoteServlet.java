package edu.uw.nrs.web;

//import static edu.uw.ext.quote.AlphaVantageQuote.getQuote;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import edu.uw.ext.quote.AlphaVantageQuote;

/**
 * Servlet implementation class QuoteServlet, obtains a stock quote from Yahoo
 * Financial Services and returns it as an XML document. The resulting document
 * is of the form:<br>
 * <code>
 * &lt;quote&gt;<br>
 * &nbsp;&nbsp;&lt;symbol&gt;symbol&lt;/symbol&gt;<br>
 * &nbsp;&nbsp;&lt;price&gt;price&lt;/price&gt;<br>
 * &lt;/quote&gt;<br>
 * </code> <br>
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public class StockQuoteServlet extends HttpServlet {
	private static final long serialVersionUID = -1684837528876551081L;
	private ServletContext ctx;

	/**
	 * Default constructor.
	 */
	public StockQuoteServlet() {
	}

	/**
	 * Called by the servlet container when the servlet is loaded.
	 * 
	 * @param servletCfg
	 *            a ServletConfig object containing the servlet's configuration and
	 *            initialization parameters
	 * 
	 * @throws javax.servlet.ServletException
	 *             if one is raised
	 */
	public void init(ServletConfig servletCfg) throws ServletException {
		ctx = servletCfg.getServletContext();
	}

	/**
	 * Delegates GET requests to the serviceRequest method.
	 * 
	 * @param request
	 *            a GET request.
	 * @param response
	 *            a GET response.
	 * @throws IOException
	 */
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		serviceRequest(request, response);
	}

	/**
	 * Delegates POST requests to the serviceRequest method.
	 * 
	 * @param request
	 *            a POST request.
	 * @param response
	 *            a POST response.
	 * @throws IOException
	 */
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		serviceRequest(request, response);
	}

	void serviceRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		String responseString = null;
		String format = request.getParameter("format");
		format = format == null ? "plain" : format;
		String symbol = request.getParameter("symbol");
		symbol = symbol == null ? "goog" : symbol; 

		int price = getRandomNumberInRange(123, 2345);

//		int price = -1;
//		try {
//			AlphaVantageQuote avQuote = AlphaVantageQuote.getQuote(symbol);
//			price = avQuote.getPrice();
//		} catch (Exception e) {
//			ctx.log("Did not get quote. Setting to a default value.");
//			price = 1234;
//		}

		responseString = String.format("%s:%s:%d", format, symbol, price);

		response.setContentType("text/plain");
		response.setContentLength(responseString.length());
		response.getWriter().print(responseString);
	}
	
	private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
}
