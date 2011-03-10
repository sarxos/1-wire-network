package com.sarxos.ow.device;

import java.util.EventListener;

import com.sarxos.ow.device.event.DevicesArrivalEvent;

public interface DevicesArrivalListener extends EventListener {

	public void devicesArival(DevicesArrivalEvent e);
	
}
