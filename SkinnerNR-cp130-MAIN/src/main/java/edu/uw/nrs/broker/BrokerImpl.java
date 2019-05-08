package edu.uw.nrs.broker;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.ExchangeListener;
import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.broker.Broker;
import edu.uw.ext.framework.broker.BrokerException;
import edu.uw.ext.framework.broker.OrderManager;
import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.MarketBuyOrder;
import edu.uw.ext.framework.order.MarketSellOrder;
import edu.uw.ext.framework.order.Order;
import edu.uw.ext.framework.order.StopBuyOrder;
import edu.uw.ext.framework.order.StopSellOrder;

/**
 * An implementation of the Broker interface, provides a full implementation
 * less the creation of the order manager and market queue.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public class BrokerImpl implements Broker, ExchangeListener {
	private static final Logger log = LoggerFactory.getLogger(BrokerImpl.class.getName());

	/** The market order queue. */
	protected OrderQueue<Boolean, Order> marketOrders;

	/** Name of the broker */
	private String brokerName;

	/** The account manager to be used by the broker */
	private AccountManager acctMgr;

	/** The stock exchange to be used by the broker */
	private StockExchange exchg;

	private HashMap<String, OrderManagerImpl> orderManagers;

	/**
	 * Constructor for sub classes
	 * 
	 * @param brokerName
	 *            name of the broker
	 * @param exchg
	 *            the stock exchange to be used by the broker
	 * @param acctMgr
	 *            the account manager to be used by the broker
	 */
	protected BrokerImpl(String brokerName, StockExchange exchg, AccountManager acctMgr) {
		this.brokerName = brokerName;
		this.exchg = exchg;
		this.acctMgr = acctMgr;
	}

	/**
	 * Constructor.
	 * 
	 * @param brokerName
	 *            name of the broker
	 * @param acctMgr
	 *            the account manager to be used by the broker
	 * @param exchg
	 *            the stock exchange to be used by the broker
	 */
	public BrokerImpl(String brokerName, AccountManager acctMgr, StockExchange exchg) {
		this(brokerName, exchg, acctMgr);

		marketOrders = new OrderQueueImpl<Boolean, Order>(exchg.isOpen(), (i, o) -> i);
		marketOrders.setOrderProcessor(this::executeOrder);

		// Create the Order Managers
		initializeOrderManagers();

		// Add self to the exchange listeners
		exchg.addExchangeListener(this);
	}

	/**
	 * Execute an order with the exchange, satisfies the Consumer<Order> functional
	 * interface.
	 * 
	 * @param order
	 *            the order to execute
	 */
	protected void executeOrder(Order order) {
		// log if enabled

		// execute trade

		// get account

	}

	/**
	 * Fetch the stock list from the exchange and initialize an order manager for
	 * each stock. Only to be used during construction.
	 */
	protected final void initializeOrderManagers() {
		orderManagers = new HashMap<>();
		Consumer<StopBuyOrder> moveBuyToMarketOrderProcessor = (StopBuyOrder order) -> marketOrders.enqueue(order);
		Consumer<StopSellOrder> moveSellToMarketOrderProcessor = (StopSellOrder order) -> marketOrders.enqueue(order);

		// one consumer for all consumers for each
		Consumer<? super StockQuote> addStockAction = (q) -> {
			int pr = q.getPrice();
		};
		
		for (String stockTick : exchg.getTickers()) {
			OrderManagerImpl orderManager = new OrderManagerImpl(stockTick, 0);
			
			orderManagers.put(stockTick, orderManager);
		}

	}

	/**
	 * Create an appropriate order manager for this broker. Only to be used during
	 * construction.
	 * 
	 * @param ticker
	 *            the ticker symbol of the stock
	 * @param initialPrice
	 *            current price of the stock
	 * @return a new OrderManager for the specified stock
	 */
	protected OrderManager createOrderManager(String ticker, int initialPrice) {
		return null;
	}

	/**
	 * Upon the exchange opening sets the market dispatch filter threshold and
	 * processes any available orders.
	 * 
	 * @param event
	 *            the price change event
	 */
	@Override
	public final synchronized void priceChanged(ExchangeEvent event) {
		log.info("PRICE CHANGED");

		// get order manager
		OrderManager orderManager = orderManagers.get(event.getTicker());

		// set threshold
		if (orderManager != null) {
			orderManager.adjustPrice(event.getPrice());
		}
	}

	/**
	 * Upon the exchange opening sets the market dispatch filter threshold and
	 * processes any available orders.
	 * 
	 * @param event
	 *            the exchange (open) event
	 */
	@Override
	public void exchangeOpened(ExchangeEvent event) {
		log.info("Market is now OPENED.");
		marketOrders.setThreshold(true);
	}

	/**
	 * Upon the exchange opening sets the market dispatch filter threshold.
	 * 
	 * @param event
	 *            the exchange (closed) event
	 */
	@Override
	public void exchangeClosed(ExchangeEvent event) {
		log.info("Market is now CLOSED.");
		marketOrders.setThreshold(false);
	}

	/**
	 * Get the name of the broker.
	 * 
	 * @return the name of the broker
	 */
	@Override
	public String getName() {
		return brokerName;
	}

	/**
	 * Create an account with the broker.
	 * 
	 * @param username
	 *            the user or account name for the account
	 * @param password
	 *            the password for the new account
	 * @param balance
	 *            the initial account balance in cents
	 * @return the new account
	 * @throws BrokerException
	 *             if unable to create account
	 */
	@Override
	public Account createAccount(String username, String password, int balance) throws BrokerException {
		try {
			return acctMgr.createAccount(username, password, balance);
		} catch (AccountException e) {
			log.error("An error was encountered creating the account:" + username);
			throw new BrokerException("An error was encountered creating the account:" + username, e);
		}
	}

	/**
	 * Delete an account with the broker.
	 * 
	 * @param username
	 *            the user or account name for the account
	 * @throws BrokerException
	 *             if unable to delete account
	 */
	@Override
	public void deleteAccount(String username) throws BrokerException {
		try {
			acctMgr.deleteAccount(username);
			log.info("The following account was deleted: " + username);
		} catch (AccountException e) {
			log.error("An error was encountered deleting the account: " + username);
			throw new BrokerException("An error was encountered deleting the account: " + username, e);
		}
	}

	/**
	 * Locate an account with the broker. The username and password are first
	 * verified and the account is returned.
	 * 
	 * @param username
	 *            the user or account name for the account
	 * @param password
	 *            the password for the new account
	 * @return the account
	 * @throws BrokerException
	 *             username and password are invalid
	 */
	@Override
	public Account getAccount(String username, String password) throws BrokerException {
		boolean isVerified = false;

		try {
			isVerified = acctMgr.validateLogin(username, password);
		} catch (AccountException e) {
			throw new BrokerException("Unable to verify access to the account.", e);
		}
		
		if (!isVerified) {
			throw new BrokerException("Invalid account or password.");
		}

		try {
			return acctMgr.getAccount(username);
		} catch (AccountException e) {
			throw new BrokerException("Unable to get the account.", e);
		}
	}

	/**
	 * Place an order with the broker.
	 * 
	 * @param the
	 *            order being placed with the broker
	 */
	@Override
	public void placeOrder(MarketBuyOrder order) {
		// TODO Auto-generated method stub

	}

	/**
	 * Place an order with the broker.
	 * 
	 * @param the
	 *            order being placed with the broker
	 */
	@Override
	public void placeOrder(MarketSellOrder order) {
		// TODO Auto-generated method stub

	}

	/**
	 * Place an order with the broker.
	 * 
	 * @param the
	 *            order being placed with the broker
	 * @throws BrokerException
	 *             if unable to place order
	 */
	@Override
	public void placeOrder(StopBuyOrder order) throws BrokerException {
		// TODO Auto-generated method stub

	}

	/**
	 * Place an order with the broker.
	 * 
	 * @param the
	 *            order being placed with the broker
	 * @throws BrokerException
	 *             if unable to place order
	 */
	@Override
	public void placeOrder(StopSellOrder order) throws BrokerException {
		// TODO Auto-generated method stub

	}

	/**
	 * Get a price quote for a stock.
	 * 
	 * @param ticker
	 *            the stocks ticker symbol
	 * @return optional containing quote, or empty if unable to obtain quote
	 */
	@Override
	public Optional<StockQuote> requestQuote(String ticker) {
		return exchg.getQuote(ticker);
	}

	/**
	 * Release broker resources.
	 * 
	 * @throws BrokerException
	 *             if the operation fails
	 */
	@Override
	public void close() throws BrokerException {
		// Closing the account manager
		try {
			acctMgr.close();
		} catch (AccountException e) {
			log.error("Encountered an error trying to close the Account Manager.", e);
		}

		// removing listener
		exchg.removeExchangeListener(this);
		
		// Null-ing up some items
		marketOrders = null;
		orderManagers = null;
	}

}
