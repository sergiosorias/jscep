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

import com.google.code.jscep.content.NextCaCertificateContentHandler;

/**
 * @link http://tools.ietf.org/html/draft-nourse-scep-19#section-5.2.6
 */
public class GetNextCaCert implements Request<List<X509Certificate>> {
    private static final String OPERATION = "GetNextCaCert";
    private String ca;

    public GetNextCaCert() {
    }

    public GetNextCaCert(String ca) {
        this.ca = ca;
    }

    public String getOperation() {
        return OPERATION;
    }

    public String getMessage() {
        return ca;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
    	return OPERATION;
    }
    
    public NextCaCertificateContentHandler getContentHandler() {
    	return new NextCaCertificateContentHandler();
    }
}
