package com.sarxos.ow.device.event;

import java.io.Serializable;
import java.util.EventObject;
import java.util.List;

import com.sarxos.ow.device.rmi.RemoteDevice;
import com.sarxos.ow.rmi.RemoteOWUser;

public class DevicesArrivalEvent extends EventObject implements Serializable {

	
	private static final long serialVersionUID = -4724448913039138489L;
	
	private List <RemoteDevice> devices = null;
	
	public DevicesArrivalEvent(RemoteOWUser source, List <RemoteDevice> devices) {
		super(source);
		this.devices = devices;
	}

	public List<RemoteDevice> getDevices() {
		return devices;
	}
}
