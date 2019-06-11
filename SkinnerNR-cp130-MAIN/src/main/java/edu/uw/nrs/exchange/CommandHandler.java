package edu.uw.nrs.exchange;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.MarketBuyOrder;
import edu.uw.ext.framework.order.MarketSellOrder;
import edu.uw.ext.framework.order.Order;

import static edu.uw.nrs.exchange.ProtocolConstants.ELEMENT_DELIMITER;
import static edu.uw.nrs.exchange.ProtocolConstants.ENCODING;
import static edu.uw.nrs.exchange.ProtocolConstants.GET_STATE_CMD;
import static edu.uw.nrs.exchange.ProtocolConstants.GET_TICKERS_CMD;
import static edu.uw.nrs.exchange.ProtocolConstants.GET_QUOTE_CMD;
import static edu.uw.nrs.exchange.ProtocolConstants.OPEN_STATE;
import static edu.uw.nrs.exchange.ProtocolConstants.CLOSED_STATE;
import static edu.uw.nrs.exchange.ProtocolConstants.BUY_ORDER;
import static edu.uw.nrs.exchange.ProtocolConstants.INVALID_STOCK;
import static edu.uw.nrs.exchange.ProtocolConstants.EXECUTE_TRADE_CMD;
import static edu.uw.nrs.exchange.ProtocolConstants.EXECUTE_TRADE_CMD_ACCOUNT_ELEMENT;
import static edu.uw.nrs.exchange.ProtocolConstants.EXECUTE_TRADE_CMD_SHARES_ELEMENT;
import static edu.uw.nrs.exchange.ProtocolConstants.EXECUTE_TRADE_CMD_TICKER_ELEMENT;
import static edu.uw.nrs.exchange.ProtocolConstants.EXECUTE_TRADE_CMD_TYPE_ELEMENT;

/**
 * An instance of this class is dedicated to executing commands received from
 * clients.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public class CommandHandler implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(CommandHandler.class.getName());

	private Socket sock;

	private StockExchange realExchange;

	/**
	 * Constructor.
	 * 
	 * @param sock
	 *            the socket for communication with the client
	 * @param realExchange
	 *            the "real" exchange to dispatch commands to
	 */
	public CommandHandler(Socket sock, StockExchange realExchange) {
		this.sock = sock;
		this.realExchange = realExchange;
	}

	/**
	 * Processes the command.
	 */
	@Override
	public void run() {
		log.debug("in cmdHndlr");
		
		try {
			final InputStream inStrm = sock.getInputStream();
			final InputStreamReader rdr = new InputStreamReader(inStrm, ENCODING);
			final BufferedReader in = new BufferedReader(rdr);

			final OutputStream outStr = sock.getOutputStream();
			final Writer wrtr = new OutputStreamWriter(outStr);
			final PrintWriter out = new PrintWriter(wrtr, true);
			//final PrintWriter out = new PrintWriter(sock.getOutputStream(), true);

			
			final String line = in.readLine();
			log.debug("line=" + line);
			final String[] commandReceived = line.split(ELEMENT_DELIMITER);
			
			if (commandReceived == null || commandReceived.length == 0) {
				return;
			}
			
			final String command = commandReceived[0];
			log.debug("command in handler = " + command);
			switch (command) {
			case GET_STATE_CMD:
				log.debug("cmdHdlr working GET_STATE_CMD");
				final String marketState = realExchange.isOpen() ? OPEN_STATE : CLOSED_STATE;
				log.debug("market state is: " + marketState);
				out.write(marketState);
				break;

			case GET_TICKERS_CMD:
				log.debug("cmdHdlr working GET_TICKERS_CMD");
				final String allTickers[] = realExchange.getTickers();
				final String returnTickers = String.join(ELEMENT_DELIMITER, allTickers);
				log.debug("we have the tickers: " + returnTickers);
				out.write(returnTickers);
				log.debug("wrote the tickers.");
				break;
			
			case GET_QUOTE_CMD:
				log.debug("cmdHdlr working GET_QUOTE_CMD");
				final String ticker = commandReceived[1];
				
				final Optional<StockQuote> response = realExchange.getQuote(ticker);
				
				if (response.isPresent()) {
					String quotePrice = String.valueOf(response.get().getPrice()); 
					log.debug("quote from exchange: " + quotePrice);
					out.write(quotePrice);
				} else {
					out.write(INVALID_STOCK);
				}
				
				log.debug("wrote the tickers.");
				break;
			
			case EXECUTE_TRADE_CMD:
				log.debug("cmdHdlr working GET_TICKERS_CMD");
				// EXECUTE_TRADE_CMD:BUY_ORDER|SELL_ORDER:account_id:symbol:shares
				final boolean isBuyOrder = commandReceived[EXECUTE_TRADE_CMD_TYPE_ELEMENT].equalsIgnoreCase(BUY_ORDER) ? true  : false;
				final String accountID = commandReceived[EXECUTE_TRADE_CMD_ACCOUNT_ELEMENT];
				final String stockTicker = commandReceived[EXECUTE_TRADE_CMD_TICKER_ELEMENT];
				final int numberOfShares = Integer.parseInt(commandReceived[EXECUTE_TRADE_CMD_SHARES_ELEMENT]);
				log.debug(String.format("order is buy [%s], acct: %s, for: %s, total shares: %d",
						isBuyOrder ? "TRUE" : "FALSE",
						accountID,
						stockTicker,
						numberOfShares));
				
				Order order = null;
				
				if (isBuyOrder) {
					order = new MarketBuyOrder(accountID, numberOfShares, stockTicker);
				} else {
					order = new MarketSellOrder(accountID, numberOfShares, stockTicker);
				}
				
				final int price = realExchange.executeTrade(order);
				out.write(String.valueOf(price));
				
				break;
				
			default:
				break;
			}
			
			
			out.close();
			in.close();
		} catch (IOException e) {
			log.error("An IO Exception: ", e);

		} finally {
			try {
				sock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		log.debug("this cmdHdlr is done.");
	}

}
