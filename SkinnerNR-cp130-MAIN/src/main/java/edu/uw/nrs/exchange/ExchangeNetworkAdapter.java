package edu.uw.nrs.exchange;

import static edu.uw.nrs.exchange.ProtocolConstants.CLOSED_EVNT;
import static edu.uw.nrs.exchange.ProtocolConstants.ELEMENT_DELIMITER;
import static edu.uw.nrs.exchange.ProtocolConstants.OPEN_EVNT;
import static edu.uw.nrs.exchange.ProtocolConstants.PRICE_CHANGE_EVNT;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.exchange.ExchangeAdapter;
import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.StockExchange;

/**
 * Provides a network interface to an exchange.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public class ExchangeNetworkAdapter extends Object implements ExchangeAdapter {
	private static final Logger log = LoggerFactory.getLogger(ExchangeNetworkAdapter.class.getName());

	/** The Stock Exchange to be used */
	private StockExchange exchng;

	/** The COMMAND listener for commands. */
	private CommandListener commandListener;

	/** The EVENT socket. */
	private DatagramSocket eventSocket;

	/** The EVENT data packet for multicast. */
	private DatagramPacket datagramPacket;

	/** The time to live for threads. */
	private int TTL = 300*1000;
	
	/**
	 * Constructor
	 * 
	 * @param exchng
	 *            the exchange used to service the network requests
	 * @param multicastIP
	 *            the ip address used to propagate price changes
	 * @param multicastPort
	 *            the ip port used to propagate price changes
	 * @param commandPort
	 *            the ports for listening for commands
	 * @throws UnknownHostException
	 *             if unable to resolve multicast IP address
	 */
	public ExchangeNetworkAdapter( final StockExchange exchng,
								   final String multicastIP,
								   final int multicastPort, 
								   final int commandPort)
			throws UnknownHostException {
		
		// Store the exchange and add listener
		this.exchng = exchng;
		this.exchng.addExchangeListener(this);

		// Setup the Multicastbroadcaster
		final InetAddress multicastAddress = InetAddress.getByName(multicastIP);
		final byte[] buf = {};
		datagramPacket = new DatagramPacket(buf, buf.length, multicastAddress, multicastPort);
		try {
			eventSocket = new DatagramSocket();
			eventSocket = new MulticastSocket();
			eventSocket.setSoTimeout(TTL);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.debug("Multicast broadcaster prepared...");
		
		// Setup and start the command listener 
		commandListener = new CommandListener(commandPort, exchng);
		Executors.newSingleThreadExecutor().execute(commandListener);
		log.debug("Command Listener engaged...");
	}

	/**
	 * Close the adapter.
	 */
	@Override
	public void close() throws Exception {
		
		// 1. kill listener
		if (null != commandListener) {
			commandListener.terminate();
			commandListener = null;
		}
		
		// 2. remove listeners
		if (null != exchng) {
			exchng.removeExchangeListener(this);
			exchng = null;
		}
		
		// 3. kill socket
		if (null != eventSocket) {
			eventSocket.close();
			eventSocket = null;
		}
	}

	/**
	 * The exchange has opened and prices are adjusting - add listener to receive
	 * price change events from the exchange and multicast them to brokers.
	 * 
	 * exchangeOpened in interface edu.uw.ext.framework.exchange.ExchangeListener
	 * 
	 * @param event
	 *            the event
	 */
	@Override
	public void exchangeOpened(ExchangeEvent event) {
		try {
			multicast(OPEN_EVNT);
		} catch (IOException e) {
			log.error("Encountered an IO Exception while multicating the opening of the market.", e);
		} catch (InterruptedException e) {
			log.error("Encountered an Interrupted Exception while multicating the opening of the market.", e);
		}
	}

	/**
	 * The exchange has closed - notify clients and remove price change listener.
	 * 
	 * exchangeClosed in interface edu.uw.ext.framework.exchange.ExchangeListener
	 * 
	 * @param event
	 *            the event
	 */
	@Override
	public void exchangeClosed(ExchangeEvent event) {
		try {
			multicast(CLOSED_EVNT);
		} catch (IOException e) {
			log.error("Encountered an IO Exception while multicating the closing of the market.", e);
		} catch (InterruptedException e) {
			log.error("Encountered an Interrupted Exception while multicating the closing of the market.", e);
		}
	}

	/**
	 * Processes price change events.
	 * 
	 * priceChanged in interface edu.uw.ext.framework.exchange.ExchangeListener
	 * 
	 * @param event
	 *            the event
	 */
	@Override
	public void priceChanged(ExchangeEvent event) {
		try {
			final String price = String.valueOf(event.getPrice());
			final String eventPriceChangeMsg = String.join(ELEMENT_DELIMITER, PRICE_CHANGE_EVNT, event.getTicker(), price);
			multicast(eventPriceChangeMsg);
		} catch (IOException e) {
			log.error("Encountered an IO Exception while multicating the closing of the market.", e);
		} catch (InterruptedException e) {
			log.error("Encountered an Interrupted Exception while multicating the closing of the market.", e);
		}
	}

	/**
	 * Send out the multicast message.
	 * 
	 * @param multicastMessage
	 *            the message to send out
	 * @throws IOException
	 *             if there is an IO exception
	 * @throws InterruptedException
	 *             if there is an interruption during the multicast message being
	 *             sent out
	 */
	private synchronized void multicast(String multicastMessage) throws IOException, InterruptedException {
		log.info("Multicating: " + multicastMessage);
		final byte[] buf = multicastMessage.getBytes();
		datagramPacket.setData(buf, 0, buf.length);
		eventSocket.send(datagramPacket);
	}
}
