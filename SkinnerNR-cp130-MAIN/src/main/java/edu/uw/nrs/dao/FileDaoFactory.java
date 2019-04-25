package edu.uw.nrs.dao;

import edu.uw.ext.framework.dao.AccountDao;
import edu.uw.ext.framework.dao.DaoFactory;
import edu.uw.ext.framework.dao.DaoFactoryException;

/**
 * An interface for factory that creates a family of DAO objects. There is
 * currently only a single member of the DAO family, the Account DAO.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public class FileDaoFactory implements DaoFactory {

	/**
	 * Instantiates a new AccountDao object.
	 * 
	 * @return a newly instantiated account DAO object
	 * 
	 * @throws DaoFactoryException
	 *             if unable to instantiate the DAO object
	 */
	@Override
	public AccountDao getAccountDao() throws DaoFactoryException {
		return new FileAccountDao();
	}
}
