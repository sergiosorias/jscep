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
package org.jscep.pkcs7;

import org.bouncycastle.asn1.cms.ContentInfo;

/**
 * This class represents the SCEP <code>pkcsPKIEnvelope</code> structure.
 * 
 * @author David Grant
 */
public class PkcsPkiEnvelope extends ContentInfo {
	private MessageData msgData;
	
	public PkcsPkiEnvelope(ContentInfo info) {
		super(info.getContentType(), info.getContent());
	}
	
	void setMessageData(MessageData msgData) {
		this.msgData = msgData;
	}
	
	public MessageData getMessageData() {
		return msgData;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		sb.append("pkcsPkiEnvelope [\n");
		sb.append("\tcontentType: " + getContentType() + "\n");
		sb.append("\tmessageData: " + msgData.toString().replaceAll("\n", "\n\t") + "\n");
		sb.append("]");
		
		return sb.toString();
	}
}
