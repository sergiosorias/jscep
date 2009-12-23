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

package com.google.code.jscep.request;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.logging.Logger;

import com.google.code.jscep.content.NextCaCertificateContentHandler;

/**
 * This class represents a <tt>GetNextCACert</tt> request.
 * 
 * @see <a href="http://tools.ietf.org/html/draft-nourse-scep-19#section-5.2.6">SCEP Internet-Draft Reference</a>
 */
public class GetNextCACert implements Request<List<X509Certificate>> {
	private static Logger LOGGER = Logger.getLogger("com.google.code.jscep.request");
    private final String caIdentifier;
    private final X509Certificate issuer;
    
    /**
     * Creates a new GetNextCACert request without a CA identification string.
     * 
     * @param issuer the existing CA certificate.
     */
    public GetNextCACert(X509Certificate issuer) {
    	this.issuer = issuer;
    	this.caIdentifier = null;
    }

    /**
     * Creates a new GetNextCACert request with the given CA identification string.
     * 
     * @param issuer the existing CA certificate.
     * @param caIdentifier the CA identification string.
     */
    public GetNextCACert(X509Certificate issuer, String caIdentifier) {
    	this.issuer = issuer;
        this.caIdentifier = caIdentifier;
    }

    /**
     * {@inheritDoc}
     */
    public Operation getOperation() {
        return Operation.GetNextCACert;
    }

    /**
     * {@inheritDoc}
     */
    public String getMessage() {
        return caIdentifier;
    }
     
    /**
     * {@inheritDoc}
     */
    public NextCaCertificateContentHandler getContentHandler() {
    	return new NextCaCertificateContentHandler(issuer);
    }
}
