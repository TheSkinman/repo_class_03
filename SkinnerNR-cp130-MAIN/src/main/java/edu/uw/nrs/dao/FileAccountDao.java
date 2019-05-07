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

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
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
public class FileAccountDao implements AccountDao {
	private static final Logger log = LoggerFactory.getLogger(FileAccountDao.class.getName());

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
		final String accountDirectory = ACCT_DIR + "/" + accountName;
		File inFile = new File(accountDirectory);
		if (!inFile.exists()) {
			log.error("Account for \"{}\" does not exist.", accountName);
			return null;
		}

		CreditCardImpl creditCard = null;
		AddressImpl address = null;
		AccountImpl account = null;

		// Load the "creditCard" file
		inFile = new File(accountDirectory, accountName + "_creditcard.dat");
		if (inFile.exists()) {
			creditCard = new CreditCardImpl();
			if (null != creditCard) {
				try (DataInputStream din = new DataInputStream(new FileInputStream(inFile))) {
					creditCard.setAccountNumber(safeString(din.readUTF()));
					creditCard.setExpirationDate(safeString(din.readUTF()));
					creditCard.setHolder(safeString(din.readUTF()));
					creditCard.setIssuer(safeString(din.readUTF()));
					creditCard.setType(safeString(din.readUTF()));
				} catch (IOException ex) {
					log.error("IO Exception saving credit card data", ex);
					creditCard = null;
				}
			}
		}

		// Load the "address" file
		inFile = new File(accountDirectory, accountName + "_address.dat");
		if (inFile.exists()) {
			address = new AddressImpl();
			if (null != address) {
				try (DataInputStream din = new DataInputStream(new FileInputStream(inFile))) {
					address.setCity(safeString(din.readUTF()));
					address.setState(safeString(din.readUTF()));
					address.setStreetAddress(safeString(din.readUTF()));
					address.setZipCode(safeString(din.readUTF()));
				} catch (IOException ex) {
					log.error("IO Exception saving address data", ex);
					address = null;
				}
			}
		}

		// Load the "account" file
		inFile = new File(accountDirectory, accountName + "_account.dat");
		if (inFile.exists()) {
			account = new AccountImpl();
			if (null != account) {
				try (DataInputStream din = new DataInputStream(new FileInputStream(inFile))) {
					int passwordLength = din.readInt();
					if (passwordLength > 0) {
						byte[] readPassword = new byte[passwordLength];
						din.readFully(readPassword);
						account.setPasswordHash(readPassword);
					}
					account.setBalance(din.readInt());
					account.setFullName(safeString(din.readUTF()));
					account.setPhone(safeString(din.readUTF()));
					account.setEmail(safeString(din.readUTF()));
					account.setName(safeString(din.readUTF()));
				} catch (AccountException ex) {
					log.error("IO Exception saving account data", ex);
					account = null;
				} catch (IOException ex) {
					log.error("IO Exception saving account data", ex);
					account = null;
				}
			}
		}
		account.setAddress(address);
		account.setCreditCard(creditCard);

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
		final String accountDirectory = ACCT_DIR + "/" + account.getName();
		prepareAccountDirectory(accountDirectory);
		File outFile = null;

		// Save the "account" file
		outFile = new File(accountDirectory, account.getName() + "_account.dat");
		try (DataOutputStream dout = new DataOutputStream(new FileOutputStream(outFile))) {
			if (account.getPasswordHash() == null) {
				dout.writeInt(-1);
			} else {
				dout.writeInt(account.getPasswordHash().length);
				for (byte b : account.getPasswordHash()) {
					dout.write(b);
				}
			}
			dout.writeInt(account.getBalance());
			dout.writeUTF(safeString(account.getFullName()));
			dout.writeUTF(safeString(account.getPhone()));
			dout.writeUTF(safeString(account.getEmail()));
			dout.writeUTF(safeString(account.getName()));
		} catch (FileNotFoundException ex) {
			log.error("Cannot Open the Output File", ex);
			throw new AccountException("Cannot Open the Output File");
		} catch (IOException ex) {
			log.error("IO Exception saving account data", ex);
			throw new AccountException("Cannot Open the Output File");
		}

		// Save the "address" file
		final AddressImpl address = (AddressImpl) account.getAddress();
		outFile = new File(accountDirectory, account.getName() + "_address.dat");
		if (null != address) {
			try (DataOutputStream dout = new DataOutputStream(new FileOutputStream(outFile))) {
				dout.writeUTF(safeString(address.getCity()));
				dout.writeUTF(safeString(address.getState()));
				dout.writeUTF(safeString(address.getStreetAddress()));
				dout.writeUTF(safeString(address.getZipCode()));
			} catch (FileNotFoundException ex) {
				log.error("Cannot Open the Output File", ex);
				throw new AccountException("Cannot Open the Output File");
			} catch (IOException ex) {
				log.error("IO Exception saving address data", ex);
				throw new AccountException("IO Exception saving address data");
			}
		} else if (outFile.exists()) {
			deleteDirectory(outFile);
		}

		// Save the "creditCard" file
		final CreditCardImpl creditCard = (CreditCardImpl) account.getCreditCard();
		outFile = new File(accountDirectory, account.getName() + "_creditcard.dat");
		if (null != creditCard) {
			try (DataOutputStream dout = new DataOutputStream(new FileOutputStream(outFile))) {
				dout.writeUTF(safeString(creditCard.getAccountNumber()));
				dout.writeUTF(safeString(creditCard.getExpirationDate()));
				dout.writeUTF(safeString(creditCard.getHolder()));
				dout.writeUTF(safeString(creditCard.getIssuer()));
				dout.writeUTF(safeString(creditCard.getType()));
			} catch (FileNotFoundException ex) {
				log.error("Cannot Open the Output File", ex);
				throw new AccountException("Cannot Open the Output File");
			} catch (IOException ex) {
				log.error("IO Exception saving credit card data", ex);
				throw new AccountException("IO Exception saving credit card data");
			}
		} else if (outFile.exists()) {
			deleteDirectory(outFile);
		}

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
		final File serverDir = new File(ACCT_DIR + "/" + accountName);
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

	private String safeString(final String string) {
		// Convert null to a writable null.
		if (string == null) { return "<null>"; }
		 
		// Convert read null to real null.
		if (string.equals("<null>")) { return null; }
		 
		// Just return the string as is.
		return string;
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
