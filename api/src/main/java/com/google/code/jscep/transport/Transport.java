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
package com.google.code.jscep.transport;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.logging.Logger;

import com.google.code.jscep.request.Operation;
import com.google.code.jscep.request.Request;
import com.google.code.jscep.util.LoggingUtil;

/**
 * This class represents a transport for sending a message to the SCEP server.
 * <p>
 * Example usage:
 * <pre>
 * Request&lt;?&gt; req = ...;
 * URL url = new URL("http://www.example.org/scep/pki-client.exe");
 * Proxy proxy = Proxy.NO_PROXY;
 * Transport trans = Transport.createTransport(Transport.Method.POST, url, proxy);
 * Object res = trans.setMessage(req);
 * </pre>
 * 
 * @author David Grant
 */
public abstract class Transport {
	private static Logger LOGGER = LoggingUtil.getLogger("com.google.code.jscep.transport");
	/**
	 * Represents the <code>HTTP</code> method to be used for transport. 
	 */
	public enum Method {
		/**
		 * The <code>HTTP GET</code> method.
		 */
		GET,
		/**
		 * The <code>HTTP POST</code> method.
		 */
		POST
	}
	final URL url;
	final Proxy proxy;
	
	Transport(URL url, Proxy proxy) {
		this.url = url;
		this.proxy = proxy;
	}
	
	/**
	 * Returns the URL configured for use by this transport.
	 * 
	 * @return the URL.
	 */
	public URL getURL() {
		return url;
	}
	
	/**
	 * Returns the proxy configured for use by this <code>Transport</code>.
	 * <p>
	 * If no proxy was used to construct this <code>Transport</code>, this
	 * method returns <code>Proxy.NO_PROXY</code>.
	 * 
	 * @return the proxy.
	 */
	public Proxy getProxy() {
		return proxy;
	}
	
	/**
	 * Sends the given request to the URL provided in the constructor and
	 * uses the {@link Request}'s content handler to parse the response.  
	 * 
	 * @param <T> the response type.
	 * @param msg the message to send.
	 * @return the response of type T.
	 * @throws IOException if any I/O error occurs.
	 */
	abstract public <T> T sendMessage(Request<T> msg) throws IOException;
	
	/**
	 * Creates a new <code>Transport</code> of type <code>method</code> with the 
	 * provided URL over the provided proxy.
	 * 
	 * @param method the transport type.
	 * @param url the URL.
	 * @param proxy the proxy.
	 * @return a new Transport instance.
	 */
	public static Transport createTransport(Method method, URL url, Proxy proxy) {
		LOGGER.entering(Transport.class.getName(), "createTransport", new Object[] { method, url, proxy });
		
		final Transport t;
		
		if (method.equals(Method.GET)) {
			t = new HttpGetTransport(url, proxy);
		} else {
			t = new HttpPostTransport(url, proxy);
		}
		
		LOGGER.exiting(Transport.class.getName(), "createTransport", t);
		return t;
	}
	
	/**
	 * Creates a new <code>Transport</code> of type <code>method</code> with the 
	 * provided URL.
	 * 
	 * @param method the transport type.
	 * @param url the url.
	 * @return a new Transport instance.
	 */
	public static Transport createTransport(Method method, URL url) {
		LOGGER.entering(Transport.class.getName(), "createTransport", new Object[] {method, url});
		
		final Transport t = createTransport(method, url, Proxy.NO_PROXY);
		
		LOGGER.exiting(Transport.class.getName(), "createTransport", t);
		return t;
	}
	
	URL getUrl(Operation op) throws MalformedURLException {
		return new URL(url.toExternalForm() + "?operation=" + op);
	}
}
