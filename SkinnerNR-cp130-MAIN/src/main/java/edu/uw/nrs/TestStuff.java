package edu.uw.nrs;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class TestStuff {

	public static void main(String[] args) {
		String password = "ABCD";
		
	    String passwordHash = hashPassword(password);
		
		System.out.println("password: " + passwordHash);

	}

	private static String hashPassword(String password) {
		String passwordHash=null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes());
			byte[] digest = md.digest();
			passwordHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return passwordHash;
	}

}
