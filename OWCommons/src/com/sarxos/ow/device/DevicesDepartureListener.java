package com.sarxos.ow.device;

import java.util.EventListener;

import com.sarxos.ow.device.event.DevicesDepartureEvent;

public interface DevicesDepartureListener extends EventListener {

	public void devicesDeparture(DevicesDepartureEvent e);
	
}
