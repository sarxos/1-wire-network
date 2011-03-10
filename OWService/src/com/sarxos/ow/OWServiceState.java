package com.sarxos.ow;

/**
 * SarXos 1-Wire service states enumeration.
 * @author Bartosz Firyn (SarXos)
 * @see OWService#getState()
 */
public enum OWServiceState {
	
	/**
	 * Service is disabled. 
	 */
	DISABLED,
	
	/**
	 * Service is in initialization state.
	 */
	INITIALIZATION,
	
	/**
	 * Service is running. 
	 */
	RUNNING,
	
	/**
	 * Service is paused.
	 */
	PAUSED,
	
	/**
	 * Servcie fatal error - can't be started.
	 */
	FATAL;
}
