package com.sarxos.ow;

public class ServiceShutodownHook extends Thread {

	private Service service = null;
	
	public ServiceShutodownHook(Service service) {
		this.service = service;
	}
	
	@Override
	public void run() {
		
		System.out.println("attempting stop");
		
		if(service instanceof OWService) {
			OWService ows = (OWService) service;
			Process rmiProcess = ows.getRMIProcess();
			if(rmiProcess != null) {
				rmiProcess.destroy();
			}
		}
		
		service.stopService();
	}
	
}
