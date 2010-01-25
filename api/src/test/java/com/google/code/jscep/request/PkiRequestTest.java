package com.google.code.jscep.request;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import junit.framework.Assert;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.google.code.jscep.pkcs7.PkiMessage;

public class PkiRequestTest {
	private PkiMessage message;
	private PkiRequest fixture;
	
	@Before
	public void setUp() throws Exception {
		message = EasyMock.createMock(PkiMessage.class);
		EasyMock.expect(message.getEncoded()).andReturn(new byte[0]).times(2);
		EasyMock.replay(message);
		
		KeyPair keyPair = KeyPairGenerator.getInstance("RSA").genKeyPair();
		
		fixture = new PkiRequest(message, keyPair);
	}
	
	@Test
	public void testGetMessage() throws IOException {
		Assert.assertEquals(message.getEncoded(), fixture.getMessage());
	}

	@Test
	public void testGetOperation() {
		Assert.assertEquals(Operation.PKIOperation, fixture.getOperation());
	}
	
	@Test
	public void testContentHandler() {
		Assert.assertNotNull(fixture.getContentHandler());
	}
}
