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

package com.google.code.jscep.asn;

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x509.X509Name;

/**
 * IssuerAndSubject ::= SEQUENCE {
 * issuer Name,
 * subject Name,
 * }
 */
public class IssuerAndSubject implements DEREncodable {
    private final X509Name issuer;
    private final X509Name subject;

    public IssuerAndSubject(ASN1Sequence seq) {
        this.issuer = (X509Name) seq.getObjectAt(0);
        this.subject = (X509Name) seq.getObjectAt(1);
    }

    public IssuerAndSubject(X509Name issuer, X509Name subject) {
        this.issuer = issuer;
        this.subject = subject;
    }

    public DERObject getDERObject() {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(issuer);
        v.add(subject);

        return new DERSequence(v);
    }

    public X509Name getIssuer() {
        return issuer;
    }

    public X509Name getSubject() {
        return subject;
    }
}
