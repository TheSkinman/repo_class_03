package edu.uw.nrs.account;

import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.account.AccountManagerFactory;
import edu.uw.ext.framework.dao.AccountDao;

/**
 * Factory interface for the creation of account managers. Implementations of
 * this class must provide a no argument constructor.
 * 
 * @author Norman Skinner (skinman@uw.edu) - DONE
 *
 */
public class AccountManagerFactoryImpl implements AccountManagerFactory {

	/**
	 * Instantiates a new account manager instance.
	 * 
	 * @param dao
	 *            the data access object to be used by the account manager
	 * @return a newly instantiated account manager
	 */
	@Override
	public AccountManager newAccountManager(AccountDao dao) {
		return new AccountManagerImpl(dao);
	}
}
