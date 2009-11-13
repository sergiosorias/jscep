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

package com.google.code.jscep.transaction;

/**
 * Object Identifiers used by SCEP
 * 
 * @link http://tools.ietf.org/html/draft-nourse-scep-19#appendix-B
 * @link http://www.oid-info.com/get/2.16.840.1.113733.1.9
 */
public enum ScepObjectIdentifiers {
	/**
	 * 2 16 US(840) 1 VeriSign(113733) pki(1) attributes(9) messageType(2)
	 */
    messageType("2.16.840.1.113733.1.9.2"),
    /**
	 * 2 16 US(840) 1 VeriSign(113733) pki(1) attributes(9) pkiStatus(3)
	 */
    pkiStatus("2.16.840.1.113733.1.9.3"),
    /**
     * 2 16 US(840) 1 VeriSign(113733) pki(1) attributes(9) failInfo(4)
     */
    failInfo("2.16.840.1.113733.1.9.4"),
    /**
     * 2 16 US(840) 1 VeriSign(113733) pki(1) attributes(9) senderNonce(5)
     */
    senderNonce("2.16.840.1.113733.1.9.5"),
    /**
     * 2 16 US(840) 1 VeriSign(113733) pki(1) attributes(9) recipientNonce(6)
     */
    recipientNonce("2.16.840.1.113733.1.9.6"),
    /**
     * 2 16 US(840) 1 VeriSign(113733) pki(1) attributes(9) transId(7)
     */
    transId("2.16.840.1.113733.1.9.7"),
    /**
     * 2 16 US(840) 1 VeriSign(113733) pki(1) attributes(9) pkiStatus(8)
     */
    extensionReq("2.16.840.1.113733.1.9.8");
    
    private final String oid;
    
    private ScepObjectIdentifiers(String oid) {
    	this.oid = oid;
    }
    
    /**
     * Returns the String representation of this OID.
     * 
     * @return the String representation of this OID.
     */
    public String getOid() {
    	return oid;
    }
}
