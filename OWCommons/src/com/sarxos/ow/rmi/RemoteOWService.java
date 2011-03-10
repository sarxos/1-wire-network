package com.sarxos.ow.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import com.sarxos.ow.device.Device;
import com.sarxos.ow.device.rmi.RemoteDevice;
import com.sarxos.ow.rmi.RemoteOWUser;

public interface RemoteOWService extends Remote {

	public void devicesArrival(List <Device> devices) throws RemoteException;
	public void devicesDeparture(List <Device> devices) throws RemoteException;
	public void broadcastMessage(String message) throws RemoteException;
	
	public List <RemoteOWUser> getUsers() throws RemoteException;
	public boolean addUser(RemoteOWUser user) throws RemoteException;
	public boolean removeUser(RemoteOWUser user) throws RemoteException;
	
	public List <RemoteDevice> getAllDevices() throws RemoteException;
}
