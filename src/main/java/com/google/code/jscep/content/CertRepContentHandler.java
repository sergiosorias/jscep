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

package com.google.code.jscep.content;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.util.logging.Logger;

import com.google.code.jscep.pkcs7.PkcsPkiEnvelopeParser;
import com.google.code.jscep.pkcs7.PkiMessage;
import com.google.code.jscep.pkcs7.PkiMessageParser;
import com.google.code.jscep.util.LoggingUtil;

/**
 * This class handles responses to <tt>PKIRequest</tt> requests.
 */
public class CertRepContentHandler implements ScepContentHandler<PkiMessage> {
	private static Logger LOGGER = LoggingUtil.getLogger("com.google.code.jscep.content");
	private final KeyPair keyPair;
	
	public CertRepContentHandler(KeyPair keyPair) {
		this.keyPair = keyPair;
	}
	
	/**
	 * {@inheritDoc}
	 * @throws IOException 
	 */
	public PkiMessage getContent(InputStream in, String mimeType) throws IOException {
		LOGGER.entering(getClass().getName(), "getContent");
		
		if (mimeType.equals("application/x-pki-message")) {
			BufferedInputStream is = new BufferedInputStream(in);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			int b;
			while ((b = is.read()) != -1) {
				baos.write(b);
			}

			final PkcsPkiEnvelopeParser envelopeParser = new PkcsPkiEnvelopeParser(keyPair.getPrivate());
			final PkiMessageParser parser = new PkiMessageParser(envelopeParser);
			PkiMessage msg = parser.parse(baos.toByteArray());
			
			LOGGER.exiting(getClass().getName(), "getContent", msg);
			return msg;
		} else {
			IOException ioe = new IOException("Invalid Content Type");
			
			LOGGER.throwing(getClass().getName(), "getContent", ioe);
			throw ioe;
		}
	}
}
