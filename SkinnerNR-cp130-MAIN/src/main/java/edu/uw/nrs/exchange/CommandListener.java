package edu.uw.nrs.exchange;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

	/** The command server socket. */
	private ServerSocket commandSock;

	/** Executor used to execute the client requests */
	private ExecutorService executor = Executors.newCachedThreadPool();

	/** If set to false the listener will terminate. */
	public boolean listening = true;

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
	}

	/**
	 * Accept connections, and creates a CommandExecutor for servicing the
	 * connection.
	 */
	@Override
	public void run() {
		log.debug("Shhh... listening for commands on port " + commandPort);
		try (ServerSocket listenSocket = new ServerSocket(commandPort)) {
			commandSock = listenSocket;
			log.debug("Server Socket hearing =" + listening);
			while (listening) {
				Socket clientSocket = null;

				try {
					log.debug("waiting for a connection...");
					clientSocket = commandSock.accept();
					log.debug(String.format("CONNECTED to address [%s] on port [%d]",
							clientSocket.getInetAddress().toString(), clientSocket.getPort()));
				} catch (final SocketException e) {
					if (commandSock != null && !commandSock.isClosed()) {
						log.error("Problem encountered while trying to accept the client socket.", e);
					}
				}

				if (clientSocket == null) {
					continue;
				}

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
		listening = false;

		try {
			if (commandSock != null && !commandSock.isClosed()) {
				commandSock.close();
			}
			try {
				commandSock.close();
				if (!executor.isShutdown()) {
					executor.shutdownNow();
					executor.awaitTermination(1l, TimeUnit.SECONDS);
				}

			} catch (IOException e) {
				log.error("Error closing server socket. " + e);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			log.error("Error closing server socket. " + e);
		}
	}
}
