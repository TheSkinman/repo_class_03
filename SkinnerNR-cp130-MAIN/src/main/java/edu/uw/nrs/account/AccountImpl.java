package edu.uw.nrs.account;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.account.Address;
import edu.uw.ext.framework.account.CreditCard;
import edu.uw.ext.framework.order.Order;

/**
 * A pure JavaBean representation of an account.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
@SuppressWarnings("serial")
public class AccountImpl implements Account {
	private static Logger logger = LoggerFactory.getLogger(AccountImpl.class.getName());
	private static final int MIN_ACCT_BALANCE = 100_000;
	private static final int MIN_ACCT_NAME_LENGTH = 8;
	private String name;
	private byte[] passwordHash;
	private int balance;
	private String fullName;
	private AddressImpl address;
	private String phone;
	private String email;
	private CreditCardImpl creditCard;
	private AccountManager accountManager;

	/**
	 * Required by JavaBean
	 */
	public AccountImpl() {
	}

	public AccountImpl(final String acctName, final byte[] passwordHash, final int balance) throws AccountException {
		if (isBlank(acctName) || acctName.length() < MIN_ACCT_NAME_LENGTH) {
			throw new AccountException("Account Name can not be NULL, or less that 8 characters.");
		}

		if (balance < MIN_ACCT_BALANCE) {
			throw new AccountException(
					"Account initial balance violation, it must be 100000 pennies or more. Unable to generate an account.");
		}

		name = acctName;
		this.balance = balance;
		this.passwordHash = passwordHash;
	}

	/**
	 * Get the account name.
	 * 
	 * @return the name of the account
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Sets the account name. This operation is not generally used but is provided
	 * for JavaBean conformance.
	 * 
	 * @param acctName
	 *            the value to be set for the account name
	 * @throws AccountException
	 *             if the account name is unacceptable
	 */
	@Override
	public void setName(String acctName) throws AccountException {
		if (isBlank(acctName) || acctName.length() < MIN_ACCT_NAME_LENGTH) {
			throw new AccountException("Account Name can not be NULL, or less that 8 characters.");
		}
		name = acctName;
	}

	/**
	 * Gets the hashed password.
	 * 
	 * @return the hashed password
	 */
	@Override
	public byte[] getPasswordHash() {
		return Arrays.copyOf(passwordHash, passwordHash.length);
	}

	/**
	 * Sets the hashed password.
	 * 
	 * @param passwordHash
	 *            the value to be set for the password hash
	 */
	@Override
	public void setPasswordHash(byte[] passwordHash) {
		this.passwordHash = Arrays.copyOf(passwordHash, passwordHash.length);
	}

	/**
	 * Gets the account balance, in cents.
	 * 
	 * @return the current balance of the account
	 */
	@Override
	public int getBalance() {
		return balance;
	}

	/**
	 * Sets the account balance.
	 * 
	 * @param balance
	 *            the value to set the balance to in cents
	 */
	@Override
	public void setBalance(int balance) {
		this.balance = balance;
	}

	/**
	 * Gets the full name of the account holder.
	 * 
	 * @return the account holders full name
	 */
	@Override
	public String getFullName() {
		return fullName;
	}

	/**
	 * Sets the full name of the account holder.
	 * 
	 * @param fullName
	 *            the account holders full name
	 */
	@Override
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * Gets the account address.
	 * 
	 * @return the accounts address
	 */
	@Override
	public Address getAddress() {
		return address;
	}

	/**
	 * Sets the account address.
	 * 
	 * @param address
	 *            the address for the account
	 */
	@Override
	public void setAddress(Address address) {
		this.address = (AddressImpl) address;
	}

	/**
	 * Gets the phone number.
	 * 
	 * @return the phone number
	 */
	@Override
	public String getPhone() {
		return phone;
	}

	/**
	 * Sets the account phone number.
	 * 
	 * @param phone
	 *            value for the account phone number
	 */
	@Override
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * Gets the email address.
	 * 
	 * @return the email address
	 */
	@Override
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the account email address.
	 * 
	 * @param email
	 *            the email address
	 */
	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the account credit card.
	 * 
	 * @return the credit card
	 */
	@Override
	public CreditCard getCreditCard() {
		return creditCard;
	}

	/**
	 * Sets the account credit card.
	 * 
	 * @param card
	 *            the value to be set for the credit card
	 */
	@Override
	public void setCreditCard(CreditCard card) {
		this.creditCard = (CreditCardImpl) card;
	}

	/**
	 * Sets the account manager responsible for persisting/managing this account.
	 * This may be invoked exactly once on any given account, any subsequent
	 * invocations should be ignored. The account manager member should not be
	 * serialized with implementing class object.
	 * 
	 * @param m
	 *            the account manager
	 */
	@Override
	public void registerAccountManager(AccountManager m) {
		if (accountManager == null) {
			accountManager = (AccountManagerImpl) m;
		} else {
			logger.warn("AccountManager has previously been set on this account.");
		}
	}

	/**
	 * Incorporates the effect of an order in the balance.
	 * 
	 * @param order
	 *            the order to be reflected in the account
	 * @param executionPrice
	 *            the price the order was executed at
	 */
	@Override
	public void reflectOrder(Order order, int executionPrice) {
		try {
			balance += order.valueOfOrder(executionPrice);
			if (accountManager != null) {
				accountManager.persist(this);
			}
		} catch (final AccountException ex) {
			logger.error("Unable to persist the account.", ex);
		}
	}
}
