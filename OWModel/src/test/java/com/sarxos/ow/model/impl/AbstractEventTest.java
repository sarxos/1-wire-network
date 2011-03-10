package com.sarxos.ow.model.impl;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import com.sarxos.ow.model.Device;
import com.sarxos.ow.model.Event;
import com.sarxos.ow.model.ProcessException;


/**
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class AbstractEventTest extends TestCase {

	protected class TestDevice extends AbstractDevice {

		@Override
		public void process(Event event) throws ProcessException {
		}

		@Override
		public boolean canProcess(Event event) {
			return true;
		}
	}
	
	protected class TestEvent extends AbstractEvent {

		public TestEvent(Device source) {
			super(source);
		}
	}

	@Test
	public void test_construct() {
		Device source = null; 
		try {
			new TestEvent(source);
			Assert.assertTrue(false);
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void test_getSource() {
		Device source = new TestDevice(); 
		Event event = new TestEvent(source);
		
		Assert.assertSame(source, event.getSource());
	}
	
}
