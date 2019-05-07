package edu.uw.nrs.broker;

import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.broker.OrderManager;
import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.order.StopBuyOrder;
import edu.uw.ext.framework.order.StopSellOrder;

/**
 * Maintains queues to different types of orders and requests the execution of
 * orders when price conditions allow their execution.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public class OrderManagerImpl implements OrderManager {
	private static final Logger log = LoggerFactory.getLogger(OrderManagerImpl.class.getName());

	private String stockTickerSymbol;

	/** Queue for stop buy orders */
	protected OrderQueue<Integer, StopBuyOrder> stopBuyOrderQueue;

	/** Queue for stop sell orders */
	protected OrderQueue<Integer, StopSellOrder> stopSellOrderQueue;

	/**
	 * Constructor. Constructor to be used by sub classes to finish initialization.
	 * 
	 * @param stockTickerSymbol
	 *            the ticker symbol of the stock this instance is manage orders for
	 */
	protected OrderManagerImpl(String stockTickerSymbol) {
		this.stockTickerSymbol = stockTickerSymbol;
	}

	/**
	 * Constructor.
	 * 
	 * @param stockTickerSymbol
	 *            the ticker symbol of the stock this instance is manage orders for
	 * @param price
	 *            the current price of stock to be managed
	 */
	public OrderManagerImpl(String stockTickerSymbol, int price) {
		this(stockTickerSymbol);

		// Setup BUY ORDER queue
		stopBuyOrderQueue = new OrderQueueImpl<>(0, (t, o) -> o.getPrice() <= t,
				Comparator.comparing(StopBuyOrder::getPrice).thenComparing(StopBuyOrder::compareTo));

		// Setup SELL ORDER queue
		stopSellOrderQueue = new OrderQueueImpl<>(0, (t, o) -> o.getPrice() >= t,
				Comparator.comparing(StopSellOrder::getPrice).reversed().thenComparing(StopSellOrder::compareTo));

		stopBuyOrderQueue.setThreshold(price);
		stopSellOrderQueue.setThreshold(price);
	}

	/**
	 * Gets the stock ticker symbol for the stock managed by this stock manager.
	 * 
	 * @return the stock ticker symbol
	 */
	@Override
	public String getSymbol() {
		return stockTickerSymbol;
	}

	/**
	 * Respond to a stock price adjustment by setting threshold on dispatch filters.
	 * 
	 * @param price
	 *            the new price
	 */
	@Override
	public void adjustPrice(int price) {
		stopBuyOrderQueue.setThreshold(price);
		stopSellOrderQueue.setThreshold(price);
	}

	/**
	 * Queue a stop buy order.
	 * 
	 * @param order
	 *            the order to be queued
	 */
	@Override
	public void queueOrder(StopBuyOrder order) {
		stopBuyOrderQueue.enqueue(order);
	}

	/**
	 * Queue a stop sell order.
	 * 
	 * @param order
	 *            the order to be queued
	 */
	@Override
	public void queueOrder(StopSellOrder order) {
		stopSellOrderQueue.enqueue(order);
	}

	/**
	 * Registers the processor to be used during buy order processing. This will be
	 * passed on to the order queues as the dispatch callback.
	 * 
	 * @param processor
	 *            the callback to be registered
	 */
	@Override
	public void setBuyOrderProcessor(Consumer<StopBuyOrder> processor) {
		stopBuyOrderQueue.setOrderProcessor(processor);
	}

	/**
	 * Registers the processor to be used during sell order processing. This will be
	 * passed on to the order queues as the dispatch callback.
	 * 
	 * @param processor
	 *            the callback to be registered
	 */
	@Override
	public void setSellOrderProcessor(Consumer<StopSellOrder> processor) {
		stopSellOrderQueue.setOrderProcessor(processor);
	}
}
