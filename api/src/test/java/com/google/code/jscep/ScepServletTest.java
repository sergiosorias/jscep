package com.google.code.jscep;

import java.io.IOException;

import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.testing.ServletTester;
import org.junit.Before;
import org.junit.Test;

import com.google.code.jscep.request.Operation;


public class ScepServletTest {
	private ServletTester tester;
	
	@Before
	public void setUp() throws Exception {
		tester = new ServletTester();
		tester.setContextPath("/scep");
		tester.addServlet(ScepServletImpl.class, "/pkiclient.exe");
		tester.start();
	}
	
	@Test
	public void testFoo() throws IOException, Exception {
		final HttpTester req = new HttpTester();
		final HttpTester res = new HttpTester();
		
		req.setMethod("GET");
		req.setURI("/scep/pkiclient.exe?operation=" + Operation.GetCACaps);
		req.setVersion("HTTP/1.0");
		
		res.parse(tester.getResponses(req.generate()));
		res.getContent();
	}
}
