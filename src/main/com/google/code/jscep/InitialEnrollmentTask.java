package com.google.code.jscep;

import java.security.KeyPair;
import java.security.cert.CertStore;
import java.security.cert.X509Certificate;
import java.util.concurrent.Callable;

import com.google.code.jscep.request.PkcsReq;
import com.google.code.jscep.request.PkiOperation;
import com.google.code.jscep.transaction.Transaction;
import com.google.code.jscep.transaction.TransactionFactory;
import com.google.code.jscep.transport.Transport;

public class InitialEnrollmentTask extends AbstractEnrollmentTask {
	private final Transport transport;
	private final X509Certificate ca;
	private final KeyPair keyPair;
	private final X509Certificate identity;
	private final char[] password;
	
	public InitialEnrollmentTask(Transport transport, X509Certificate ca, KeyPair keyPair, X509Certificate identity, char[] password) {
		this.transport = transport;
		this.ca = ca;
		this.keyPair = keyPair;
		this.identity = identity;
		this.password = password;
	}
	
	@Override
	public EnrollmentResult call() throws Exception {
		Transaction trans = TransactionFactory.createTransaction(transport, ca, identity, keyPair);
		PkiOperation req = new PkcsReq(keyPair, identity, password);
		try {
			CertStore store = trans.performOperation(req);
			
			return new EnrollmentResult(getCertificates(store.getCertificates(null)));
		} catch (RequestPendingException e) {
			Callable<EnrollmentResult> task = new PendingEnrollmentTask(transport, ca, keyPair, identity);
			
			return new EnrollmentResult(task);
		}
	}
}
