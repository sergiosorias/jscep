/*
 * Copyright (c) 2009 David Grant
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

package com.google.code.jscep.response;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertStore;
import java.util.logging.Logger;

import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;

import com.google.code.jscep.transaction.CmsException;
import com.google.code.jscep.util.HexUtil;

/**
 * Implementation of {@link PkcsPkiEnvelope} that uses Bouncy Castle.
 */
public class PkcsPkiEnvelopeImpl extends PkcsPkiEnvelope {
	private final static Logger LOGGER = Logger.getLogger(PkcsPkiEnvelopeImpl.class.getName());
	public KeyPair keyPair;
	public byte[] envelopedData;

	public PkcsPkiEnvelopeImpl(KeyPair keyPair, byte[] envelopedData) {
		LOGGER.info("INCOMING EnvelopedData:\n" + HexUtil.format(envelopedData));
		this.keyPair = keyPair;
		this.envelopedData = envelopedData;
	}
	
	public CertStore getCertStore() throws NoSuchProviderException, NoSuchAlgorithmException, CmsException {
		try {
			// TODO: Try to break BC Mail dependency.
			CMSEnvelopedData ed = new CMSEnvelopedData(envelopedData);
			RecipientInformationStore recipientStore = ed.getRecipientInfos();
	    	RecipientInformation recipient = (RecipientInformation) recipientStore.getRecipients().iterator().next();
	    	byte[] content = recipient.getContent(keyPair.getPrivate(), "BC");
	    	CMSSignedData contentData = new CMSSignedData(content);
	    	
	    	return contentData.getCertificatesAndCRLs("Collection", "BC");
		} catch (CMSException e) {
			throw new CmsException(e);
		}
	}
}
