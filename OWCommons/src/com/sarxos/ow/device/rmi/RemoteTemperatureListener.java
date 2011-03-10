package com.sarxos.ow.device.rmi;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;

import com.sarxos.ow.device.event.TemperatureAlarmEvent;
import com.sarxos.ow.device.event.TemperatureChangeEvent;


public interface RemoteTemperatureListener extends Remote, EventListener, Serializable {

	public void temparatureChange(TemperatureChangeEvent e) throws RemoteException;
	public void temparatureAlarm(TemperatureAlarmEvent e) throws RemoteException;
	
}
