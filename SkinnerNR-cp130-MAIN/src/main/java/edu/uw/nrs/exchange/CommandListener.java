package edu.uw.nrs.exchange;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.exchange.StockExchange;

/**
 * Accepts connections and passes them to a CommandHandler, for the reading and
 * processing of commands.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public class CommandListener implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(CommandListener.class.getName());

	/** The "real" exchange to be used to execute the commands */
	private StockExchange realExchange;

	/** The port to listen for connections on */
	private int commandPort;

	/**  */
	private ServerSocket commandSock;
	
	/** Number of threads to use in the executor pool */
	private int MAX_T = 10;

	/** */
	private ExecutorService executor = Executors.newFixedThreadPool(MAX_T);

	/** If set to false the listener will terminate. */
	public boolean listening;

	/**
	 * Constructor.
	 * 
	 * @param commandPort
	 *            the port to listen for connections on
	 * @param realExchange
	 *            the "real" exchange to be used to execute the commands
	 */
	public CommandListener(final int commandPort, final StockExchange realExchange) {
		this.commandPort = commandPort;
		this.realExchange = realExchange;
		listening = true;
	}

	/**
	 * Accept connections, and creates a CommandExecutor for servicing the
	 * connection.
	 */
	@Override
	public void run() {
		log.info("Shhh... listening for commands on port " + commandPort);
		try (ServerSocket listenSocket = new ServerSocket(commandPort)) {
			commandSock = listenSocket;
			log.info("11323 hearing=" + listening);
			while (listening) {
				Socket clientSocket = null;

				try {
					log.info("waiting for a connection...");
					clientSocket = commandSock.accept();
					//log.info("Now connected to: {}:{}", clientSocket.getLocalAddress(), clientSocket.getLocalPort());
				} catch (final SocketException e) {
					if (commandSock != null && !commandSock.isClosed()) {
						log.error("Problem encountered while trying to accept the client socket.", e);
					}
				}
				
				if (clientSocket == null) {
					continue;
				}

				// Hand work off to an enslaved thread that must do our bidding less be punished
				log.warn("fire exe");
				
				//new Thread(new CommandHandler(clientSocket, realExchange)).run();
				executor.execute(new CommandHandler(clientSocket, realExchange));
			}
		} catch (IOException ex) {
			log.error("Server error: " + ex);
		} finally {
			terminate();
		}
	}

	/**
	 * Terminates this thread gracefully.
	 */
	public void terminate() {
		if (commandSock != null) {
			try {
				commandSock.close();
			} catch (IOException ioex) {
				log.error("Error closing server socket. " + ioex);
			}
		}
	}
}
