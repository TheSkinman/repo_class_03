package edu.uw.nrs.exchange;

import static edu.uw.nrs.exchange.ProtocolConstants.BUY_ORDER;
import static edu.uw.nrs.exchange.ProtocolConstants.SELL_ORDER;
import static edu.uw.nrs.exchange.ProtocolConstants.ENCODING;
import static edu.uw.nrs.exchange.ProtocolConstants.GET_TICKERS_CMD;
import static edu.uw.nrs.exchange.ProtocolConstants.ELEMENT_DELIMITER;
import static edu.uw.nrs.exchange.ProtocolConstants.EXECUTE_TRADE_CMD;
import static edu.uw.nrs.exchange.ProtocolConstants.GET_QUOTE_CMD;
import static edu.uw.nrs.exchange.ProtocolConstants.GET_STATE_CMD;
import static edu.uw.nrs.exchange.ProtocolConstants.INVALID_STOCK;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.exchange.ExchangeListener;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.BuyOrder;
import edu.uw.ext.framework.order.Order;

/**
 * Client for interacting with a network accessible exchange. This
 * SocketExchange methods encode the method request as a string, per
 * ProtocolConstants, and send the command to the ExchangeNetworkAdapter,
 * receive the response decode it and return the result.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public class ExchangeNetworkProxy extends Object implements StockExchange {
	private static final Logger log = LoggerFactory.getLogger(ExchangeNetworkProxy.class.getName());

	/** The event socket. */
	private Socket eventSocket;
	
	/** The event printwriter. */
	private PrintWriter eventWriter;

	// private DataInputStream eventInput = null;
	private BufferedReader eventInput = null;
	private String cmdIpAddress;
	private int cmdPort;


	
	private String eventIpAddress;
	private int eventPort;
	private NetEventProcessor netEventProcessor;


	/**
	 * Constructor.
	 * 
	 * @param eventIpAddress
	 *            the multicast IP address to connect to
	 * @param eventPort
	 *            the multicast port to connect to
	 * @param cmdIpAddress
	 *            the address the exchange accepts request on
	 * @param cmdPort
	 *            the address the exchange accepts request on
	 */
	public ExchangeNetworkProxy(String eventIpAddress, int eventPort, String cmdIpAddress, int cmdPort) {
		// Store the command address and port.
		this.cmdIpAddress = cmdIpAddress;
		this.cmdPort = cmdPort;

		// Store and start up EVENTS
		netEventProcessor = new NetEventProcessor(eventIpAddress, eventPort);
		Executors.newSingleThreadExecutor().execute(netEventProcessor);
	}

	/**
	 * The state of the exchange.
	 * 
	 * Specified by: isOpen in interface edu.uw.ext.framework.exchange.StockExchange
	 * 
	 * @return true if the exchange is open otherwise false
	 */
	@Override
	public boolean isOpen() {
		log.debug("Starting to isOpen()...");
		final String response = sendTcpCmd(GET_STATE_CMD);
		log.debug("isOpen() return string = -> {} <-" + response);
		return response.equalsIgnoreCase("OPEN");
	}

	/**
	 * Gets the ticker symbols for all of the stocks in the traded on the exchange.
	 * 
	 * Specified by: getTickers in interface
	 * edu.uw.ext.framework.exchange.StockExchange
	 * 
	 * @return the stock ticker symbols
	 */
	@Override
	public String[] getTickers() {
		log.debug("Starting to getTickers()...");
		final String response = sendTcpCmd(GET_TICKERS_CMD);
		log.debug("getTickers() return string = -> {} <-" + response);
		return response.split(ELEMENT_DELIMITER);
	}

	/**
	 * Gets a stocks current price.
	 * 
	 * Specified by: getQuote in interface
	 * edu.uw.ext.framework.exchange.StockExchange
	 * 
	 * @param ticker
	 *            the ticker symbol for the stock
	 * @return the quote, or null if the quote is unavailable.
	 */
	@Override
	public Optional<StockQuote> getQuote(String ticker) {
		log.debug("Starting getQuote({})...", ticker);
		final String cmd = String.join(ELEMENT_DELIMITER, GET_QUOTE_CMD, ticker);
		final String response = sendTcpCmd(cmd);
		log.debug("getQuote() return string = -> {} <-", response);
		int price = INVALID_STOCK;

		try {
			price = Integer.parseInt(response);
		} catch (NumberFormatException e) {
			log.error("Unable to convert the price of [{}] returned for [{}]", price, ticker, e);
		}

		if (price >= 0) {
			return Optional.of(new StockQuote(ticker, price));
		} else {
			return Optional.<StockQuote>empty();
		}
	}

	/**
	 * Creates a command to execute a trade and sends it to the exchange.
	 * 
	 * Specified by: executeTrade in interface
	 * edu.uw.ext.framework.exchange.StockExchange
	 * 
	 * @param order
	 *            the order to execute
	 * @return the price the order was executed at
	 */
	@Override
	public int executeTrade(Order order) {

		// EXECUTE_TRADE_CMD:BUY_ORDER|SELL_ORDER:account_id:symbol:shares
		final String order_type = order.isBuyOrder() ? BUY_ORDER : SELL_ORDER;
		final String account_id = order.getAccountId();
		final String symbol = order.getStockTicker();
		final String shares = String.valueOf(order.getNumberOfShares());
		final String cmd = String.join(ELEMENT_DELIMITER, EXECUTE_TRADE_CMD, order_type, account_id, symbol, shares);
		final String response = sendTcpCmd(cmd);
		int returnPrice = 0;
		
		try {
			returnPrice = Integer.parseInt(response);
		} catch (NumberFormatException e) {
			log.error("number error", e);
		}
		
		return returnPrice;
	}

	/**
	 * Adds a market listener. Delegates to the NetEventProcessor.
	 * 
	 * Specified by: addExchangeListener in interface
	 * edu.uw.ext.framework.exchange.StockExchange
	 * 
	 * @param l
	 *            the listener to add
	 */
	@Override
	public synchronized void addExchangeListener(final ExchangeListener l) {
		netEventProcessor.addExchangeListener​(l);
	}

	/**
	 * Removes a market listener. Delegates to the NetEventProcessor.
	 * 
	 * Specified by: removeExchangeListener in interface
	 * edu.uw.ext.framework.exchange.StockExchange
	 * 
	 * @param l
	 *            the listener to remove
	 */
	@Override
	public synchronized void removeExchangeListener(final ExchangeListener l) {
		netEventProcessor.removeExchangeListener​(l);
	}

	private String sendTcpCmd(final String cmd) {
		log.debug("Starting to send the command {} over TCP.", cmd);
		PrintWriter printWriter = null;
		String response = null;
		BufferedReader br = null;

		try (Socket cmdSocket = new Socket(cmdIpAddress, cmdPort);) {
			log.info(String.format("Connected to server: %s:%d", cmdSocket.getLocalAddress(), cmdSocket.getLocalPort()));
			
			final InputStream inStrm = cmdSocket.getInputStream();
			final Reader rdr = new InputStreamReader(inStrm, ENCODING);
			br = new BufferedReader(rdr);

			final OutputStream outStrm = cmdSocket.getOutputStream();
			final Writer wrt = new OutputStreamWriter(outStrm, ENCODING);
			printWriter = new PrintWriter(wrt, true);

			printWriter.println(cmd);
			response = br.readLine();
		} catch (IOException e) {
			log.error("IO Exception when trying to create the COMMAND socket.", e);
		}

		return response;
	}

}
