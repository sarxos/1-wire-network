package com.sarxos.ow.device.event;

import java.io.Serializable;
import java.util.EventObject;

import com.sarxos.ow.device.TemperatureAlarm;
import com.sarxos.ow.device.rmi.RemoteTemperatureDevice;

public class TemperatureAlarmEvent extends EventObject implements Serializable {

	private static final long serialVersionUID = -2034647413349478816L;
	
	private double temperature = 0;
	private TemperatureAlarm temperatureAlarm = null;
	
	public TemperatureAlarmEvent(RemoteTemperatureDevice device, double temp, TemperatureAlarm alarm) {
		super(device);
		this.temperature = temp;
		this.temperatureAlarm = alarm;
	}

	public TemperatureAlarm getTemperatureAlarm() {
		return temperatureAlarm;
	}

	public double getTemperature() {
		return temperature;
	}
}
