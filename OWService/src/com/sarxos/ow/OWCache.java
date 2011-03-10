package com.sarxos.ow;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dalsemi.onewire.container.OneWireContainer;
import com.sarxos.ow.device.Device;
import com.sarxos.ow.device.DeviceException;
import com.sarxos.ow.sm.DirStorageManager;
import com.sarxos.ow.sm.ZipStorageManager;

public class OWCache implements Cache {

	private transient Controller controller = null;
	private transient Storage storageManager = null;
	
	private HashMap <String, Device> devicesmap = null;
	
	/**
	 * Przechowalnia urzadzen, ktore kiedykolwiek zostaly podlaczone do 
	 * magistrali OneWire.<br>
	 * @param controller
	 * @throws StorageManagerException 
	 */
	public OWCache(Controller controller) throws StorageManagerException {
		
		if(controller == null) {
			throw new IllegalArgumentException(
					OWController.class.getSimpleName() + " object in " + 
					OWCache.class.getSimpleName() + " can't be null!"
			);
		}
		
		this.storageManager = getStorageManager();
		
		setController(controller);
		loadStorage();
	}

	/**
	 * Zwraca kontroller dla ktorego zostala utworzona przechowalnia.<br>
	 * @return OWController
	 */
	public Controller getController() {
		return controller;
	}
	
	private void setController(Controller controller) {
		this.controller = controller;
	}
	
	protected Storage getStorageManager() {
		if(storageManager == null) {
			//storageManager = new ZipStorageManager(this);
			storageManager = new DirStorageManager(this);
		}
		return storageManager;
	}
	
	public void setStorageManager(Storage storageManager) throws StorageManagerException {
		this.storageManager = storageManager;
		loadStorage();
	}

	protected void loadStorage() throws StorageManagerException {
		assert storageManager != null;
		List <Device> devices = storageManager.loadAll(); 
		devicesmap = new HashMap<String, Device>((int)(devices.size() * 1.25 + 1), 0.75f); 
		for(Device d : devices) {
			devicesmap.put(d.getAddress(), d);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.sarxos.onewire.Cache#createNewDevice(com.dalsemi.onewire.container.OneWireContainer)
	 */
	public Device createNewDevice(OneWireContainer container, Controller controller) 
	throws DeviceException {
		
		if(container == null) {
			throw new IllegalArgumentException(
					"Can't create new device on base of null container object."
			);
		}

		Class c = null;
		try {
			c = getDeviceClassForContainer(container);
		} catch (ClassNotFoundException e) {
			throw new DeviceException(
					"Nie znaleziono klasy dla urzadzenia " + container.getAddressAsString(), e
			);
		}
		
		Constructor <Device> cc = null;
		try {
			cc = c.getConstructor(new Class[] {container.getClass()});
		} catch (SecurityException e) {
			throw new DeviceException(
					"Brak uprawnien do wywolania konstruktora urzadzenia " + c.getSimpleName(), e
			);
		} catch (NoSuchMethodException e) {
			throw new DeviceException(
					"Brak konstruktora klasy urzadzenia " + c.getSimpleName(), e
			);
		}
		
		Device d = null;
		try {
			d = cc.newInstance(new Object[] {container});
			d.init(container, controller);
		} catch (Exception e) {
			throw new DeviceException(
					"Nie udalo sie utworzyc obiektu dla urzadzenia " + c.getSimpleName(), e
			);
		}
		
		try {
			getStorageManager().save(d);
		} catch (Exception e) {
			throw new DeviceException(
					"Nie udalo sie zapisac deskryptora urzadzenia " + c.getSimpleName(), e
			);
		}
		
		return d;
	}
	
	protected Class getDeviceClassForContainer(OneWireContainer container) 
	throws ClassNotFoundException {
		String family = container.getAddressAsString().substring(14);
		String deviceClassName = Device.class.getName() + family;
		return Class.forName(deviceClassName);
	}
	
	/* (non-Javadoc)
	 * @see com.sarxos.onewire.Cache#getDevice(com.dalsemi.onewire.container.OneWireContainer)
	 */
	public Device getDevice(OneWireContainer container) {
		if(container == null) {
			throw new IllegalArgumentException("OneWireContainer for device can't be null.");
		}
		Device d = devicesmap.get(container.getAddressAsString());
		if(d == null) {
			try {
				d = createNewDevice(container, getController());
			} catch (DeviceException e) {
				getController().getService().exceptionOccured(e);
			}
		}
		d.init(container, getController());
		return d;
	}

	public Map<String, Device> getDevices() {
		assert devicesmap != null;
		return devicesmap;
	}
}
