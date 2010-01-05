package com.google.code.jscep;

import java.math.BigInteger;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.security.auth.x500.X500Principal;

import com.google.code.jscep.response.Capability;
import com.google.code.jscep.util.LoggingUtil;

public class ScepServletImpl extends ScepServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger LOGGER = LoggingUtil.getLogger("com.google.code.jscep");
	
	@Override
	protected List<X509Certificate> enrollCertificate(byte[] certificationRequest) {
		 LOGGER.entering(getClass().getName(), "enrollCertificate");
		 
		return null;
	}

	@Override
	protected List<X509Certificate> getCACertificate(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<X509CRL> getCRL(X500Principal issuer, BigInteger serial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Set<Capability> getCapabilities(String identifier) {
		LOGGER.entering(getClass().getName(), "getCapabilities");
		
		final Set<Capability> caps = EnumSet.of(Capability.SHA_1);
		
		LOGGER.exiting(getClass().getName(), "getCapabilities", caps);
		return caps;
	}

	@Override
	protected X509Certificate getCertificate(X500Principal issuer,
			BigInteger serial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected X509Certificate getCertificate(X500Principal issuer,
			X500Principal subject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<X509Certificate> getNextCACertificate(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}

}
