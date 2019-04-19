package edu.uw.nrs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountFactory;

/**
 * Factory interface for the creation of accounts. Implementations of this class
 * must provide a no argument constructor.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public class AccountFactoryImpl implements AccountFactory {
	private static final Logger log = LoggerFactory.getLogger(AccountFactoryImpl.class);
	
	/**
	 * Instantiates a new account instance.
	 * 
	 * @param accountName
	 *            the account name
	 * @param hashedPassword
	 *            the password hash
	 * @param initialBalance
	 *            the balance
	 * @return the newly instantiated account, or null if unable to instantiate the
	 *         account
	 */
	@Override
	public Account newAccount(String accountName, byte[] hashedPassword, int initialBalance) {
		AccountImpl account = new AccountImpl();
		try {
			account.setName(accountName);
		} catch (AccountException ex) {
			log.error("Account name violation, it must be a minimum of 8 characters. Unable to generate an account.", ex);
			return null;
		}
		
		if (initialBalance < 100000) {
			log.error("Account initial balance violation, it must be 100000 pennies or more. Unable to generate an account.");
			return null;
		}
		
		account.setPasswordHash(hashedPassword);
		account.setBalance(initialBalance);
		
		return account;
	}
}
