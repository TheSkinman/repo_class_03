package edu.uw.nrs.exchange;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.uw.ext.framework.exchange.ExchangeAdapter;
import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.StockExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public ExchangeNetworkAdapter(StockExchange exchng, String multicastIP, int multicastPort, int commandPort)
			throws UnknownHostException {
		this.exchng = exchng;
		this.multicastIP = multicastIP; 
		this.multicastPort = multicastPort;  
		this.commandPort = commandPort;
		this.exchng.addExchangeListener(this);
		
	}

	/**
	 * Close the adapter.
	 */
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

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
			multicast(ProtocolConstants.OPEN_EVNT);
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
			multicast(ProtocolConstants.CLOSED_EVNT);
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
			String eventPriceChangeMsg = String.format("%1$s%2$s%3$s%2$s%4$d",
					ProtocolConstants.PRICE_CHANGE_EVNT,
					ProtocolConstants.ELEMENT_DELIMITER,
					event.getTicker(),
					event.getPrice());
			multicast(eventPriceChangeMsg);
		} catch (IOException e) {
			log.error("Encountered an IO Exception while multicating the closing of the market.", e);
		} catch (InterruptedException e) {
			log.error("Encountered an Interrupted Exception while multicating the closing of the market.", e);
		}
	}

	private void multicast(String multicastMessage) throws IOException, InterruptedException {
		log.info("Multicating: " + multicastMessage);
		InetAddress multicastAddress = InetAddress.getByName(multicastIP);
		try (DatagramSocket serverSocket = new DatagramSocket()) {
			byte[] buf = multicastMessage.getBytes();
			
			DatagramPacket packet = new DatagramPacket(buf, buf.length, multicastAddress, multicastPort);
			serverSocket.send(packet);
			System.out.println("Server sent packet with msg: " + multicastMessage);
            Thread.sleep(500);
		}
	}	
}
