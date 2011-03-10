package com.sarxos.ow.model.impl;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import com.sarxos.ow.model.Device;
import com.sarxos.ow.model.Event;
import com.sarxos.ow.model.Pipe;
import com.sarxos.ow.model.annotation.EventProcessorPolicy;
import com.sarxos.ow.model.annotation.ProcessEventClass;
import com.sarxos.ow.model.annotation.ProcessPolicy;


/**
 * This is simple implementation of {@link Device} interface.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public abstract class AbstractDevice implements Device {

	/**
	 * Set of incoming {@link Pipe}'s.
	 */
	private Set<Pipe> incoming = new HashSet<Pipe>();
	
	/**
	 * Set of outgoing {@link Pipe}'s.
	 */
	private Set<Pipe> ougoing = new HashSet<Pipe>();

	private Set<Class<? extends Event>> support = new HashSet<Class<? extends Event>>();
	private EventProcessorPolicy policy = EventProcessorPolicy.CLASS;
	
	/*
	 * Dynamic initializer.
	 */
	{
		Class<? extends AbstractDevice> c = this.getClass();
		
		ProcessPolicy pp = c.getAnnotation(ProcessPolicy.class);
		if (pp != null) {
			policy = pp.value();
		}
		
		Annotation[] annotations = c.getAnnotations();
		Annotation a = null;
		for (int i = 0; i < annotations.length; i++) {
			a = annotations[i];
			if (a instanceof ProcessEventClass) {
				ProcessEventClass epc = (ProcessEventClass) a;
				Class<? extends Event> clazz = epc.value();
				support.add(clazz);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.sarxos.ow.model.Device#getIncomingPipes()
	 */
	@Override
	public Set<Pipe> getIncomingPipes() {
		return incoming;
	}

	/* (non-Javadoc)
	 * @see com.sarxos.ow.model.Device#getOutgoingPipes()
	 */
	@Override
	public Set<Pipe> getOutgoingPipes() {
		return ougoing;
	}
	
	/* (non-Javadoc)
	 * @see com.sarxos.ow.model.EventProcessor#canProcess(com.sarxos.ow.model.Event)
	 */
	@Override
	public boolean canProcess(Event event) {
		switch (policy) {
			case CLASS: 
				return support.contains(event.getClass());
			case ALL:
				return true;
			case NONE:
				return false;
		}
		return false;
	}
}
