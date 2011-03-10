package com.sarxos.ow.device.event;

import java.io.Serializable;
import java.util.EventObject;
import java.util.List;

import com.sarxos.ow.device.Device;
import com.sarxos.ow.device.rmi.RemoteDevice;
import com.sarxos.ow.rmi.RemoteOWUser;

public class DevicesDepartureEvent extends EventObject implements Serializable {

	
	private static final long serialVersionUID = 4546111731648809195L;
	
	private List <RemoteDevice> devices = null;
	
	public DevicesDepartureEvent(RemoteOWUser source, List <RemoteDevice> devices) {
		super(source);
		this.devices = devices;
	}

	public List<RemoteDevice> getDevices() {
		return devices;
	}
}
