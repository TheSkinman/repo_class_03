package edu.uw.nrs.account;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountFactory;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.dao.AccountDao;

/**
 * Interface for managing accounts. Provides interfaces for the basic account
 * operations; create, delete, authentication and persistence.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public class AccountManagerImpl implements AccountManager {
	private static Logger log = LoggerFactory.getLogger(AccountManagerImpl.class.getName());
	private static final String ENCODING = "ISO-8859-1";
	private static final String ALGORITHM = "SHA-256";

	private AccountDao dao;
	private AccountFactory accountFactory;

	public AccountManagerImpl(AccountDao dao) {
		this.dao = dao;
		this.accountFactory = new AccountFactoryImpl();
	}

	/**
	 * Used to persist an account.
	 * 
	 * @param account
	 *            the account to persist
	 * @throws AccountException
	 *             if operation fails
	 */
	@Override
	public void persist(Account account) throws AccountException {
		dao.setAccount(account);
	}

	/**
	 * Lookup an account based on account name.
	 * 
	 * @param accountName
	 *            the name of the desired account
	 * 
	 * @return the account if located otherwise null
	 * 
	 * @throws AccountException
	 *             if operation fails
	 */
	@Override
	public Account getAccount(String accountName) throws AccountException {
		Account account = dao.getAccount(accountName);
		if (account == null) {
			return null;
		}
		account.registerAccountManager(this);
		return account;
	}

	/**
	 * Remove the account.
	 * 
	 * @param accountName
	 *            the name of the account to remove
	 * 
	 * @throws AccountException
	 *             if operation fails
	 */
	@Override
	public void deleteAccount(String accountName) throws AccountException {
		dao.deleteAccount(accountName);
	}

	/**
	 * Creates an account. The creation process should include persisting the
	 * account and setting the account manager reference (through the Account
	 * registerAccountManager operation).
	 * 
	 * @param accountName
	 *            the name for account to add
	 * @param password
	 *            the password used to gain access to the account
	 * @param balance
	 *            the initial balance of the account
	 * @return the newly created account
	 * @throws AccountException
	 *             if operation fails
	 */
	@Override
	public Account createAccount(String accountName, String password, int balance) throws AccountException {
		Account account = dao.getAccount(accountName);
		if (account != null) {
			throw new AccountException("The account \"" + accountName + "\" already exists. unable to create account.");
		}
		account = accountFactory.newAccount(accountName, hashPassword(password), balance);
		account.registerAccountManager(this);
		dao.setAccount(account);

		return account;
	}

	/**
	 * Check whether a login is valid. An account must exist with the account name
	 * and the password must match.
	 * 
	 * @param accountName
	 *            name of account the password is to be validated for
	 * @param password
	 *            password is to be validated
	 * @return true if password is valid for account identified by accountName
	 * @throws AccountException
	 *             if error occurs accessing accounts
	 */
	@Override
	public boolean validateLogin(String accountName, String password) throws AccountException {
		Account account = dao.getAccount(accountName);
		if (account == null) {
			log.info("Account \"" + accountName + "\" was not located.");
			return false;
		}

		boolean valid = MessageDigest.isEqual(account.getPasswordHash(), hashPassword(password));
		return valid;
	}

	private byte[] hashPassword(String password) throws AccountException {
		byte[] digest = null;
		try {
			final MessageDigest md = MessageDigest.getInstance(ALGORITHM);
			md.update(password.getBytes(ENCODING));
			digest = md.digest();
		} catch (NoSuchAlgorithmException ex) {
			throw new AccountException("Cannot use the \"" + ALGORITHM + "\" algorithm.", ex);
		} catch (UnsupportedEncodingException ex) {
			throw new AccountException("The \"" + ENCODING + "\" encoding is not supported.", ex);
		}
		return digest;
	}

	/**
	 * Release any resources used by the AccountManager implementation. Once closed
	 * further operations on the AccountManager may fail.
	 * 
	 * @throws AccountException
	 *             if error occurs accessing accounts
	 */
	@Override
	public void close() throws AccountException {
		dao.close();
		dao = null;
	}

}
