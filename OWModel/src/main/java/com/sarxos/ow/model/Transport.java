package com.sarxos.ow.model;

import java.util.concurrent.Callable;



/**
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Transport implements Callable<Void> {

	private Device device;
	private Event event;
	
	public Transport(Device device, Event event) {
		this.device = device;
		this.event = event;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Void call() {
		try {
			this.device.process(this.event);
		} catch (ProcessException e) {
			e.printStackTrace();
		}
		return null; 
	}
}
