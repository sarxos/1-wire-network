package com.sarxos.ow.client;

public class ShutdownHook extends Thread {

	private OWClient owc = null;
	
	public ShutdownHook(OWClient owc) {
		this.owc = owc;
	}	
	
	@Override
	public void run() {
		
		super.run();
		
		Process rmi = owc.getRMIProcess();
		if(rmi != null) {
			rmi.destroy();
		}
	}
}
