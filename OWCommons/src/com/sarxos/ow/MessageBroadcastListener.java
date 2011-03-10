package com.sarxos.ow;

import java.util.EventListener;

import com.sarxos.ow.device.event.MessageBroadcastEvent;

public interface MessageBroadcastListener extends EventListener {

	public void messageReceived(MessageBroadcastEvent e);
	
}
