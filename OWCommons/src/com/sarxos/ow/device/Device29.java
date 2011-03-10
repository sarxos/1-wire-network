package com.sarxos.ow.device;

import java.rmi.RemoteException;

import com.dalsemi.onewire.container.OneWireContainer29;

public class Device29 extends AbstractDevice {

	private static final long serialVersionUID = -7931662048193749490L;

	public Device29(OneWireContainer29 owc) throws RemoteException {
		super(owc);
	}
}
