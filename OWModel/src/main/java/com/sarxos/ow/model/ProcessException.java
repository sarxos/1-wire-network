/**
 * 
 */
package com.sarxos.ow.model;


/**
 * @author Bartosz Firyn (SarXos)
 */
public class ProcessException extends Exception {

	private static final long serialVersionUID = 7902490870722169421L;

	/**
	 * 
	 */
	public ProcessException() {
		super();
	}

	/**
	 * @param message
	 */
	public ProcessException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ProcessException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ProcessException(String message, Throwable cause) {
		super(message, cause);
	}

}
