package com.google.code.jscep.request;

import java.io.IOException;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.code.jscep.response.Capability;

public class GetCACapsTest {
	private Request<Set<Capability>> fixture;
	private String caIdentifier;
	
	@Before
	public void setUp() {
		caIdentifier = "id";
		fixture = new GetCACaps(caIdentifier);
	}

	@Test
	public void testGetOperation() {
		Assert.assertSame(Operation.GetCACaps, fixture.getOperation());
	}

	@Test
	public void testGetMessage() throws IOException {
		Assert.assertEquals(caIdentifier, fixture.getMessage());
	}
	
	@Test
	public void testContentHandler() {
		Assert.assertNotNull(fixture.getContentHandler());
	}
}
