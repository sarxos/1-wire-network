package com.sarxos.ow.device;

public class DeviceException extends Exception {

	private static final long serialVersionUID = 1L;

	public DeviceException() {
		super();
	}

	public DeviceException(String message) {
		super(message);
	}

	public DeviceException(Throwable cause) {
		super(cause);
	}

	public DeviceException(String message, Throwable cause) {
		super(message, cause);
	}
}
