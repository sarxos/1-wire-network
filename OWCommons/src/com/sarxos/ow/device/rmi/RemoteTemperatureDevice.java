package com.sarxos.ow.device.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;



public interface RemoteTemperatureDevice extends Remote {

	public boolean addTemperatureListener(RemoteTemperatureListener l) throws RemoteException;
	public boolean removeTemperatureListener(RemoteTemperatureListener l) throws RemoteException;
	public RemoteTemperatureListener[] getTemperatureListeners() throws RemoteException;
	public double getTemperature() throws RemoteException;
}
