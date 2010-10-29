/*
 * Copyright (c) 2010 ThruPoint Ltd
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
package org.jscep.message;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.util.Collection;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class PkcsPkiEnvelopeDecoder {
	private final PrivateKey priKey;
	
	public PkcsPkiEnvelopeDecoder(PrivateKey priKey) {
		this.priKey = priKey;
	}
	
	@SuppressWarnings("unchecked")
	public ASN1Encodable decode(CMSEnvelopedData envelopedData) throws IOException {
		Collection<RecipientInformation> recipientInfos = envelopedData.getRecipientInfos().getRecipients();
		RecipientInformation recipientInfo = recipientInfos.iterator().next();

		Provider provider = new BouncyCastleProvider();
		byte[] bytes;
		try {
			Security.addProvider(provider);
			bytes = recipientInfo.getContent(priKey, provider.getName());
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			Security.removeProvider(provider.getName());
		}
		
		return ASN1Object.fromByteArray(bytes);
	}
}
