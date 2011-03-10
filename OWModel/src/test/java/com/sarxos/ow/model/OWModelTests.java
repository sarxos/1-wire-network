package com.sarxos.ow.model;

import com.sarxos.ow.model.impl.AbstractDeviceTest;
import com.sarxos.ow.model.impl.AbstractEventTest;
import com.sarxos.ow.model.impl.NullDeviceTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class OWModelTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.sarxos.ow.model.impl");
		suite.addTestSuite(AbstractDeviceTest.class);
		suite.addTestSuite(AbstractEventTest.class);
		suite.addTestSuite(NullDeviceTest.class);
		return suite;
	}

}
