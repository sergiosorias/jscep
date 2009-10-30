package com.google.code.jscep;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertStore;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.x509.X509V1CertificateGenerator;

import com.google.code.jscep.asn1.MessageType;
import com.google.code.jscep.asn1.PkiStatus;
import com.google.code.jscep.asn1.ScepObjectIdentifiers;
import com.google.code.jscep.request.PkiOperation;
import com.google.code.jscep.request.PkiRequest;
import com.google.code.jscep.transport.Transport;

public class Transaction {
    private static AtomicLong transactionCounter = new AtomicLong();
    private static Random rnd = new SecureRandom(); 
    private DERPrintableString transId;
    private DEROctetString senderNonce;
    private int reason;
    private KeyPair keyPair;
    private X509Certificate ca;
    private X509Certificate identity;
    private List<X509CRL> crls;
    private List<X509Certificate> certs;
    private final Transport transport;
    
    protected Transaction(Transport transport, X509Certificate ca, KeyPair keyPair) {
    	this.transport = transport;
    	this.ca = ca;
    	this.keyPair = keyPair;
        this.transId = generateTransactionId(keyPair);
        
    	// FUDGED.  What identity do we send for getCRL?
    	X509V1CertificateGenerator gen = new X509V1CertificateGenerator();
    	gen.setIssuerDN(new X500Principal("CN=foo"));
    	gen.setNotAfter(new Date());
    	gen.setNotBefore(new Date());
    	gen.setPublicKey(keyPair.getPublic());
    	gen.setSerialNumber(BigInteger.ONE);
    	gen.setSignatureAlgorithm("MD5withRSA");
    	gen.setSubjectDN(new X500Principal("CN=foo"));
    	try {
			this.identity = gen.generate(keyPair.getPrivate());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
        
        init();
    }
    
    private void init() {
    	final byte[] nonce = new byte[16];
    	rnd.nextBytes(nonce);
    	
    	this.senderNonce = new DEROctetString(nonce);
    }
    
    private DERPrintableString generateTransactionId(KeyPair keyPair) {
    	if (keyPair == null) {
    		return new DERPrintableString(Long.toHexString(transactionCounter.getAndIncrement()).getBytes());
    	} else {
	    	MessageDigest digest = null;
	        try {
	            digest = MessageDigest.getInstance("MD5");
	        } catch (NoSuchAlgorithmException e) {
	            throw new RuntimeException(e);
	        }
	        return new DERPrintableString(Hex.encode(digest.digest(keyPair.getPublic().getEncoded())));
    	}
    }
    
    public int getFailureReason() {
    	return reason;
    }
    
    private CMSEnvelopedData envelope(DEREncodable data) throws GeneralSecurityException, CMSException {
    	CMSEnvelopedDataGenerator gen = new CMSEnvelopedDataGenerator();
    	gen.addKeyTransRecipient(ca);
    	
    	ContentInfo contentInfo = new ContentInfo(PKCSObjectIdentifiers.data, data);
    	CMSProcessable processableData = new CMSProcessableByteArray(contentInfo.getDEREncoded());
    	
    	return gen.generate(processableData, getCipherId(), "BC");
    }
    
    private CMSSignedData sign(CMSProcessable data, AttributeTable table) throws GeneralSecurityException, CMSException {
    	CMSSignedDataGenerator signer = new CMSSignedDataGenerator();
    	
    	List<X509Certificate> certList = new ArrayList<X509Certificate>(1);
        certList.add(identity);
        CertStore certs = CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList));
        signer.addCertificatesAndCRLs(certs);
        signer.addSigner(keyPair.getPrivate(), identity, getDigestId(), table, null);
        
