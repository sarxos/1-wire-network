package com.sarxos.ow.util;

import com.sarxos.ow.device.TemperatureAlarm;

public class AlarmFactory {

	public static TemperatureAlarm createTemperatureAlarm(Double min, Double max) {
		double minTemperature = min != null ? min : Integer.MIN_VALUE;
		double maxTemperature = max != null ? max : Integer.MIN_VALUE;
		TemperatureAlarm a = new TemperatureAlarm(minTemperature, maxTemperature);
		return a;
	}
	
	
}
