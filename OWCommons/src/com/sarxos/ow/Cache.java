package com.sarxos.ow;

import java.util.Map;

import com.dalsemi.onewire.container.OneWireContainer;
import com.sarxos.ow.device.Device;
import com.sarxos.ow.device.DeviceException;

public interface Cache {

	public Device createNewDevice(OneWireContainer container, Controller c) throws DeviceException;

	public Device getDevice(OneWireContainer container);

	public Map <String, Device> getDevices();
}