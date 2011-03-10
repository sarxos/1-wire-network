package com.sarxos.ow;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.dalsemi.onewire.application.monitor.DeviceMonitorEvent;
import com.dalsemi.onewire.container.OneWireContainer;
import com.sarxos.ow.device.Device;


public aspect OWControllerTransparency {

	pointcut devicesArrival(OWController owc, DeviceMonitorEvent e) :	
		execution(void OWController.deviceArrival(DeviceMonitorEvent)) && 
		target(owc)	&& 
		args(e); 

	pointcut devicesDeparture(OWController owc, DeviceMonitorEvent e) : 
		execution(void OWController.deviceDeparture(DeviceMonitorEvent)) &&
		target(owc)	&& 
		args(e); 

	pointcut alarmBegin(OWController owc, DeviceAlarmEvent e) : 
		execution(void OWController.alarmBegin(DeviceAlarmEvent)) &&
		target(owc)	&& 
		args(e); 

	after(OWController owc, DeviceMonitorEvent e) : devicesArrival(owc, e) {
		int n = e.getDeviceCount();
		List <Device> devices = new ArrayList<Device>((int)(n * 1.25)); 
		for(int i = 0; i < n; i++) {
			OneWireContainer container = e.getContainerAt(i);
			Device d = owc.getCache().getDevice(container);
			devices.add(d);
		}
		if(devices.size() > 0) {
			OWService ows = (OWService) owc.getService(); 
			try {
				ows.getRemote().devicesArrival(devices);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	after(OWController owc, DeviceMonitorEvent e) : devicesDeparture(owc, e) {
		int n = e.getDeviceCount();
		List <Device> devices = new ArrayList<Device>((int)(n * 1.25)); 
		for(int i = 0; i < n; i++) {
			OneWireContainer container = e.getContainerAt(i);
			Device d = owc.getCache().getDevice(container);
			devices.add(d);
		}
		if(devices.size() > 0) {
			OWService ows = (OWService) owc.getService(); 
			try {
				ows.getRemote().devicesDeparture(devices);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	after(OWController owc, DeviceAlarmEvent e) : alarmBegin(owc, e) {
		int n = e.getDeviceCount();
		List <Device> devices = new ArrayList<Device>((int)(n * 1.25)); 
		for(int i = 0; i < n; i++) {
			OneWireContainer container = e.getContainerAt(i);
			Device d = owc.getCache().getDevice(container);
			devices.add(d);
		}
		if(devices.size() > 0) {
			// TODO: implement alarm behavior 
//			OWService ows = (OWService) owc.getService(); 
//			try {
//				//ows.getRemote().devicesDeparture(devices);
//				
//			} catch (RemoteException e1) {
//				e1.printStackTrace();
//			}
		}
	}
}
