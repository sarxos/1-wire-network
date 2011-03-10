package com.sarxos.ow.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import com.sarxos.ow.MessageBroadcastListener;
import com.sarxos.ow.device.Device;
import com.sarxos.ow.device.DevicesArrivalListener;
import com.sarxos.ow.device.DevicesDepartureListener;
import com.sarxos.ow.device.event.DevicesArrivalEvent;
import com.sarxos.ow.device.event.DevicesDepartureEvent;
import com.sarxos.ow.device.event.MessageBroadcastEvent;
import com.sarxos.ow.device.rmi.RemoteDevice;
import com.sarxos.ow.rmi.RemoteOWUser;

public class RemoteOWUserImpl extends UnicastRemoteObject implements RemoteOWUser {

	private static final long serialVersionUID = 5953786065145877518L;
	
	private transient List <EventListener> arrivalsListeners = new ArrayList<EventListener>(); 
	private transient List <EventListener> departuresListeners = new ArrayList<EventListener>(); 
	private transient List <EventListener> broadcastListeners = new ArrayList<EventListener>(); 
	
	public RemoteOWUserImpl() throws RemoteException {
		super();
	}

	/* NOTE!
	 * Obluga listenerow.
	 */
	
	public void addDevicesArrivalListener(DevicesArrivalListener l) {
		arrivalsListeners.add(l);
	}
	
	public boolean removeDevicesArrivalListener(DevicesArrivalListener l) {
		return arrivalsListeners.remove(l);		
	}

	public DevicesArrivalListener[] getDevicesArrivalListeners() {
		return arrivalsListeners.toArray(new DevicesArrivalListener[] {});
	}

	public void addDevicesDepartureListener(DevicesDepartureListener l) {
		departuresListeners.add(l);
	}
	
	public boolean removeDevicesDepartureListener(DevicesDepartureListener l) {
		return departuresListeners.remove(l);		
	}

	public DevicesDepartureListener[] getDevicesDepartureListeners() {
		return departuresListeners.toArray(new DevicesDepartureListener[] {});
	}
	
	public void addMessageBroadcastListener(MessageBroadcastListener l) {
		broadcastListeners.add(l);
	}
	
	public boolean removeMessageBroadcastListener(MessageBroadcastListener l) {
		return broadcastListeners.remove(l);		
	}

	public MessageBroadcastListener[] getMessageBroadcastListeners() {
		return broadcastListeners.toArray(new MessageBroadcastListener[] {});
	}
	
	/* NOTE!
	 * Z interfejscu zdalnego.
	 */
	
	public void devicesArrival(List <RemoteDevice> devices) {
		DevicesArrivalEvent e = new DevicesArrivalEvent(this, devices);
		for(EventListener l : arrivalsListeners) {
			((DevicesArrivalListener)l).devicesArival(e);
		}
	}

	public void devicesDeparture(List <RemoteDevice> devices) {
		DevicesDepartureEvent e = new DevicesDepartureEvent(this, devices);
		for(EventListener l : departuresListeners) {
			((DevicesDepartureListener)l).devicesDeparture(e);
		}
	}

	public void displayMessage(String message) {
		MessageBroadcastEvent e = new MessageBroadcastEvent(this, message);
		for(EventListener l : broadcastListeners) {
			((MessageBroadcastListener)l).messageReceived(e);
		}
	}
}
