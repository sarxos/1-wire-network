package com.sarxos.ow.device.event;

import java.io.Serializable;
import java.util.EventObject;

import com.sarxos.ow.device.rmi.RemoteTemperatureDevice;

public class TemperatureChangeEvent extends EventObject implements Serializable {

	private static final long serialVersionUID = 5592750221542602438L;
	
	private double oldTemperature = Integer.MIN_VALUE;
	private double newTemperature = Integer.MIN_VALUE;
	
	public TemperatureChangeEvent(RemoteTemperatureDevice device, double oldTemp, double newTemp) {
		super(device);
		this.oldTemperature = oldTemp;
		this.newTemperature = newTemp;
	}

	public double getOldTemperature() {
		return oldTemperature;
	}

	public double getNewTemperature() {
		return newTemperature;
	}
}
