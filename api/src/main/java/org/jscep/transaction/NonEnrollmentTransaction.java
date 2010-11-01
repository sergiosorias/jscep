package org.jscep.transaction;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.cms.CMSSignedData;
import org.jscep.content.CertRepContentHandler;
import org.jscep.message.CertRep;
import org.jscep.message.GetCert;
import org.jscep.message.PkiMessageDecoder;
import org.jscep.message.PkiMessageEncoder;
import org.jscep.message.PkiRequest;
import org.jscep.pkcs7.SignedDataUtil;
import org.jscep.request.PKCSReq;
import org.jscep.transport.Transport;

public class NonEnrollmentTransaction extends Transaction {
	private final TransactionId transId;
	private final PkiRequest<? extends ASN1Encodable> request;
	// Optional
	
	public NonEnrollmentTransaction(PkiMessageEncoder encoder, PkiMessageDecoder decoder, IssuerAndSerialNumber iasn, MessageType msgType) {
		super(encoder, decoder);
		this.transId = TransactionId.createTransactionId();
		
		if (msgType == MessageType.GetCert) {
			this.request = new GetCert(transId, Nonce.nextNonce(), iasn);
		} else  if (msgType == MessageType.GetCRL) {
			this.request = new GetCert(transId, Nonce.nextNonce(), iasn);
		} else {
			throw new IllegalArgumentException(msgType.toString());
		}
	}
	
	public TransactionId getId() {
		return transId;
	}
	
	public State send(Transport transport) throws IOException {
		final CMSSignedData signedData = encoder.encode(request);
		final CertRepContentHandler handler = new CertRepContentHandler();
		final CMSSignedData inMsg = transport.sendRequest(new PKCSReq(signedData, handler));
		final CertRep response = (CertRep) decoder.decode(inMsg);
		
		if (response.getPkiStatus() == PkiStatus.FAILURE) {
			failInfo = response.getFailInfo();
			state = State.CERT_NON_EXISTANT;
		} else if (response.getPkiStatus() == PkiStatus.SUCCESS) {
			try {
				certStore = SignedDataUtil.extractCertStore(response.getMessageData());
			} catch (GeneralSecurityException e) {
				throw new IOException(e);
			}
			state = State.CERT_ISSUED;
		} else {
			throw new IOException("Invalid Response");
		}
		
		return state;
	}
}
