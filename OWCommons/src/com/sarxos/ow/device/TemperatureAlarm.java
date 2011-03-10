package com.sarxos.ow.device;

import java.io.Serializable;

public class TemperatureAlarm implements Alarm, Serializable {

	private static final long serialVersionUID = -3600565156777608005L;
	
	private double minTemperature = Integer.MIN_VALUE;
	private double maxTemperature = Integer.MIN_VALUE;
	
	public TemperatureAlarm() {
	}
	
	/**
	 * Alarm temperaturowy dla urzadzenia mierzacego temperature. Aby ustawic
	 * tylko jeden alarm (min lub max temp) wystarczy w drugie pole wpisac 
	 * wartosc <b>{@link Integer#MIN_VALUE}</b>. 
	 * @param minTemp - dolna temperatura alarmu
	 * @param maxTemp - gorna temperatura alarmu
	 */
	public TemperatureAlarm(double minTemp, double maxTemp) {
		this.minTemperature = minTemp;
		this.maxTemperature = maxTemp;
	}

	public double getMaxTemperature() {
		return maxTemperature;
	}

	public void setMaxTemperature(double maxTemperature) {
		this.maxTemperature = maxTemperature;
	}

	public double getMinTemperature() {
		return minTemperature;
	}

	public void setMinTemperature(double minTemperature) {
		this.minTemperature = minTemperature;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[min: " + getMinTemperature() +
			", max: " + getMaxTemperature() + "]";
	}
}
