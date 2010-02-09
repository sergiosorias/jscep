package com.google.code.jscep.pkcs9;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class ChallengePasswordTest {
	private String password = "password";
	private ChallengePassword fixture;
	
	@Before
	public void setup() {
		this.fixture = new ChallengePassword(password);
	}
	
	@Test
	public void testGetPassword() {
		Assert.assertEquals(password, fixture.getPassword());
	}

}
