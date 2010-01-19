/*
 * Copyright (c) 2009-2010 David Grant
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.google.code.jscep.pkcs7;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.BERConstructedOctetString;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;

import com.google.code.jscep.util.LoggingUtil;

public class PkcsPkiEnvelopeGenerator {
	private static Logger LOGGER = LoggingUtil.getLogger("com.google.code.jscep.pkcs7");
	private X509Certificate recipient;
	private AlgorithmIdentifier cipherAlgorithm;
	
	public void setRecipient(X509Certificate recipient) {
		this.recipient = recipient;
	}
	
	public void setCipherAlgorithm(AlgorithmIdentifier cipherAlgorithm) {
		this.cipherAlgorithm = cipherAlgorithm;
	}
	
	public PkcsPkiEnvelope generate(ASN1Encodable messageData) throws IOException {
		LOGGER.entering(getClass().getName(), "generate");

    	final ContentInfo contentInfo;
		try {
			final Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			final SecretKey encKey = KeyGenerator.getInstance("DES").generateKey();
			final AlgorithmParameters params = generateParameters();
			final AlgorithmIdentifier encAlgId = getAlgorithmIdentifier(cipherAlgorithm.getObjectId(), params);
			cipher.init(Cipher.ENCRYPT_MODE, encKey, params);
						
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			CipherOutputStream caos = new CipherOutputStream(baos, cipher);
			caos.write(messageData.getDEREncoded());
			caos.close();
			
			final ASN1OctetString encContent = new BERConstructedOctetString(baos.toByteArray());
			final RecipientInfo keyTrans = toRecipientInfo(recipient, encKey);
			final ASN1EncodableVector recipientInfos = new ASN1EncodableVector();
			recipientInfos.add(keyTrans);

			final EncryptedContentInfo eci = new EncryptedContentInfo(PKCSObjectIdentifiers.data, encAlgId, encContent);
			final EnvelopedData ed = new EnvelopedData(null, new DERSet(recipientInfos), eci, null);
			contentInfo = new ContentInfo(PKCSObjectIdentifiers.envelopedData, ed);
			
		} catch (Exception e) {
			
			IOException ioe = new IOException(e);
			LOGGER.throwing(getClass().getName(), "parse", ioe);
			throw ioe;
		}
    	
    	final PkcsPkiEnvelopeImpl envelope = new PkcsPkiEnvelopeImpl();
    	envelope.setEncoded(contentInfo.getEncoded());
    	envelope.setMessageData(messageData);
    	
    	LOGGER.exiting(getClass().getName(), "generate", envelope);
		return envelope;
	}
	
	private AlgorithmParameters generateParameters() throws GeneralSecurityException {
		final byte[] iv = new byte[8];
		final SecureRandom rnd = new SecureRandom();
		rnd.nextBytes(iv);
		
		final AlgorithmParameters params = AlgorithmParameters.getInstance("DES");
		params.init(new IvParameterSpec(iv));
		
		return params;
	}
	
	private AlgorithmIdentifier getAlgorithmIdentifier(DERObjectIdentifier oid, AlgorithmParameters algParams) throws IOException {
		ASN1InputStream in = new ASN1InputStream(algParams.getEncoded());
		DEREncodable asn1Params = in.readObject();
		
		return new AlgorithmIdentifier(oid, asn1Params);
	}
	
	private RecipientInfo toRecipientInfo(X509Certificate cert, SecretKey key) throws CertificateEncodingException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {
		PublicKey pubKey = cert.getPublicKey();
		TBSCertificateStructure tbs = TBSCertificateStructure.getInstance(ASN1Object.fromByteArray(cert.getTBSCertificate()));
		AlgorithmIdentifier keyEncAlg = tbs.getSubjectPublicKeyInfo().getAlgorithmId();

		ASN1OctetString encKey;
		Cipher keyCipher = Cipher.getInstance("RSA");
		keyCipher.init(Cipher.WRAP_MODE, pubKey);
		encKey = new DEROctetString(keyCipher.wrap(key));
		
		ASN1InputStream aIn = new ASN1InputStream(cert.getTBSCertificate());
		tbs = TBSCertificateStructure.getInstance(aIn.readObject());
		IssuerAndSerialNumber encSid = new IssuerAndSerialNumber(tbs.getIssuer(), tbs.getSerialNumber().getValue());
		
		return new RecipientInfo(new KeyTransRecipientInfo(new RecipientIdentifier(encSid), keyEncAlg, encKey));
	}
}
