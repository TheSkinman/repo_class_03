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

public class CryptoUtils {
	public static final String JCEKS = "JCEKS";

	public static KeyStore loadKeyStore(String storeType, char[] storePasswd, InputStream inputStream)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore keyStore = KeyStore.getInstance(storeType);
		keyStore.load(inputStream, storePasswd);
		return keyStore;
	}

	public static KeyStore loadKeyStore(String storeFile, String storeType, char[] storePasswd)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		try (InputStream stream = CryptoUtils.class.getClassLoader().getResourceAsStream(storeFile)) {
			return loadKeyStore(storeType, storePasswd, stream);
		}
	}

	public static SecretKey generateAesSecretKey() throws NoSuchAlgorithmException {
		KeyGenerator generator = KeyGenerator.getInstance("AES");
		generator.init(128);
		SecretKey key = generator.generateKey();
		return key;
	}

	public static SecretKey keyBytesToAesSecretKey(final byte[] key) throws NoSuchAlgorithmException {
		SecretKey secKey = new SecretKeySpec(key, 0, 16, "AES");
		return secKey;
	}
}
