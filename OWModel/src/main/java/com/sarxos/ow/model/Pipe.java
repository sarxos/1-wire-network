package com.sarxos.ow.model;

import java.util.List;
import java.util.concurrent.Future;


/**
 * This interface represents Pipe object. It has target and source.
 * {@link Event}'s are passed from source to target.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public interface Pipe extends EventProcessor {

	/**
	 * Set source {@link Device}.
	 * 
	 * @param d - new source {@link Device}.
	 */
	public void setSource(Device d);
	
	/**
	 * Set target {@link Device}.
	 * 
	 * @param d - new target {@link Device}.
	 */
	public void setTarget(Device d);
	
	/**
	 * Get source {@link Device}.
	 * 
	 * @return {@link Device}
	 */
	public Device getSource();
	
	/**
	 * Get target {@link Device}.
	 * 
	 * @return {@link Device}
	 */
	public Device getTarget();
	
	/**
	 * Return the list of currently processing futures. Some of them could be done. 
	 * 
	 * @return List of {@link Future} objects.
	 */
	public List<Future<Void>> getTransports();
}
