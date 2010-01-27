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
package com.google.code.jscep.operations;

import java.io.IOException;

import org.bouncycastle.asn1.x509.X509Name;

import com.google.code.jscep.asn1.IssuerAndSubject;
import com.google.code.jscep.transaction.MessageType;

/**
 * This class represents the <tt>SCEP</tt> <tt>GetCertInitial</tt> <tt>pkiMessage</tt> type.
 * 
 * @author davidjgrant1978
 */
public class GetCertInitial implements PkiOperation<IssuerAndSubject> {
	private final X509Name issuer;
    private final X509Name subject;

    public GetCertInitial(X509Name issuer, X509Name subject) {
        this.issuer = issuer;
        this.subject = subject;
    }

    /**
     * {@inheritDoc}
     */
    public MessageType getMessageType() {
        return MessageType.GetCertInitial;
    }

    /**
     * Returns a DER-encoded IssuerAndSubject
     * 
     * @return the IssuerAndSubject.
     */
	public IssuerAndSubject getMessageData() throws IOException {
		return new IssuerAndSubject(issuer, subject);
	}
	
	@Override
	public String toString() {
		return getMessageType().toString();
	}
}
