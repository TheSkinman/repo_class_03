package edu.uw.nrs.exchange;

import static edu.uw.nrs.exchange.ProtocolConstants.CLOSED_EVNT;
import static edu.uw.nrs.exchange.ProtocolConstants.OPEN_EVNT;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

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

	/** the EVENT/MULTICAST ip address used to propagate price changes */
	private String multicastIP;

	/** The EVENT/MULTICAST port used to propagate price changes. */
	private int multicastPort;
	
	
	// for killing it . This just is the thread firerer
	private Thread commandListener;

	/** The COMMAND port for listening for commands. */
	private int commandPort;

	/** The COMMAND socket. */
	private ServerSocket commandSock;
	
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
		this.exchng = exchng;
		this.exchng.addExchangeListener(this);
		this.commandPort = commandPort;

		commandListener = new Thread(new CommandListener(commandPort, exchng));
		commandListener.start();
		log.info("set command listener to run");
		
		this.multicastIP = multicastIP;
		this.multicastPort = multicastPort;
		
		
//		new Multicast
//		
//		make datagramand multicast inetaddress
//		
//		
//		add timeToLive = 
//
//		commandListener.start();
//		
//		
//		ExecutorService  deded = ExecutorService
//		
//		On the command listener, on terminate change listening = false
//		try (ServerSocket localServSock )
//			servsco = localSock
//			while(listening)
//		
//		commandListener = new CommandListener eeerf.execute()
//		Executors.newSin
//		
//		tru on the printwriter for flushing
//		
//		
//		
//		COMMAN_HANDLER - writes and reads
//		
//		   realExchange.isOpen() ? OPEN_STATE : CLOSED_STATE;
//		
//				
//				execute trade returns zero if market is closed
//				
//				Optional<StockQuote> 
//		
	}

	/**
	 * Close the adapter.
	 */
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
//		1. kill listener
		commandListener.stop();
//		2. remove listeners
		this.exchng.removeExchangeListener(this);
//		3. kill socket

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
			String eventPriceChangeMsg = String.format("%1$s%2$s%3$s%2$s%4$d", ProtocolConstants.PRICE_CHANGE_EVNT,
					ProtocolConstants.ELEMENT_DELIMITER, event.getTicker(), event.getPrice());
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
	private void multicast(String multicastMessage) throws IOException, InterruptedException {
		log.info("Multicating: " + multicastMessage);
		InetAddress multicastAddress = InetAddress.getByName(multicastIP);
		try (DatagramSocket commandSock = new DatagramSocket()) {
			byte[] buf = multicastMessage.getBytes();

			DatagramPacket packet = new DatagramPacket(buf, buf.length, multicastAddress, multicastPort);
			commandSock.send(packet);
			Thread.sleep(500);
		}
	}

}
