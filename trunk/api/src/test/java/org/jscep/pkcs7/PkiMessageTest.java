package org.jscep.pkcs7;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;

import junit.framework.Assert;

import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.x509.X509Name;
import org.jscep.operations.GetCRL;
import org.jscep.operations.GetCert;
import org.jscep.operations.GetCertInitial;
import org.jscep.operations.PKCSReq;
import org.jscep.pkcs7.MessageData;
import org.jscep.pkcs7.PkiMessage;
import org.jscep.pkcs7.PkiMessageGenerator;
import org.jscep.pkcs7.PkiMessageParser;
import org.jscep.pkcs7.SignedDataGenerator;
import org.jscep.transaction.MessageType;
import org.jscep.transaction.Nonce;
import org.jscep.transaction.PkiStatus;
import org.jscep.transaction.TransactionId;
import org.jscep.x509.X509Util;
import org.junit.Before;
import org.junit.Test;


public class PkiMessageTest {
	private PkiMessageGenerator generator;
	private PkiMessageParser parser;
	private KeyPair keyPair;
	private X500Principal subject;
	private X509Certificate recipient;
	private X509Certificate identity;
	
	@Before
	public void setUp() throws Exception {
		keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		subject = new X500Principal("CN=example.org");
		recipient = X509Util.createEphemeralCertificate(subject, keyPair);
		identity = X509Util.createEphemeralCertificate(subject, keyPair);
		
		generator = new PkiMessageGenerator();
		generator.setTransactionId(TransactionId.createTransactionId());
		generator.setSenderNonce(Nonce.nextNonce());
		generator.setPkiStatus(PkiStatus.SUCCESS);
		generator.setRecipientNonce(Nonce.nextNonce());
		generator.setMessageDigest("SHA-1");
		generator.setCipherAlgorithm("DES");
		generator.setKeyPair(keyPair);
		generator.setRecipient(recipient);
		generator.setSigner(identity);
		
		parser = new PkiMessageParser();
		parser.setPrivateKey(keyPair.getPrivate());
	}
	
	@Test
	public void testCertRepResponse() throws IOException, NoSuchAlgorithmException {
		SignedDataGenerator dsdGenerator = new SignedDataGenerator();
		SignedData sd = dsdGenerator.generate(); 
		generator.setMessageType(MessageType.CertRep);
		generator.setMessageData(MessageData.getInstance(sd));
		final PkiMessage generatedMsg = generator.generate();
		final PkiMessage parsedMsg = parser.parse(generatedMsg.getSignerData());
		
		Assert.assertEquals(generatedMsg, parsedMsg);
	}
	
	@Test
	public void testGetCertRequest() throws IOException {
		final GetCert req = new GetCert(subject, BigInteger.ONE);
		
		generator.setMessageType(MessageType.GetCert);
		generator.setMessageData(MessageData.getInstance(req.getMessage()));
		final PkiMessage generatedMsg = generator.generate();
		final PkiMessage parsedMsg = parser.parse(generatedMsg.getSignerData());
		
		Assert.assertEquals(generatedMsg, parsedMsg);
	}
	
	@Test
	public void testGetCertInitialRequest() throws IOException {
		final GetCertInitial req = new GetCertInitial(new X509Name("CN=example.org"), new X509Name("CN=example.org"));
		
		generator.setMessageType(MessageType.GetCertInitial);
		generator.setMessageData(MessageData.getInstance(req.getMessage()));
		final PkiMessage generatedMsg = generator.generate();
		final PkiMessage parsedMsg = parser.parse(generatedMsg.getSignerData());
		
		Assert.assertEquals(generatedMsg, parsedMsg);
	}
	
	@Test
	public void testGetCRLRequest() throws IOException {
		final GetCRL req = new GetCRL(subject, BigInteger.ONE);
		
		generator.setMessageType(MessageType.GetCRL);
		generator.setMessageData(MessageData.getInstance(req.getMessage()));
		final PkiMessage generatedMsg = generator.generate();
		final PkiMessage parsedMsg = parser.parse(generatedMsg.getSignerData());
		
		Assert.assertEquals(generatedMsg, parsedMsg);
	}
	
	@Test
	public void testPKCSReqRequest() throws IOException {
		final PKCSReq req = new PKCSReq(keyPair, identity, "SHA-1", new char[0]);
		
		generator.setMessageType(MessageType.PKCSReq);
		generator.setMessageData(MessageData.getInstance(req.getMessage()));
		final PkiMessage generatedMsg = generator.generate();
		final PkiMessage parsedMsg = parser.parse(generatedMsg.getSignerData());
		
		Assert.assertEquals(generatedMsg, parsedMsg);
	}
}
