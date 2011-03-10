package com.sarxos.ow.device.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteDevice extends Remote {

	public String getAddress() throws RemoteException;
	public String getLabel() throws RemoteException;
	public void setLabel(String label) throws RemoteException;
	public boolean isEnabled() throws RemoteException;
	public void setEnabled(boolean enabled) throws RemoteException;
	public void setScript(String script) throws RemoteException;
	public String getScript() throws RemoteException;
	
}
