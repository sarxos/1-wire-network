package com.sarxos.ow.model;

import java.util.Set;


/**
 * @author Bartosz Firyn (SarXos)
 */
public interface Device extends EventProcessor {

	/**
	 * @return The list of incoming {@link Pipe} objects.
	 */
	public Set<Pipe> getIncomingPipes();
	
	/**
	 * @return The list of outgoing {@link Pipe} objects.
	 */
	public Set<Pipe> getOutgoingPipes();
	
}
