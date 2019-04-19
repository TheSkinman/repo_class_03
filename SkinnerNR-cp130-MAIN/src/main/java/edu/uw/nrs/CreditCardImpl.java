package edu.uw.nrs;

import edu.uw.ext.framework.account.CreditCard;

/**
 * Interface for a pure JavaBean implementation of a credit card. The
 * implementing class must provide a no argument constructor.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public class CreditCardImpl implements CreditCard {
	private static final long serialVersionUID = -750099640402498043L;

	private String accountNumber;
	private String expDate;
	private String name;
	private String issuer;
	private String type;

	/**
	 * Gets the card issuer.
	 * 
	 * @return the card issuer
	 */
	@Override
	public String getIssuer() {
		return issuer;
	}

	/**
	 * Sets the card issuer.
	 * 
	 * @param issuer
	 *            the card issuer
	 */
	@Override
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	/**
	 * Gets the card type.
	 * 
	 * @return the card type
	 */
	@Override
	public String getType() {
		return type;
	}

	/**
	 * Sets the card type.
	 * 
	 * @param type
	 *            the card type
	 */
	@Override
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the card holder's name.
	 * 
	 * @return the card holders name
	 */
	@Override
	public String getHolder() {
		return name;
	}

	/**
	 * Sets the card holder's name.
	 * 
	 * @param name
	 *            the card holders name
	 */
	@Override
	public void setHolder(String name) {
		this.name = name;
	}

	/**
	 * Gets the card account number.
	 * 
	 * @return the account number
	 */
	@Override
	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * Sets the card account number.
	 * 
	 * @param accountNumber
	 *            the account number
	 */
	@Override
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * Gets the card expiration date.
	 * 
	 * @return the expiration date
	 */
	@Override
	public String getExpirationDate() {
		return expDate;
	}

	/**
	 * Sets the card expiration date.
	 * 
	 * @param expDate
	 *            the expiration date
	 */
	@Override
	public void setExpirationDate(String expDate) {
		this.expDate = expDate;
	}
}
