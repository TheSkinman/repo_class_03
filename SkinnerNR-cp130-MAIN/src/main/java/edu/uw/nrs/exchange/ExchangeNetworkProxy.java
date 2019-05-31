package edu.uw.nrs.exchange;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;

import javax.swing.event.EventListenerList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.exchange.ExchangeListener;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
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

	private String eventIpAddress;
	private int eventPort;

	Thread multicastReceiver = new Thread(new Runnable() {


		public void run() {
			log.info("Starting client MULTICAST reading thread.");
			InetAddress multicastAddress = null;
			try {
				multicastAddress = InetAddress.getByName(eventIpAddress);
			} catch (UnknownHostException e1) {
				log.error("Unknown Host while starting the client multicast reading thread.", e1);
			}
			
			byte[] buf = new byte[256];
			try (MulticastSocket clientSocket = new MulticastSocket(eventPort);){
				clientSocket.joinGroup(multicastAddress);
				
				while (true) {
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					clientSocket.receive(packet);
					String received = new String(buf, 0, buf.length);
					if ("end".equals(received)) {
						break;
					}
					
					// Handle the incoming event
					here

					
				}
				clientSocket.leaveGroup(multicastAddress);
				clientSocket.close();
			} catch (Exception e) {

			}
		}
	}, "MulticastReader - thread");

	/** The command socket. */
	private Socket cmdSocket;
	/** The command printwriter. */
	private PrintWriter cmdWriter;

	/** The EVENT listener. */
	private EventListenerList listenerList = new EventListenerList();

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

		// // Initialize the COMMAND socket.
		// try {
		// cmdSocket = new Socket(cmdIpAddress, cmdPort);
		// } catch (UnknownHostException e) {
		// log.error("Unknown Host Exception when trying to create the COMMAND socket.",
		// e);
		// } catch (IOException e) {
		// log.error("IO Exception when trying to create the COMMAND socket.", e);
		// }
		// OutputStream cmdOS = null;
		// try {
		// cmdOS = cmdSocket.getOutputStream();
		// } catch (IOException e) {
		// log.error("IO Exception when trying to get the COMMAND output stream.", e);
		// }
		// cmdWriter = new PrintWriter(new OutputStreamWriter(cmdOS), true);

		// Initialize the EVENT socket.
		// try {
		// eventSocket = new Socket(eventIpAddress, eventPort);
		// log.info("Event socket connected.");
		//
		//
		//
		// } catch (UnknownHostException e) {
		// log.error("Unknown Host Exception when trying to create the EVENT socket.",
		// e);
		// } catch (IOException e) {
		// log.error("IO Exception when trying to create the EVENT socket.", e);
		// }

		this.eventIpAddress = eventIpAddress;
		this.eventPort = eventPort;

		multicastReceiver.start();
	}

	// public void run() {
	// try {
	// eventInput = new BufferedReader(new
	// InputStreamReader(eventSocket.getInputStream()));
	// String line = null;
	//
	// log.info("NetEventProcessor connected and waiting for event...");
	// while ((line = eventInput.readLine()) != null && eventSocket != null &&
	// !eventSocket.isClosed() && eventInput != null) {
	// log.info("Raw EVENT receiveed -=> " + line);
	// }
	// } catch (IOException ex) {
	// System.err.println("Server error: " + ex);
	// } finally {
	// if (eventSocket != null) {
	// try {
	// eventSocket.close();
	// } catch (IOException ioex) {
	// System.err.println("Error closing EVENT socket. " + ioex);
	// }
	// }
	// }
	// }

	/**
	 * The state of the exchange.
	 * 
	 * Specified by: isOpen in interface edu.uw.ext.framework.exchange.StockExchange
	 * 
	 * @return true if the exchange is open otherwise false
	 */
	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
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
		String tester = ProtocolConstants.GET_TICKERS_CMD;
		return null;
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return 0;
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
	public void addExchangeListener(ExchangeListener l) {
		listenerList.add(ExchangeListener.class, l);
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
	public void removeExchangeListener(ExchangeListener l) {
		listenerList.remove(ExchangeListener.class, l);
	}

}
