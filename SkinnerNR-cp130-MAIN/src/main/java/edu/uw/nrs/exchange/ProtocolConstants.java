package edu.uw.nrs.exchange;

/**
 * Constants for the command strings composing the exchange protocol. The
 * protocol supports events and commands. <br>
 * Events are one way messages sent from the exchange to the broker(s). <br>
 * The protocol supports the following events: <br>
 * Event: [OPEN_EVNT]<br>
 * - <br>
 * Event: [CLOSED_EVNT] <br>
 * - <br>
 * Event: [PRICE_CHANGE_EVNT][ELEMENT_DELIMITER]symbol[ELEMENT_DELIMITER]price
 * <br>
 * <br>
 * Commands conform to a request/response model where requests are sent from a
 * broker and the result is a response sent to the requesting broker from the
 * exchange. <br>
 * The protocol supports the following commands: <br>
 * <p>
 * Request: [GET_STATE_CMD] <br>
 * Response: [OPEN_STATE]|[CLOSED_STATE] <br>
 * - <br>
 * Request: [GET_TICKERS_CMD] <br>
 * Response: symbol[ELEMENT_DELIMITER]symbol... <br>
 * - <br>
 * Request: [GET_QUOTE_CMD][ELEMENT_DELIMITER]symbol <br>
 * Response: price | INVALID_STOCK <br>
 * - <br>
 * Request: [EXECUTE_TRADE_CMD][ELEMENT_DELIMITER][BUY_ORDER]|[SELL_ORDER]<br>
 * [ELEMENT_DELIMITER]account_id[ELEMENT_DELIMITER]<br>
 * symbol[ELEMENT_DELIMITER]shares <br>
 * Response: execution_price
 * </p>
 * <br>
 * <br>
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public final class ProtocolConstants extends Object {

	/** Identifies an order as a buy order. */
	public static final String BUY_ORDER = "BUY_ORDER";

	/** Event indicating the exchange has closed. */
	public static final String CLOSED_EVNT = "CLOSED_EVNT";

	/** Indicates the exchange is closed. */
	public static final String CLOSED_STATE = "CLOSED";

	/** The index of the command element. */
	public static final int CMD_ELEMENT = 0;

	/** The character used to separate elements in the protocol. */
	public static final String ELEMENT_DELIMITER = ":";

	/** Character encoding to use. */
	public static final String ENCODING = "ISO-8859-1";

	/** The index of the event type element. */
	public static final int EVENT_ELEMENT = 0;

	/** A request to execute a trade. */
	public static final String EXECUTE_TRADE_CMD = "EXECUTE_TRADE_CMD";

	/** The index of the account id element in the execute trade command. */
	public static final int EXECUTE_TRADE_CMD_ACCOUNT_ELEMENT = 2;

	/** The index of the shares element in the execute trade command. */
	public static final int EXECUTE_TRADE_CMD_SHARES_ELEMENT = 4;

	/** The index of the ticker element in the execute trade command. */
	public static final int EXECUTE_TRADE_CMD_TICKER_ELEMENT = 3;

	/** The index of the order type element in the execute trade command. */
	public static final int EXECUTE_TRADE_CMD_TYPE_ELEMENT = 1;

	/** A request for a stock price quote. */
	public static final String GET_QUOTE_CMD = "GET_QUOTE_CMD";

	/** A request for the exchange's state. */
	public static final String GET_STATE_CMD = "GET_STATE_CMD";

	/** A request for the ticker symbols for the traded stocks. */
	public static final String GET_TICKERS_CMD = "GET_TICKERS_CMD";

	/** The invalid stock price - indicates stock is not on the exchange. */
	public static final int INVALID_STOCK = -1;

	/** Event indicating the exchange has opened. */
	public static final String OPEN_EVNT = "OPEN_EVNT";

	/** Indicates the exchange is open. */
	public static final String OPEN_STATE = "OPEN";

	/** Event indicating a stock price has changed. */
	public static final String PRICE_CHANGE_EVNT = "PRICE_CHANGE_EVNT";

	/** The index of the price element. */
	public static final int PRICE_CHANGE_EVNT_PRICE_ELEMENT = 2;

	/** The index of the ticker element. */
	public static final int PRICE_CHANGE_EVNT_TICKER_ELEMENT = 1;

	/** The index of the ticker element in the price quote command. */
	public static final int QUOTE_CMD_TICKER_ELEMENT = 1;

	/** Identifies an order as a sell order. */
	public static final String SELL_ORDER = "SELL_ORDER";

}
