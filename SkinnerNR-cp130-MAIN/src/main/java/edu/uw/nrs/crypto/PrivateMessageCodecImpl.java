package edu.uw.nrs.crypto;

import java.io.IOException;
import java.security.GeneralSecurityException;

import edu.uw.ext.framework.crypto.PrivateMessageCodec;
import edu.uw.ext.framework.crypto.PrivateMessageTriple;

/**
 * Implementation of PrivateMessageCodec that uses a 128 bit AES key to encrypt
 * the data. All keystores are of the JCEKS type.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public class PrivateMessageCodecImpl implements PrivateMessageCodec {

	/** Constructor. */
	public PrivateMessageCodecImpl() {
	}

	/**
	 * Enciphers the provided data. Key stores will be accessed as resources, i.e.
	 * on the classpath.
	 *
	 * @param plaintext
	 *            the data to be encrypted
	 * @param senderKeyStoreName
	 *            the name of the sender's key store resource
	 * @param senderKeyStorePasswd
	 *            the sender's key store password
	 * @param senderKeyName
	 *            the alias of the sender's private key
	 * @param senderKeyPasswd
	 *            the password for the sender's private key
	 * @param senderTrustStoreName
	 *            the name of the sender's trust key store resource
	 * @param senderTrustStorePasswd
	 *            the sender's trust store key
	 * @param recipientCertName
	 *            the alias of the recipient's certificate key
	 * @return message containing the ciphertext, key and signature
	 * @throws GeneralSecurityException
	 *             if any cryptographic operations fail
	 * @throws IOException
	 *             if unable to write either of the files
	 */
	@Override
	public PrivateMessageTriple encipher(byte[] plaintext, String senderKeyStoreName, char[] senderKeyStorePasswd,
			String senderKeyName, char[] senderKeyPasswd, String senderTrustStoreName, char[] senderTrustStorePasswd,
			String recipientCertName) throws GeneralSecurityException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Decipher the provided message. Keystores will be accessed as resources, i.e.
	 * on the classpath.
	 * 
	 * @param triple
	 *            the message containing the ciphertext, key and signature
	 * @param recipientKeyStoreName
	 *            the name of the recipient's key store resource
	 * @param recipientKeyStorePasswd
	 *            the recipient's key store password
	 * @param recipientKeyName
	 *            the alias of the recipient's private key
	 * @param recipientKeyPasswd
	 *            the password for the recipient's private key
	 * @param trustStoreName
	 *            the name of the trust store resource
	 * @param trustStorePasswd
	 *            the trust store password
	 * @param signerCertName
	 *            the name of the signer's certificate
	 * @return the plaintext from the file
	 * @throws GeneralSecurityException
	 *             - if any cryptographic operations fail
	 * @throws IOException
	 *             - if unable to write either of the files
	 */
	@Override
	public byte[] decipher(PrivateMessageTriple triple, String recipientKeyStoreName, char[] recipientKeyStorePasswd,
			String recipientKeyName, char[] recipientKeyPasswd, String trustStoreName, char[] trustStorePasswd,
			String signerCertName) throws GeneralSecurityException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
