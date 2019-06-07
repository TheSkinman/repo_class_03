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

import static edu.uw.nrs.exchange.ProtocolConstants.ELEMENT_DELIMITER;
import static edu.uw.nrs.exchange.ProtocolConstants.ENCODING;
import static edu.uw.nrs.exchange.ProtocolConstants.GET_STATE_CMD;
import static edu.uw.nrs.exchange.ProtocolConstants.GET_TICKERS_CMD;
import static edu.uw.nrs.exchange.ProtocolConstants.GET_QUOTE_CMD;
import static edu.uw.nrs.exchange.ProtocolConstants.EXECUTE_TRADE_CMD;

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
		log.info("in cmdHndlr");
		
		try {
			final InputStream inStrm = sock.getInputStream();
			final InputStreamReader rdr = new InputStreamReader(inStrm, ENCODING);
			final BufferedReader in = new BufferedReader(rdr);
			
			
			

//			final OutputStream outStrm = sock.getOutputStream();
//			final Writer wrt = new OutputStreamWriter(outStrm, ENCODING);
//			final PrintWriter printWriter = new PrintWriter(wrt, true);
			final PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
			

			final String line = in.readLine();
			log.info("line=" + line);
			final String[] commandReceived = line.split(ELEMENT_DELIMITER);
			
			if (commandReceived == null || commandReceived.length == 0) {
				return;
			}
			
			final String command = commandReceived[0];
			log.info("command in handler = " + command);
			switch (command) {
			case GET_STATE_CMD:
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
				out.write(String.valueOf(response.get().getPrice()));
				
				break;
			
			case EXECUTE_TRADE_CMD:
				break;
				
			default:
				break;
			}
			
			
			out.close();
			in.close();
		} catch (IOException e) {
			log.error("", e);

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
