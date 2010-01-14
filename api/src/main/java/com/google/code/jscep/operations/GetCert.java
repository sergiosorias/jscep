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

package com.google.code.jscep.operations;

import java.io.IOException;
import java.math.BigInteger;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.X509Principal;

import com.google.code.jscep.transaction.MessageType;

/**
 * This class represents the <tt>SCEP</tt> <tt>GetCert</tt> <tt>pkiMessage</tt> type.
 * 
 * @see <a href="http://tools.ietf.org/html/draft-nourse-scep-20#section-3.2.4">SCEP Internet-Draft Reference</a>
 */
public class GetCert implements PkiOperation {
	private final X500Principal issuer;
    private final BigInteger serial;

    public GetCert(X500Principal issuer, BigInteger serial) {
        this.issuer = issuer;
        this.serial = serial;
    }

    /**
     * {@inheritDoc}
     */
    public MessageType getMessageType() {
        return MessageType.GetCert;
    }

    /**
     * Returns a DER-encoded IssuerAndSerialNumber.
     * 
     * @return the IssuerAndSerialNumber
     * @see <a href="http://tools.ietf.org/html/rfc2315#section-6.7">SCEP Internet-Draft Reference</a>
     */
	public ASN1Encodable getMessageData() throws IOException {
    	// TODO: BC Dependency
        X509Name issuerName = new X509Principal(issuer.getEncoded());
        
        return new IssuerAndSerialNumber(issuerName, serial);
    }
}
