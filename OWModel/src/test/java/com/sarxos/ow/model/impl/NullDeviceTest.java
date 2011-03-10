package com.sarxos.ow.model.impl;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sarxos.ow.model.Device;
import com.sarxos.ow.model.Event;
import com.sarxos.ow.model.ProcessException;


/**
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class NullDeviceTest extends TestCase {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test_process() {
		Device source = new NullDevice();
		Device target = new NullDevice();
		
		Event event = new AbstractEvent(source) {}; 
		try {
			target.process(event);
			Assert.assertTrue(true);
		} catch (ProcessException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}
}
