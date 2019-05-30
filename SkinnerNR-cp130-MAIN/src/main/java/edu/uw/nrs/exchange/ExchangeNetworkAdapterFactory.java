package edu.uw.nrs.exchange;

import edu.uw.ext.framework.exchange.ExchangeAdapter;
import edu.uw.ext.framework.exchange.NetworkExchangeAdapterFactory;
import edu.uw.ext.framework.exchange.StockExchange;

import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A NetworkExchangeAdapterFactory implementation for creating
 * ExchangeNetworkAdapter instances.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public final class ExchangeNetworkAdapterFactory extends Object implements NetworkExchangeAdapterFactory {
	private static final Logger log = LoggerFactory.getLogger(ExchangeNetworkAdapterFactory.class.getName());

	/**
	 * Contructor.
	 */
	public ExchangeNetworkAdapterFactory() {

	}

	/**
	 * Instantiates an ExchangeNetworkAdapter.
	 * 
	 * @param exchange
	 *            the underlying real exchange
	 * @param multicastIP
	 *            the multicast ip address used to distribute events
	 * @param multicastPort
	 *            the port used to distribute events
	 * @param commandPort
	 *            the listening port to be used to accept command requests
	 * @return a newly instantiated ExchangeNetworkAdapter, or null if instantiation
	 *         fails
	 */
	@Override
	public ExchangeAdapter newAdapter(StockExchange exchange, String multicastIP, int multicastPort, int commandPort) {
		try {
			return new ExchangeNetworkAdapter(exchange, multicastIP, multicastPort, commandPort);
		} catch (UnknownHostException e) {
			log.error("Unable to create the Exchange Adapter.", e);
		}
		return null;
	}

}
