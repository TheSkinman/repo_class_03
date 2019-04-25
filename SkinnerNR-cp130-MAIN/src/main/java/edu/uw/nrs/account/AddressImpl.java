package edu.uw.nrs.account;

import edu.uw.ext.framework.account.Address;

/**
 * Interface for a pure JavaBean implementation of an address. The implementing
 * class must provide a no argument constructor.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public class AddressImpl implements Address {
	private static final long serialVersionUID = -1710511715791093745L;

	private String city;
	private String state;
	private String streetAddress;
	private String zip;

	public AddressImpl() {
	}

	/**
	 * Gets the street address.
	 * 
	 * @return the street address
	 */
	@Override
	public String getStreetAddress() {
		return streetAddress;
	}

	/**
	 * Sets the street address.
	 * 
	 * @param streetAddress
	 *            the street address
	 */
	@Override
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	/**
	 * Gets the city.
	 * 
	 * @return the city
	 */
	@Override
	public String getCity() {
		return city;
	}

	/**
	 * Sets the city.
	 * 
	 * @param city
	 *            the city
	 */
	@Override
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Gets the state.
	 * 
	 * @return the state
	 */
	@Override
	public String getState() {
		return state;
	}

	/**
	 * Sets the state.
	 * 
	 * @param state
	 *            the state
	 */
	@Override
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Gets the ZIP code.
	 * 
	 * @return the ZIP code
	 */
	@Override
	public String getZipCode() {
		return zip;
	}

	/**
	 * Sets the ZIP code.
	 * 
	 * @param zip
	 *            the ZIP code
	 */
	@Override
	public void setZipCode(String zip) {
		this.zip = zip;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s%n%s, %s %s", streetAddress, city, state, zip);
	}

}
