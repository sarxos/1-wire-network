package com.sarxos.ow;

public class OWException extends Exception {

	public OWException() {
		super();
	}

	public OWException(String message, Throwable cause) {
		super(message, cause);
	}

	public OWException(String message) {
		super(message);
	}

	public OWException(Throwable cause) {
		super(cause);
	}
}
