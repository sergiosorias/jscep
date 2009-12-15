package com.google.code.jscep;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.x509.X509V1CertificateGenerator;

public final class X509CertificateFactory {
	public static X509Certificate createCertificate(X500Principal subject, KeyPair keyPair) throws GeneralSecurityException {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		final Date notBefore = cal.getTime();
		cal.add(Calendar.DATE, 2);
		final Date notAfter = cal.getTime();
		
		final X509V1CertificateGenerator gen = new X509V1CertificateGenerator();
		gen.setIssuerDN(subject);
		gen.setNotBefore(notBefore);
		gen.setNotAfter(notAfter);
		gen.setPublicKey(keyPair.getPublic());
		gen.setSerialNumber(BigInteger.ONE);
		gen.setSignatureAlgorithm("SHA1withRSA");
		gen.setSubjectDN(subject);

		return gen.generate(keyPair.getPrivate());
	}
}
