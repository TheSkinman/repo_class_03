package edu.uw.nrs.broker;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.order.Order;

/**
 * A simple OrderQueue implementation backed by a TreeSet.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 * @param <E>
 *            the type of order contained in the queue
 * @param <T>
 *            the dispatch threshold type
 *
 */
public final class OrderQueueImpl<T, E extends Order> extends Object implements OrderQueue<T, E>, Runnable {
	private static final Logger log = LoggerFactory.getLogger(OrderQueueImpl.class.getName());

	private final ReentrantLock queueLock = new ReentrantLock();
	private final Condition dispatchCondition = queueLock.newCondition();
	private final ReentrantLock processorLock = new ReentrantLock();

	private Thread dispatchingThread;
	private TreeSet<E> queue;
	private T threshold;
	private BiPredicate<T, E> filter;
	private Consumer<E> orderProcessor;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            the name of the thread
	 * @param threshold
	 *            the initial threshold
	 * @param filter
	 *            the dispatch filter used to control dispatching from this queue
	 */
	public OrderQueueImpl(final String name, final T threshold, final BiPredicate<T, E> filter) {
		this(name, threshold, filter, Comparator.naturalOrder());
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            the name of the thread
	 * @param threshold
	 *            the initial threshold
	 * @param filter
	 *            the dispatch filter used to control dispatching from this queue
	 * @param cmp
	 *            Comparator to be used for ordering
	 */
	public OrderQueueImpl(final String name, final T threshold, final BiPredicate<T, E> filter,
			final Comparator<E> cmp) {
		queue = new TreeSet<>(cmp);
		this.threshold = threshold;
		this.filter = filter;
		StartupDispatchThread(name);
	}

	private void StartupDispatchThread(final String name) {
		dispatchingThread = new Thread(this, name + "-thread");
		dispatchingThread.setDaemon(true);
		dispatchingThread.start();
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
		queueLock.lock();
		try {
			if (queue.add(order)) {
				dispatchOrders();
			}
		} finally {
			queueLock.unlock();
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

		queueLock.lock();
		try {
			if (!queue.isEmpty()) {
				returnOrder = queue.first();

				if (filter.test(threshold, returnOrder)) {
					queue.remove(returnOrder);
				} else {
					returnOrder = null;
				}
			}
		} finally {
			queueLock.unlock();
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
		queueLock.lock();
		try {
			dispatchCondition.signal();

		} finally {
			queueLock.unlock();
		}
	}

	@Override
	public void run() {
		long startTime = 0l;

		while (true) {

			E order = null;
			queueLock.lock();
			try {
				while ((order = dequeue()) == null) {
					try {
						dispatchCondition.await();
					} catch (InterruptedException e) {
						log.error("Thread error BIG Time homie!", e);
					}
				}
			} finally {
				queueLock.unlock();
				startTime = System.currentTimeMillis();
			}

			processorLock.lock();
			try {
				if (orderProcessor != null) {
					orderProcessor.accept(order);
					long endTime = System.currentTimeMillis();
					long totalTime = (endTime - startTime);
					log.info("processThread [{}], processTime: {} ms.", Thread.currentThread().getName(), totalTime);
				}
			} finally {
				processorLock.unlock();
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
		processorLock.lock();
		try {
			this.orderProcessor = proc;
		} finally {
			processorLock.unlock();
		}
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
