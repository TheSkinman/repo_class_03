package edu.uw.nrs;

//import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountFactory;
import edu.uw.ext.framework.account.Address;
import edu.uw.ext.framework.account.CreditCard;

/**
 * JUnit test for the Account implementation classes.
 *
 * @author Russ Moul
 */
public class AccountImplTest {

	/** Test account's name */
	private static final String ACCT_NAME = "neotheone";

	/** Alternate test account name */
	private static final String ALT_ACCT_NAME = "tanderson";

	/** Test account's bad name, too short */
	private static final String BAD_ACCT_NAME = "theone";

	/** Test account's password bytes */
	private static final byte[] PASSWORD_BYTES = { 'p', 'a', 's', 's', 'w', 'o', 'r', 'd' };

	/** Test account's better password bytes */
	private static final byte[] PASSWORD_BYTES_UPDATED = { 'b', 'e', 't', 't', 'e', 'r', 'p', 'a', 's', 's', 'w', 'o',
			'r', 'd' };

	/** Test account's initial balance */
	private static final int INIT_BALANCE = 100000;

	/** Test account's bad initial balance, too low */
	private static final int BAD_INIT_BALANCE = 10000;

	/** Test account's full name */
	private static final String FULL_NAME = "Thomas Anderson";

	/** Test account's phone number */
	private static final String PHONE = "(555) 567-8900";

	/** Test account's name email address */
	private static final String EMAIL = "neo@metacortex.com";

	/** Test account's street address */
	private static final String STREET = "101 Hackett Street";

	/** Test account's address city */
	private static final String CITY = "Mega City";

	/** Test account's address state */
	private static final String STATE = "IL";

	/** Test account's address ZIP code */
	private static final String ZIP = "60666";

	/** Test account's credit card issued */
	private static final String ISSUER = "Commonwealth Bank";

	/** Test account's credit card type */
	private static final String CARD_TYPE = "MasterChip";

	/** Test account's credit card holder */
	private static final String HOLDER = "Thomas A Anderson";

	/** Test account's credit card number */
	private static final String ACCT_NO = "1234-5678-9012-3456";

	/** Test account's credit card expiration date */
	private static final String EXPIRATION_DATE = "03/05";

	/** The account factory used by the tests */
	private static AccountFactory accountFactory;

	/** Spring bean factory. */
	private static ClassPathXmlApplicationContext appContext;

	/**
	 * Initialize the test fixture. Initializes variables used by the various tests.
	 *
	 * @throws Exception
	 *             is an exception is raised
	 */
	@BeforeAll
	public static void setUp() throws Exception {
		System.out.println("Starting tests...");
		appContext = new ClassPathXmlApplicationContext("context.xml");
		accountFactory = appContext.getBean("AccountFactory", AccountFactory.class);
	}

	/**
	 * Tears down the test fixture.
	 */
	@AfterAll
	public static void tearDown() {
		if (appContext != null) {
			appContext.close();
		}
	}

	/**
	 * Tests the creation of an <code>Account</code> object.
	 *
	 * @throws Exception
	 *             is an exception is raised
	 */
//	@Test
	public void testGoodAccountCreation() throws Exception {
		Account acct = accountFactory.newAccount(ACCT_NAME, PASSWORD_BYTES, INIT_BALANCE);
		acct.setFullName(FULL_NAME);
		acct.setPhone(PHONE);
		acct.setEmail(EMAIL);

		Address addr = appContext.getBean("Address", Address.class);

		addr.setStreetAddress(STREET);
		addr.setCity(CITY);
		addr.setState(STATE);
		addr.setZipCode(ZIP);
		acct.setAddress(addr);

		CreditCard card = appContext.getBean("CreditCard", CreditCard.class);
		card.setType(CARD_TYPE);
		card.setIssuer(ISSUER);
		card.setHolder(HOLDER);
		card.setAccountNumber(ACCT_NO);
		card.setExpirationDate(EXPIRATION_DATE);
		acct.setCreditCard(card);

		assertEquals(ACCT_NAME, acct.getName());
		assertEquals(INIT_BALANCE, acct.getBalance());
		assertEquals(FULL_NAME, acct.getFullName());
		assertEquals(PHONE, acct.getPhone());
		assertEquals(EMAIL, acct.getEmail());

		Address verifyAddr = acct.getAddress();
		assertEquals(STREET, verifyAddr.getStreetAddress());
		assertEquals(CITY, verifyAddr.getCity());
		assertEquals(STATE, verifyAddr.getState());
		assertEquals(ZIP, verifyAddr.getZipCode());
		assertNotNull(verifyAddr.toString());

		CreditCard verifyCard = acct.getCreditCard();
		assertEquals(CARD_TYPE, verifyCard.getType());
		assertEquals(ISSUER, verifyCard.getIssuer());
		assertEquals(HOLDER, verifyCard.getHolder());
		assertEquals(ACCT_NO, verifyCard.getAccountNumber());
		assertEquals(EXPIRATION_DATE, verifyCard.getExpirationDate());

		acct.setBalance(BAD_INIT_BALANCE);
		assertEquals(BAD_INIT_BALANCE, acct.getBalance());

		assertTrue(Arrays.equals(PASSWORD_BYTES, acct.getPasswordHash()));
		acct.setPasswordHash(PASSWORD_BYTES_UPDATED);
		assertTrue(Arrays.equals(PASSWORD_BYTES_UPDATED, acct.getPasswordHash()));
	}

	/**
	 * Tests the failed creation, due to a bad account name, of an
	 * <code>Account</code> object.
	 *
	 * @throws Exception
	 *             is an exception is raised
	 */
//	@Test
	public void testBadNameAccountCreation() throws Exception {
		assertNull(accountFactory.newAccount(BAD_ACCT_NAME, PASSWORD_BYTES, INIT_BALANCE));
	}

	/**
	 * Tests the setting of the account name.
	 *
	 * @throws Exception
	 *             is an exception is raised
	 */
//	@Test
	public void testSetAccountName() throws Exception {
		Account acct = accountFactory.newAccount(ACCT_NAME, PASSWORD_BYTES, INIT_BALANCE);
		try {
			acct.setName(BAD_ACCT_NAME);
			fail("Shouldn't be able to set the name to '" + BAD_ACCT_NAME + "'");
		} catch (AccountException ex) {
			// this is expected
			acct.setName(ALT_ACCT_NAME);
		}
	}

	/**
	 * Tests the failed creation, due to a bad initial balance, of an
	 * <code>Account</code> object.
	 *
	 * @throws Exception
	 *             is an exception is raised
	 */
//	@Test
	public void testBadBalanceAccountCreation() throws Exception {
		assertNull(accountFactory.newAccount(ACCT_NAME, PASSWORD_BYTES, BAD_INIT_BALANCE));
	}

}
