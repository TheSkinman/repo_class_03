package edu.uw.nrs;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.dao.AccountDao;

/**
 * Defines the methods needed to store and load accounts from a persistent
 * storage mechanism. The implementing class must provide a no argument
 * constructor.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public class AccountDaoImpl implements AccountDao {

	/**
	 * Lookup an account in based on account name.
	 * 
	 * @param accountName
	 *            the name of the desired account
	 * @return the account if located otherwise null
	 */
	@Override
	public Account getAccount(String accountName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Adds or updates an account.
	 * 
	 * @param account
	 *            the account to add/update
	 * @throws AccountException
	 *             if operation fails
	 */
	@Override
	public void setAccount(Account account) throws AccountException {
		// TODO Auto-generated method stub

	}

	/**
	 * Remove the account.
	 * 
	 * @param accountName
	 *            the name of the account to be deleted
	 * @throws AccountException
	 *             if operation fails
	 */
	@Override
	public void deleteAccount(String accountName) throws AccountException {
		// TODO Auto-generated method stub

	}

	/**
	 * Remove all accounts. This is primarily available to facilitate testing.
	 * 
	 * @throws AccountException
	 *             if operation fails
	 */
	@Override
	public void reset() throws AccountException {
		// TODO Auto-generated method stub

	}

	/**
	 * Close the DAO. Release any resources used by the DAO implementation. If the
	 * DAO is already closed then invoking this method has no effect.
	 * 
	 * @throws AccountException
	 *             if operation fails
	 */
	@Override
	public void close() throws AccountException {
		// TODO Auto-generated method stub

	}
}
