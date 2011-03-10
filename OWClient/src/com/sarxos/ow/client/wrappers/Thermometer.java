package com.sarxos.ow.client.wrappers;

import java.rmi.RemoteException;

import com.sarxos.ow.client.ApplicationContext;
import com.sarxos.ow.client.graph.OWGraph;
import com.sarxos.ow.device.TemperatureAlarm;
import com.sarxos.ow.device.event.TemperatureAlarmEvent;
import com.sarxos.ow.device.event.TemperatureChangeEvent;
import com.sarxos.ow.device.rmi.RemoteDevice;
import com.sarxos.ow.device.rmi.RemoteTemperatureDevice;
import com.sarxos.ow.device.rmi.RemoteTemperatureListener;

public class Thermometer extends AbstractDeviceWrapper 
	implements RemoteTemperatureListener {

	private transient RemoteTemperatureDevice thermo = null;
	private transient double temp = Integer.MIN_VALUE;
	private transient boolean alarm = false;
	
	public Thermometer(RemoteDevice device) throws RemoteException {
		super(device);
		
		this.thermo = (RemoteTemperatureDevice) device;
		try {
			this.thermo.addTemperatureListener(this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void temparatureAlarm(TemperatureAlarmEvent e) {
		TemperatureAlarm alarm = e.getTemperatureAlarm();
		double temp = e.getTemperature();
		double min = alarm.getMinTemperature();
		double max = alarm.getMaxTemperature();
		if (temp <= min) {
			System.err.print("Temperatura za niska: " + temp + " < " + min);
		} else if (temp >= max) {
			System.err.print("Temperatura za wysoka: " + temp + " > " + max);
		} else {
			assert false : "This should never happen";
		}
	}

	public void temparatureChange(TemperatureChangeEvent e) {
		this.temp = e.getNewTemperature();
		System.out.println("Nowa temperatura: " + temp + "'C");
		OWGraph graph = ApplicationContext.getInstance().getCurrentGraph(); 
		if (graph != null) {
			graph.repaint();
		}
	}

	public double getTemp() {
		if (temp == Integer.MIN_VALUE) {
			try {
				temp = this.thermo.getTemperature();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return temp;
	}

	public boolean isAlarm() {
		return alarm;
	}
}
