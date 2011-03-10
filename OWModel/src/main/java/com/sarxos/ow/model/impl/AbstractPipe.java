package com.sarxos.ow.model.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import com.sarxos.aspectj.RunInSeparateThread;
import com.sarxos.aspectj.SeparateThreadExecution;
import com.sarxos.ow.model.Device;
import com.sarxos.ow.model.Event;
import com.sarxos.ow.model.Pipe;
import com.sarxos.ow.model.Transport;
import com.sarxos.ow.model.Transporter;


/**
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class AbstractPipe implements Pipe {

	private AtomicReference<Device> sourceRef = new AtomicReference<Device>(new NullDevice());
	private AtomicReference<Device> targetRef = new AtomicReference<Device>(new NullDevice());
	private Transporter transporter = Transporter.getInstance(); 

	protected List<Future<Void>> futures = new LinkedList<Future<Void>>();

	
	public AbstractPipe() {
	}

	public AbstractPipe(Device source, Device target) {
		setSource(source);
		setTarget(target);
	}

	@Override
	public Device getSource() {
		return sourceRef.get();
	}

	@Override
	public Device getTarget() {
		return targetRef.get();
	}

	@Override
	public void setSource(Device source) {
		if (source == null) {
			throw new IllegalArgumentException("Source device cannot be null.");
		}
		synchronized (sourceRef) {
			Device device = this.sourceRef.getAndSet(source);
			if (device != source) {
				device.getOutgoingPipes().remove(this);
				source.getOutgoingPipes().add(this);
			}
		}
	}

	@Override
	public void setTarget(Device target) {
		if (target == null) {
			throw new IllegalArgumentException("Target device cannot be null.");
		}
		synchronized (targetRef) {
			Device device = this.targetRef.getAndSet(target);
			if (device != target) {
				device.getIncomingPipes().remove(this);
				target.getIncomingPipes().add(this); 
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.sarxos.ow.model.EventProcessor#process(com.sarxos.ow.model.Event)
	 */
	@Override
	public void process(Event e) {
		
		Transport transport = new Transport(this.getTarget(), e);
		Future<Void> future = this.transporter.execute(transport);

		/* NOTE!
		 * First execute the futures list cleanup (this happen in
		 * separate Thread).
		 */
		cleanupFuturesList();

		this.futures.add(future);
	}
	
	/**
	 * Clean futures which has been done. This method is executed in
	 * separate {@link Thread} via {@link ExecutorService}. Because 
	 * this method operates on the {@link ListIterator} instead of normal
	 * {@link Iterator}, it doesn't need synchronization.
	 * 
	 * @see RunInSeparateThread
	 * @see SeparateThreadExecution
	 */
	@RunInSeparateThread
	protected void cleanupFuturesList() {
		ListIterator<Future<Void>> i = this.futures.listIterator();
		while (i.hasNext()) {
			Future<Void> f = i.next(); 
			if(f.isDone()) {
				i.remove();
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.sarxos.ow.model.Pipe#getTransports()
	 */
	@Override
	public List<Future<Void>> getTransports() {
		return futures;
	}

	/* (non-Javadoc)
	 * @see com.sarxos.ow.model.EventProcessor#canProcess(com.sarxos.ow.model.Event)
	 */
	@Override
	public boolean canProcess(Event event) {
		return true;
	}
}
