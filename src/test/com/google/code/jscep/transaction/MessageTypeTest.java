package com.google.code.jscep.transaction;

import org.junit.Assert;
import org.junit.Test;

public class MessageTypeTest {
	@Test
	public void testValueOf() {
		for (MessageType msgType : MessageType.values()) {
			Assert.assertSame(msgType, MessageType.valueOf(msgType.getValue()));
		}
	}

}
