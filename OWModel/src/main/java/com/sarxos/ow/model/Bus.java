package com.sarxos.ow.model;

import java.util.List;


public interface Bus extends EventProcessor {

	public void addPipe(Pipe p);
	
	public void removePipe(Pipe p);
	
	/**
	 * Return list of the connected {@link Pipe} objects.
	 * @return Generic {@link List} of {@link Pipe}
	 */
	public List<Pipe> getPipes();

}
