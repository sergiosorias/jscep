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

package com.google.code.jscep;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.Callable;

import com.google.code.jscep.transaction.PkiStatus;

public class EnrollmentResult {
	private List<X509Certificate> certs;
	private Callable<EnrollmentResult> task;
	private String message;
	private PkiStatus status;
	
	EnrollmentResult(List<X509Certificate> certs) {
		this.certs = certs;
		this.status = PkiStatus.SUCCESS;
	}
	
	EnrollmentResult(Callable<EnrollmentResult> task) {
		this.task = task;
		this.status = PkiStatus.PENDING;
	}
	
	EnrollmentResult(String message) {
		this.message = message;
		this.status = PkiStatus.FAILURE;
	}
	
	public List<X509Certificate> getCertificates() {
		return certs;
	}
	
	public Callable<EnrollmentResult> getTask() {
		return task;
	}
	
	public String getMessage() {
		return message;
	}
	
	public PkiStatus getStatus() {
		return status; 
	}
}
