package edu.uw.nrs.exchange;

import static edu.uw.nrs.exchange.ProtocolConstants.CLOSED_EVNT;
import static edu.uw.nrs.exchange.ProtocolConstants.OPEN_EVNT;
import static edu.uw.nrs.exchange.ProtocolConstants.PRICE_CHANGE_EVNT;
import static edu.uw.nrs.exchange.ProtocolConstants.PRICE_CHANGE_EVNT_PRICE_ELEMENT;
import static edu.uw.nrs.exchange.ProtocolConstants.PRICE_CHANGE_EVNT_TICKER_ELEMENT;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import javax.swing.event.EventListenerList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.ExchangeListener;

/**
 * Listens for (by joining the multicast group) and processes events received
 * from the exchange. Processing the events consists of propagating them to
 * registered listeners.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public class NetEventProcessor implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(NetEventProcessor.class.getName());

	private String eventIpAddress;
	private int eventPort;

	/** The EVENT listener. */
	private EventListenerList listenerList = new EventListenerList();

	/**
	 * Constructor.
	 * 
	 * @param eventIpAddress
	 *            the multicast IP address to connect to
	 * @param eventPort
	 *            the multicast port to connect to
	 */
	public NetEventProcessor(String eventIpAddress, int eventPort) {
		this.eventIpAddress = eventIpAddress;
		this.eventPort = eventPort;
	}

	/**
	 * Continuously accepts and processes market and price change events.
	 */
	@Override
	public void run() {
		log.info("Starting client MULTICAST reading thread.");
		InetAddress multicastAddress = null;

		try {
			multicastAddress = InetAddress.getByName(eventIpAddress);
		} catch (UnknownHostException e) {
			log.error("Unknown Host while starting the client multicast reading thread.", e);
		}

		byte[] buf = new byte[256];
		try (MulticastSocket clientSocket = new MulticastSocket(eventPort);) {
			clientSocket.joinGroup(multicastAddress);

			while (!clientSocket.isClosed()) {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				clientSocket.receive(packet);
				final String receivedEvent = new String(buf, 0, buf.length);
				if ("end".equals(receivedEvent)) {
					break;
				}

				// Handle the incoming event
				final String[] eventArray = receivedEvent.trim().split(":");
				final String eventCommand = eventArray[0];

				switch (eventCommand) {
				case CLOSED_EVNT:
					log.info("Notifying listners: {}", CLOSED_EVNT);
					notifyEventListners(ExchangeEvent.newClosedEvent(this));
					break;
				case OPEN_EVNT:
					log.info("Notifying listners: {}", OPEN_EVNT);
					notifyEventListners(ExchangeEvent.newOpenedEvent(this));
					break;
				case PRICE_CHANGE_EVNT:
					log.info("Notifying listners: {} for {} at {}", PRICE_CHANGE_EVNT,
							eventArray[PRICE_CHANGE_EVNT_TICKER_ELEMENT], eventArray[PRICE_CHANGE_EVNT_PRICE_ELEMENT]);
					notifyEventListners(
							ExchangeEvent.newPriceChangedEvent(this, eventArray[PRICE_CHANGE_EVNT_TICKER_ELEMENT],
									Integer.parseInt(eventArray[PRICE_CHANGE_EVNT_PRICE_ELEMENT])));
					break;
				default:
					log.error("Event not recognized in the multievent [{}].", eventArray[0]);
					break;
				}
			}
			clientSocket.leaveGroup(multicastAddress);
			clientSocket.close();
		} catch (Exception e) {
			log.error("An error in the Multicast Socket!", e);
		}
	}

	/**
	 * Adds a market listener.
	 * 
	 * @param l
	 *            the listener to add
	 */
	public void addExchangeListener​(ExchangeListener l) {
		listenerList.add(ExchangeListener.class, l);
	}

	/**
	 * Removes a market listener.
	 * 
	 * @param l
	 *            the listener to remove
	 */
	public void removeExchangeListener​(ExchangeListener l) {
		listenerList.remove(ExchangeListener.class, l);
	}

	/**
	 * Notifies all the registered event listeners.
	 * 
	 * @param event
	 *            the event that has happened.
	 */
	private void notifyEventListners(final ExchangeEvent event) {
		ExchangeListener[] listeners;
		listeners = listenerList.getListeners(ExchangeListener.class);

		for (ExchangeListener listener : listeners) {
			switch (event.getEventType()) {
			case OPENED:
				listener.exchangeOpened(event);
				break;

			case CLOSED:
				listener.exchangeClosed(event);
				break;

			case PRICE_CHANGED:
				listener.priceChanged(event);
				break;

			default:
				log.warn("Attempted to fire an unknown exchange event: " + event.getEventType());
				break;
			}
		}
	}

}
