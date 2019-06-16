package edu.uw.nrs.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * These methods are a grouping of useful code for working with KeyStores for
 * cryptography.
 * 
 * @author Norman Skinner (skinman@uw.edu)
 *
 */
public class CryptoUtils {
	private static final String AES = "AES";

	/** KeyStore Type formats as a constant */
	public static final String JKS = "JKS";
	public static final String JCEKS = "JCEKS";

	/**
	 * 
	 * @param storeType
	 *            The type of KeyStore. Commonly "JKS" or "JCEKS".
	 * @param storePasswd
	 *            The password to access the KeyStore.
	 * @param inputStream
	 *            The input stream to the KeyStore.
	 * @return The loaded KeyStore for use.
	 * @throws KeyStoreException
	 *             If the KeyStore exception occurs.
	 * @throws NoSuchAlgorithmException
	 *             The selected algorithm was not recognized.
	 * @throws CertificateException
	 *             An issue with the certificate was encountered.
	 * @throws IOException
	 *             There was an IO exception encountered.
	 */
	public static KeyStore loadKeyStore(String storeType, char[] storePasswd, InputStream inputStream)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore keyStore = KeyStore.getInstance(storeType);
		keyStore.load(inputStream, storePasswd);
		return keyStore;
	}

	/**
	 * Loads a KeyStore given the file path, KeyStore type, and the KeyStore
	 * password.
	 * 
	 * @param storeFile
	 *            The KeyStore file path.
	 * @param storeType
	 *            The type of KeyStore. Commonly "JKS" or "JCEKS".
	 * @param storePasswd
	 *            The password to access the KeyStore.
	 * @return The loaded KeyStore for use.
	 * @throws KeyStoreException
	 *             If the KeyStore exception occurs.
	 * @throws NoSuchAlgorithmException
	 *             The selected algorithm was not recognized.
	 * @throws CertificateException
	 *             An issue with the certificate was encountered.
	 * @throws IOException
	 *             There was an IO exception encountered.
	 */
	public static KeyStore loadKeyStore(String storeFile, String storeType, char[] storePasswd)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		try (InputStream stream = CryptoUtils.class.getClassLoader().getResourceAsStream(storeFile)) {
			return loadKeyStore(storeType, storePasswd, stream);
		}
	}

	/**
	 * Generates a SecretKey at 128 bit and AES format.
	 * 
	 * @return A secret key.
	 * @throws NoSuchAlgorithmException
	 *             If the "AES" format is not found.
	 */
	public static SecretKey generateAesSecretKey() throws NoSuchAlgorithmException {
		KeyGenerator generator = KeyGenerator.getInstance(AES);
		generator.init(128);
		SecretKey key = generator.generateKey();
		return key;
	}

	/**
	 * Takes a byte array format of a key and converts it back to a SecretKey.
	 * 
	 * @param key
	 *            The SecretKey in a encoded byte array format.
	 * @return The key back in a SecretKey format.
	 * @throws NoSuchAlgorithmException
	 *             If the selected "AES" format is not found.
	 */
	public static SecretKey keyBytesToAesSecretKey(final byte[] key) throws NoSuchAlgorithmException {
		SecretKey secKey = new SecretKeySpec(key, 0, 16, AES);
		return secKey;
	}
}
