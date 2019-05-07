package edu.uw.nrs.broker;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple OrderQueue implementation backed by a TreeSet.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 * @param <E>
 * @param <T>
 *
 */
public final class OrderQueueImpl<T, E extends edu.uw.ext.framework.order.Order> extends Object
		implements edu.uw.ext.framework.broker.OrderQueue<T, E> {
	private static final Logger log = LoggerFactory.getLogger(OrderQueueImpl.class.getName());

	private TreeSet<E> queue;
	private T threshold;
	private BiPredicate<T, E> filter;
	private Consumer<E> orderProcessor;

	/**
	 * Constructor.
	 * 
	 * @param threshold
	 *            the initial threshold
	 * @param filter
	 *            the dispatch filter used to control dispatching from this queue
	 */
	public OrderQueueImpl(T threshold, BiPredicate<T, E> filter) {
		queue = new TreeSet<>();
		this.threshold = threshold;
		this.filter = filter;
	}

	/**
	 * Constructor.
	 * 
	 * @param threshold
	 *            the initial threshold
	 * @param filter
	 *            the dispatch filter used to control dispatching from this queue
	 * @param cmp
	 *            Comparator to be used for ordering
	 */
	public OrderQueueImpl(T threshold, BiPredicate<T, E> filter, Comparator<E> cmp) {
		queue = new TreeSet<>(cmp);
		this.threshold = threshold;
		this.filter = filter;
	}

	/**
	 * Adds the specified order to the queue. Subsequent to adding the order
	 * dispatches any dispatchable orders.
	 * 
	 * @param order
	 *            the order to be added to the queue
	 */
	@Override
	public void enqueue(E order) {
		if (queue.add(order)) {
			dispatchOrders();
		}
	}

	/**
	 * Removes the highest dispatchable order in the queue. If there are orders in
	 * the queue but they do not meet the dispatch threshold order will not be
	 * removed and null will be returned.
	 * 
	 * @return the first dispatchable order in the queue, or null if there are no
	 *         dispatchable orders in the queue
	 */
	@Override
	public E dequeue() {
		E returnOrder = null;

		if (!queue.isEmpty()) {
			returnOrder = queue.first();

			if (filter.test(threshold, returnOrder)) {
				queue.remove(returnOrder);
			} else {
				returnOrder = null;
			}
		}

		return returnOrder;
	}

	/**
	 * Executes the callback for each dispatchable order. Each dispatchable order is
	 * in turn removed from the queue and passed to the callback. If no callback is
	 * registered the order is simply removed from the queue.
	 * 
	 */
	@Override
	public void dispatchOrders() {
		E order = null;

		while ((order = dequeue()) != null) {
			if (orderProcessor != null) {
				orderProcessor.accept(order);
			}
		}
	}

	/**
	 * Registers the callback to be used during order processing.
	 * 
	 * @param proc
	 *            the callback to be registered
	 */
	@Override
	public void setOrderProcessor(Consumer<E> proc) {
		this.orderProcessor = proc;
	}

	/**
	 * Adjusts the threshold and dispatches orders.
	 * 
	 * @param threshold
	 *            the new threshold
	 */
	@Override
	public final void setThreshold(T threshold) {
		this.threshold = threshold;
		dispatchOrders();
	}

	/**
	 * Obtains the current threshold value.
	 * 
	 * @return the current threshold
	 */
	@Override
	public final T getThreshold() {
		return threshold;
	}
}
