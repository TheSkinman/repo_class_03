package edu.uw.nrs.exchange;

import edu.uw.ext.framework.exchange.NetworkExchangeProxyFactory;
import edu.uw.ext.framework.exchange.StockExchange;

/**
 * A factory interface for creating ExchangeNetworkProxy instances.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public final class ExchangeNetworkProxyFactory implements NetworkExchangeProxyFactory {

	/**
	 * Constructor.
	 */
	public ExchangeNetworkProxyFactory() {

	}

	/**
	 * Instantiates a enabled ExchangeNetworkProxy.
	 * 
	 * @param multicastIP
	 *            the multicast ip address used to distribute events
	 * @param multicastPort
	 *            the port used to distribute events
	 * @param commandIP
	 *            the exchange host
	 * @param commandPort
	 *            the listening port to be used to accept command requests
	 * @return a newly instantiated ExchangeAdapter
	 */
	@Override
	public StockExchange newProxy(String multicastIP, int multicastPort, String commandIP, int commandPort) {
		return new ExchangeNetworkProxy(multicastIP, multicastPort, commandIP, commandPort);
	}

}
