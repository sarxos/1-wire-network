package com.sarxos.ow.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sarxos.ow.Controller;
import com.sarxos.ow.Service;
import com.sarxos.ow.device.Device;
import com.sarxos.ow.device.rmi.RemoteDevice;

public class RemoteOWServiceImpl extends UnicastRemoteObject implements RemoteOWService {

	private static final long serialVersionUID = 8137293601326762888L;
	
	private transient Service owService = null; 
	private List <RemoteOWUser> users = new ArrayList<RemoteOWUser>();
	
	/**
	 * Zdalna instancja serwisu OW.
	 * @param owService
	 * @throws RemoteException
	 */
	public RemoteOWServiceImpl(Service owService) throws RemoteException {
		super();

		if(owService == null) {
			throw new IllegalArgumentException(
					Service.class.getSimpleName() + " in " + getClass().getSimpleName() + " " +
					"can't be null."
			);
		}
		
		this.owService = owService;
	}

	// TODO: zaimplementowac funkcjonalnosc FutureTask zeby inwokacje metod klienckich odbywaly sie poprzez odseparowane watki wywolan.
	// TODO: wprowadzic wywolania asynchroniczne dla metod klienckich typu non-void.
	
	public void devicesArrival(List <Device> devices) {
		for(RemoteOWUser user : users) {
			try {
				List <RemoteDevice> remotes = new ArrayList<RemoteDevice>((int) (devices.size() * 1.5));
				for (Device d : devices) {
					remotes.add((RemoteDevice) d);
				}
				user.devicesArrival(remotes);
			} catch (RemoteException e) {
				owService.exceptionOccured(e);
			}
		}
	}

	public void devicesDeparture(List <Device> devices) {
		for(RemoteOWUser user : users) {
			try {
				List <RemoteDevice> remotes = new ArrayList<RemoteDevice>((int) (devices.size() * 1.5));
				for (Device d : devices) {
					remotes.add((RemoteDevice) d);
				}
				user.devicesDeparture(remotes);
			} catch (RemoteException e) {
				owService.exceptionOccured(e);
			}
		}
	}

	public void broadcastMessage(String message) {
		for(RemoteOWUser user : users) {
			try {
				user.displayMessage(message);
			} catch (RemoteException e) {
				owService.exceptionOccured(e);
			}
		}
	}
	
	public List <RemoteOWUser> getUsers() {
		return users;
	}

	public boolean addUser(RemoteOWUser user) {
		return users.add(user);
	}

	public boolean removeUser(RemoteOWUser user) {
		return users.remove(user);
	}

	/**
	 * This method return only enabled devices.
	 * @see com.sarxos.ow.rmi.RemoteOWService#getAllDevices()
	 */
	public List<RemoteDevice> getAllDevices() {
		Controller controller = this.owService.getController();
		List <RemoteDevice> devices = new ArrayList<RemoteDevice>();
		Map<String, Device> devicesMap = controller.getCache().getDevices();
		for (Device d : devicesMap.values()) {
			if (d.isEnabled()) {
				devices.add((RemoteDevice) d);
			}
		}
		
		return devices;
	}
	
	
}
