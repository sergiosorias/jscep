package com.google.code.jscep.pkcs7;

import java.io.IOException;
import java.security.cert.CertStore;
import java.util.logging.Logger;

import org.bouncycastle.cms.CMSSignedData;

import com.google.code.jscep.util.LoggingUtil;

public class DegenerateSignedDataParser {
	private static Logger LOGGER = LoggingUtil.getLogger("com.google.code.jscep.pkcs7");
	public DegenerateSignedData parse(byte[] signedData) throws IOException {
		LOGGER.entering(getClass().getName(), "parse");
		
		try {
			CMSSignedData sd = new CMSSignedData(signedData);
			CertStore store = sd.getCertificatesAndCRLs("Collection", "BC");
			
			DegenerateSignedDataImpl certRep = new DegenerateSignedDataImpl();
			certRep.setCertStore(store);
			certRep.setEncoded(signedData);
			
			LOGGER.exiting(getClass().getName(), "parse", certRep);
			return certRep;
		} catch (Exception e) {
			
			LOGGER.throwing(getClass().getName(), "parse", e);
			throw new IOException(e);
		}
	}
}
