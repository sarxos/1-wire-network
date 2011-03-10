package com.sarxos.ow.client.wrappers;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.sarxos.ow.device.rmi.RemoteDevice;

/**
 * Local abstract device wrapper.
 * @author Bartosz Firyn (SarXos)
 */
public abstract class AbstractDeviceWrapper extends UnicastRemoteObject
	implements Serializable {

	private transient RemoteDevice device = null;
	private transient String address = null; 
	private transient String label = null;
	private transient String script = null;
	private transient boolean enabled = false;
	
	public AbstractDeviceWrapper(RemoteDevice device) throws RemoteException {
		init(device);
	} 

	protected void init(RemoteDevice device) {
		this.device = device;
		try {
			this.address = device.getAddress();
			this.label = device.getLabel();
			this.script = device.getScript();
			this.enabled = device.isEnabled();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	protected RemoteDevice getDevice() {
		return device;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
		try {
			this.device.setLabel(label);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
		try {
			this.device.setScript(script);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public String getAddress() {
		return address;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		try {
			this.device.setEnabled(enabled);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "[Device " + address + "]";
	}
}
