package com.sarxos.ow.model.impl;

import java.util.Set;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sarxos.ow.model.Device;
import com.sarxos.ow.model.Event;
import com.sarxos.ow.model.Pipe;
import com.sarxos.ow.model.ProcessException;


/**
 * {@link AbstractDevice} test case.
 * @author Bartosz Firyn (SarXos)
 */
public class AbstractDeviceTest extends TestCase {

	/**
	 * {@link Device} test class.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	protected class TestDevice extends AbstractDevice {

		private boolean processed = false; 
		
		@Override
		public void process(Event event) throws ProcessException {
			this.processed = true;
		}
		
		public boolean isProcessed() {
			return processed;
		}

		@Override
		public boolean canProcess(Event event) {
			return true;
		}
	}

	/**
	 * {@link Event} test class.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	protected class TestEvent extends AbstractEvent {

		public TestEvent(Device source) {
			super(source);
		}
	}
	
	/**
	 * {@link Pipe} test class.
	 * 
	 * @author Bartosz Firyn (SarXos)
	 */
	protected class TestPipe extends AbstractPipe {

		public TestPipe(Device source, Device target) {
			super(source, target);
		}
	}
	
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
		Device source = new TestDevice();
		Device target = new TestDevice();
		
		Event event = new TestEvent(source);
		
		try {
			target.process(event);
			Assert.assertTrue(true);
		} catch (ProcessException e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}
	
	@Test
	public void test_getIncomingPipes() {
		
		Device sourceA = new NullDevice(); 
		Device sourceB = new NullDevice();
		
		Device target = new TestDevice();
		
		Pipe pipeA = new TestPipe(sourceA, target); 
		Pipe pipeB = new TestPipe(sourceB, target); 
		
		Set<Pipe> incoming = target.getIncomingPipes();

		Assert.assertNotNull(incoming);

		Assert.assertTrue(incoming.contains(pipeA));
		Assert.assertTrue(incoming.contains(pipeB));
		Assert.assertTrue(incoming.size() == 2);
		
		Assert.assertSame(pipeA.getSource(), sourceA);
		Assert.assertSame(pipeB.getSource(), sourceB);
		
		Assert.assertSame(pipeA.getTarget(), target);
		Assert.assertSame(pipeB.getTarget(), target);
		
		try {
			pipeA.setTarget(null);
			Assert.assertTrue(false);
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void test_getOutgoingPipes() {
		
		Device targetA = new NullDevice(); 
		Device targetB = new NullDevice();
		Device targetC = new NullDevice();
		
		Device source = new TestDevice();
		
		Pipe pipeA = new TestPipe(source, targetA); 
		Pipe pipeB = new TestPipe(source, targetB); 
		Pipe pipeC = new TestPipe(source, targetC); 
		Pipe pipeD = new TestPipe(targetA, targetC); 
		
		Set<Pipe> outgoing = source.getOutgoingPipes();
		
		Assert.assertNotNull(outgoing);
		
		Assert.assertTrue(outgoing.contains(pipeA));
		Assert.assertTrue(outgoing.contains(pipeB));
		Assert.assertTrue(outgoing.contains(pipeC));
		Assert.assertTrue(outgoing.size() == 3);
		
		Assert.assertTrue(!outgoing.contains(pipeD));
		
		
	}
}