    	return signer.generate(data, true, "BC");
    }

    public CertStore performOperation(PkiOperation operation) throws MalformedURLException, IOException, ScepException {
    	try {
    		CMSEnvelopedData enveloped = envelope(operation.getMessageData());
    		CMSProcessable envelopedData = new CMSProcessableByteArray(enveloped.getEncoded());
	
	        Attribute msgType = getMessageTypeAttribute(operation);
	        Attribute transId = getTransactionIdAttribute();
	        Attribute senderNonce = getSenderNonceAttribute();
	
	        Hashtable<DERObjectIdentifier, Attribute> attributes = new Hashtable<DERObjectIdentifier, Attribute>();
	        attributes.put(msgType.getAttrType(), msgType);
	        attributes.put(transId.getAttrType(), transId);
	        attributes.put(senderNonce.getAttrType(), senderNonce);
	        AttributeTable table = new AttributeTable(attributes);
	
	        CMSSignedData signedData = sign(envelopedData, table);
	        PkiRequest request = new PkiRequest(signedData);
	        CMSSignedData response = (CMSSignedData) transport.sendMessage(request);
    	
	        return handleResponse(response);
    	} catch (CMSException e) {
    		throw new ScepException(e);
    	} catch (GeneralSecurityException e) {
    		throw new ScepException(e);
    	}
    }
    
    private Attribute getMessageTypeAttribute(PkiOperation operation) {
        return new Attribute(ScepObjectIdentifiers.messageType, new DERSet(operation.getMessageType()));
    }
    

    private Attribute getTransactionIdAttribute() {
        return new Attribute(ScepObjectIdentifiers.transId, new DERSet(transId));
    }

    private Attribute getSenderNonceAttribute() {
        return new Attribute(ScepObjectIdentifiers.senderNonce, new DERSet(senderNonce));
    }

    private String getCipherId() {
        // DES
        // return SMIMECapability.dES_CBC.getId();
        // Triple-DES
        return SMIMECapability.dES_EDE3_CBC.getId();
    }

    private String getDigestId() {
        // MD5
        return CMSSignedDataGenerator.DIGEST_MD5;
        // SHA-1
        // return CMSSignedDataGenerator.DIGEST_SHA1;
        // SHA-256
        // return CMSSignedDataGenerator.DIGEST_SHA256;
        // SHA-512
        // return CMSSignedDataGenerator.DIGEST_SHA512;
    }
    
    public CertStore handleResponse(CMSSignedData signedData) throws ScepException {
    	SignerInformationStore store = signedData.getSignerInfos();
        Collection<?> signers = store.getSigners();
        
        if (signers.size() > 1) {
        	throw new ScepException("Too Many SignerInfos");
        }
        SignerInformation signerInformation = (SignerInformation) signers.iterator().next();
        AttributeTable signedAttrs = signerInformation.getSignedAttributes();

        Attribute transIdAttr = signedAttrs.get(ScepObjectIdentifiers.transId);
        DERPrintableString transId = (DERPrintableString) transIdAttr.getAttrValues().getObjectAt(0);
        if (transId.equals(this.transId) == false) {
            throw new ScepException("Transaction ID Mismatch: Sent [" + this.transId + "]; Received [" + transId + "]");
        }
        
        Attribute msgTypeAttribute = signedAttrs.get(ScepObjectIdentifiers.messageType);
        DERPrintableString msgType = (DERPrintableString) msgTypeAttribute.getAttrValues().getObjectAt(0);
        if (msgType.equals(MessageType.CertRep) == false) {
        	throw new ScepException("Invalid Message Type: " + msgType);
        }
        
//        Attribute senderNoneAttribute = signedAttrs.get(ScepObjectIdentifiers.senderNonce);
//        DEROctetString senderNonce = (DEROctetString) senderNoneAttribute.getAttrValues().getObjectAt(0);
        
        Attribute recipientNonceAttribute = signedAttrs.get(ScepObjectIdentifiers.recipientNonce);
        DEROctetString recipientNonce = (DEROctetString) recipientNonceAttribute.getAttrValues().getObjectAt(0);
        
        if (recipientNonce.equals(this.senderNonce) == false) {
        	throw new ScepException("Sender Nonce Mismatch.  Sent [" + this.senderNonce + "]; Received [" + recipientNonce + "]");
        }
        
        Attribute pkiStatusAttribute = signedAttrs.get(ScepObjectIdentifiers.pkiStatus);
        DERPrintableString pkiStatus = (DERPrintableString) pkiStatusAttribute.getAttrValues().getObjectAt(0);
        
        if (pkiStatus.equals(PkiStatus.FAILURE)) {
        	
        	Attribute failInfoAttribute = signedAttrs.get(ScepObjectIdentifiers.failInfo);
        	DERPrintableString failInfo = (DERPrintableString) failInfoAttribute.getAttrValues().getObjectAt(0);
        	
        	reason = Integer.parseInt(failInfo.toString());
        	return null;
        } else if (pkiStatus.equals(PkiStatus.PENDING)) {
        	return null;
        }
        
        try {
			return signedData.getCertificatesAndCRLs("Collection", "BC");
		} catch (CMSException e) {
			throw new ScepException(e);
		} catch (GeneralSecurityException e) {
			throw new ScepException(e);
		}
    }
    
    public List<X509CRL> getCRLs() {
    	return crls; 
    }
    
    public List<X509Certificate> getCertificates() {
    	return certs;
    }
}
