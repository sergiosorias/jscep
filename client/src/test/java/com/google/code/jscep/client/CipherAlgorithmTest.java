package com.google.code.jscep.client;

import static org.hamcrest.core.Is.is;

import java.io.IOException;

import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

import com.google.code.jscep.response.Capability;
import com.google.code.jscep.transaction.Transaction;

public class CipherAlgorithmTest extends AbstractClientTest {
	@Test
	public void testCipherDES() throws Exception {
		testCipher("DES");
	}
	
	@Test
	public void testCipher3DES() throws Exception {
		Assume.assumeTrue(client.getCaCapabilities().contains(Capability.TRIPLE_DES));
		
		testCipher("DESede");
	}
	
	@Ignore @Test(expected = IOException.class)
	public void testCipher3DESUnsupported() throws Exception {
		Assume.assumeThat(client.getCaCapabilities().contains(Capability.TRIPLE_DES), is(false));
		
		testCipher("DESede");
	}
	
	private void testCipher(String cipher) throws Exception {
		client.setPreferredCipherAlgorithm(cipher);
		
		Transaction trans = client.createTransaction();
		trans.enrollCertificate(identity, keyPair, password);
	}
}
