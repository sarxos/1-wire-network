package com.sarxos.ow.device;

public interface TemperatureDevice {

	public double measTemperature();
	public double getTemperature();
	
	public double getMaxTemperature();
	public double getMinTemperature();

	public double getTemperatureResolution();
	public void setTemperatureResolution(double resolution);
	public double[] getTemperatureResolutions();
	
	public boolean hasTemperatureResolutions();
	public boolean hasTemperatureAlarms();
	
	public void setTemperatureAlarm(TemperatureAlarm a);
	public TemperatureAlarm getTemperatureAlarm();
	
	
}
