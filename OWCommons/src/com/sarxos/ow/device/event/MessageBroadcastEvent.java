package com.sarxos.ow.device.event;

import java.io.Serializable;
import java.util.EventObject;

import com.sarxos.ow.rmi.RemoteOWUser;

public class MessageBroadcastEvent extends EventObject implements Serializable {

	
	private static final long serialVersionUID = 8018256121058976019L;
	
	private String message = null;
	
	public MessageBroadcastEvent(RemoteOWUser source, String message) {
		super(source);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
