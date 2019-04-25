package edu.uw.nrs.dao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.Address;
import edu.uw.ext.framework.account.CreditCard;
import edu.uw.ext.framework.dao.AccountDao;
import edu.uw.nrs.account.AccountImpl;
import edu.uw.nrs.account.AddressImpl;
import edu.uw.nrs.account.CreditCardImpl;

/**
 * Defines the methods needed to store and load accounts from a persistent
 * storage mechanism. The implementing class must provide a no argument
 * constructor.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public class JsonAccountDao implements AccountDao {
	private static final Logger log = LoggerFactory.getLogger(JsonAccountDao.class.getName());

	private static final String ACCT_DIR = "target/accounts";

	/**
	 * Lookup an account in based on account name.
	 * 
	 * @param accountName
	 *            the name of the desired account
	 * @return the account if located otherwise null
	 */
	@Override
	public Account getAccount(String accountName) {

		File inFile = new File(ACCT_DIR, accountName + ".json");
		if (!inFile.exists()) {
			log.error("Account for \"{}\" does not exist.", accountName);
			return null;
		}

		SimpleModule module = new SimpleModule();
		module.addAbstractTypeMapping(Account.class, AccountImpl.class);
		module.addAbstractTypeMapping(Address.class, AddressImpl.class);
		module.addAbstractTypeMapping(CreditCard.class, CreditCardImpl.class);
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(module);
		
		AccountImpl account = null;
		try {
			account = mapper.readValue(inFile, AccountImpl.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return account;
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

		// Save the "account" file
		File outFile = new File(ACCT_DIR, account.getName() + ".json");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		try {
			AccountImpl accountOut = (AccountImpl) account;
			mapper.writeValue(outFile, accountOut);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// } catch (FileNotFoundException ex) {
		// log.error("Cannot Open the Output File", ex);
		// throw new AccountException("Cannot Open the Output File");
		// } catch (IOException ex) {
		// log.error("IO Exception saving account data", ex);
		// throw new AccountException("Cannot Open the Output File");
		// }

	}

	private void prepareAccountDirectory(final String accountDirectory) {
		final File serverDir = new File(accountDirectory);
		if (!serverDir.exists()) {
			if (!serverDir.mkdirs()) {
				log.error("Failed to create directory:" + serverDir.getAbsolutePath());
			}
		}
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
		final File serverDir = new File(ACCT_DIR, accountName + ".json");
		deleteDirectory(serverDir);
	}

	/**
	 * Remove all accounts. This is primarily available to facilitate testing.
	 * 
	 * @throws AccountException
	 *             if operation fails
	 */
	@Override
	public void reset() throws AccountException {
		final File serverDir = new File(ACCT_DIR);
		deleteDirectory(serverDir);
		prepareAccountDirectory(ACCT_DIR);
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
		// no-op
	}

	private String safeWrite(final String input) {
		return input == null ? "<null>" : input;
	}

	private String safeRead(final String output) {
		return output.equals("<null>") ? null : output;
	}

	boolean deleteDirectory(File directory) {
		File[] allContents = directory.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}
		return directory.delete();
	}
}
