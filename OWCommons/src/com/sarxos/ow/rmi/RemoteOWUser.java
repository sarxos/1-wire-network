package com.sarxos.ow.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import com.sarxos.ow.device.Device;
import com.sarxos.ow.device.rmi.RemoteDevice;

/**
 * Remote OW user interface.
 * @author Bartosz Firyn (SarXos)
 */
public interface RemoteOWUser extends Remote {

	/**
	 * Broadcast message.
	 * @param message
	 * @throws RemoteException
	 */
	public void displayMessage(String message) throws RemoteException;
	
	/**
	 * Devices arrival.
	 * @param devices
	 * @throws RemoteException
	 */
	public void devicesArrival(List <RemoteDevice> devices) throws RemoteException;
	
	/**
	 * Devices departue.
	 * @param devices
	 * @throws RemoteException
	 */
	public void devicesDeparture(List <RemoteDevice> devices) throws RemoteException;
	
}
