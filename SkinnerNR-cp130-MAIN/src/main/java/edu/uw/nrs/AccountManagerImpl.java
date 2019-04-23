package edu.uw.nrs;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

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
	private static Logger logger = LoggerFactory.getLogger(AccountManagerImpl.class.getName());
	private static final String ENCODING = "ISO-8859-1";
	private static final String  ALGORITHM = "SHA-256";

	private AccountDao dao;
	private AccountFactory acctfact;
	
	public AccountManagerImpl(AccountDao dao) {
		this.dao = dao;
		setup account factory
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
		// TODO Auto-generated method stub
		//check for account manager in account
		return null;
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
		// TODO Auto-generated method stub

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
		
		try to get account / if null ok
		
		1. create accoun
		2. register accMgr
		3. presist
		
		return null;
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
		// TODO Auto-generated method stub
		1. get account
		2.
		valid = MessageDigest.isEqual(acct.getpasswordHash, passwordHash)
		return false;
	}

	private byte[] hashPassword(String password) {
		byte[] digest = null;
		try {
			final MessageDigest md = MessageDigest.getInstance(ALGORITHM);
			md.update(password.getBytes(ENCODING));
			digest = md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
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
