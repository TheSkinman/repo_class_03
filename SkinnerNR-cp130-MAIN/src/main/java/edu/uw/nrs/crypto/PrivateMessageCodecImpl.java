package edu.uw.nrs.crypto;

import static edu.uw.nrs.crypto.CryptoUtils.JCEKS;
import static edu.uw.nrs.crypto.CryptoUtils.generateAesSecretKey;
import static edu.uw.nrs.crypto.CryptoUtils.keyBytesToAesSecretKey;
import static edu.uw.nrs.crypto.CryptoUtils.loadKeyStore;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger log = LoggerFactory.getLogger(PrivateMessageCodecImpl.class.getName());

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

		byte[] encipheredSharedKey = null;
		byte[] ciphertext = null;
		byte[] signature = null;

		log.debug("plaintext as array = " + Arrays.toString(plaintext));
		log.debug("plaintext = " + new String(plaintext));
		log.debug("senderKeyStoreName = " + senderKeyStoreName);
		log.debug("senderKeyStorePasswd = " + new String(senderKeyStorePasswd));
		log.debug("senderKeyName = " + senderKeyName);
		log.debug("senderKeyPasswd = " + new String(senderKeyPasswd));
		log.debug("senderTrustStoreName = " + senderTrustStoreName);
		log.debug("senderTrustStorePasswd = " + new String(senderTrustStorePasswd));
		log.debug("recipientCertName = " + recipientCertName);

		// 1. Generate a one-time use shared symmetric secret key
		// slide 35
		SecretKey secKey = generateAesSecretKey();

		// 2. Encipher the the order data
		// using the one-time use shared symmetric secret key
		// slide 35
		Cipher cipher = Cipher.getInstance(secKey.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, secKey);
		ciphertext = cipher.doFinal(plaintext);

		// 3. Obtain the bytes representing the one-time use shared symmetric secret key
		// slide 35
		byte[] secKeyBytes = secKey.getEncoded();

		// 4. Retrieve the (broker's) public key from the provided truststore
		// slide 30
		KeyStore clientTrustStore = loadKeyStore(senderTrustStoreName, JCEKS, senderTrustStorePasswd);
		Certificate cert = clientTrustStore.getCertificate(recipientCertName);
		PublicKey pubKey = cert.getPublicKey();

		// 5. Encipher the shared symmetric secret key's bytes
		// using the public key from the truststore
		cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		encipheredSharedKey = cipher.doFinal(secKeyBytes);

		// 6. Retrieve the (client's) private key from the the provided keystore
		// slide 29
		KeyStore privateStore = loadKeyStore(senderKeyStoreName, JCEKS, senderKeyStorePasswd);
		PrivateKey privateKey = (PrivateKey) privateStore.getKey(senderKeyName, senderKeyPasswd);

		// 7. Sign the plaintext order data using the private key from the the provided
		// keystore
		// slide 29
		Signature signer = Signature.getInstance("MD5withRSA");
		signer.initSign(privateKey);
		signer.update(plaintext);
		signature = signer.sign();

		// 8. Construct and return a PrivateMessageTriple containing the ciphertext, key
		// bytes and signature
		PrivateMessageTriple returnPMT = new PrivateMessageTriple(encipheredSharedKey, ciphertext, signature);

		return returnPMT;
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

		log.debug("recipientKeyStoreName = " + recipientKeyStoreName);
		log.debug("recipientKeyStorePasswd = " + new String(recipientKeyStorePasswd));
		log.debug("recipientKeyName = " + recipientKeyName);
		log.debug("recipientKeyPasswd = " + new String(recipientKeyPasswd));
		log.debug("trustStoreName = " + trustStoreName);
		log.debug("trustStorePasswd = " + new String(trustStorePasswd));
		log.debug("signerCertName = " + signerCertName);

		// 1. Obtain the shared secret key, order data ciphertext and signature from the
		// provided PrivateMessageTriple
		byte[] encipheredSharedKey = triple.getEncipheredSharedKey();
		byte[] ciphertext = triple.getCiphertext();
		byte[] signature = triple.getSignature();

		// 2. Retrieve the (brokers's) private key from the the provided keystore
		KeyStore keyStore = loadKeyStore(recipientKeyStoreName, JCEKS, recipientKeyStorePasswd);
		Key privKey = keyStore.getKey(recipientKeyName, recipientKeyPasswd);

		// 3. Use the private key from the keystore to decipher the shared secret key's
		Cipher decipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		decipher.init(Cipher.DECRYPT_MODE, privKey);
		byte[] secKeyBytes = decipher.doFinal(encipheredSharedKey);

		// 4. Reconstruct the shared secret key from shared secret key's bytes
		SecretKey secKey = keyBytesToAesSecretKey(secKeyBytes);

		// 5. Use the shared secret key to decipher the order data ciphertext
		Cipher decipherData = Cipher.getInstance(secKey.getAlgorithm());
		decipherData.init(Cipher.DECRYPT_MODE, secKey);
		byte[] plaintext = decipherData.doFinal(ciphertext);

		// 6. Retrieve the (client's) public key from the provided truststore
		KeyStore clientTrustStore = loadKeyStore(trustStoreName, JCEKS, trustStorePasswd);
		Certificate cert = clientTrustStore.getCertificate(signerCertName);
		PublicKey publicKey = cert.getPublicKey();

		// 7. Verify the order data plaintext and signature using the public key from
		// the truststore
		// slide 30
		Signature verifier = Signature.getInstance("MD5withRSA");
		verifier.initVerify(publicKey);
		verifier.update(plaintext);
		boolean valid = verifier.verify(signature);

		// 8. Return the order data plaintext
		if (valid) {
			return plaintext;
		} else {
			log.warn("Data was not signed correctly! ! !");
			return null;
		}
	}

}
