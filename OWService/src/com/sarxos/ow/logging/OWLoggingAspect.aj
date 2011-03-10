package com.sarxos.ow.logging;

import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.dalsemi.onewire.OneWireException;
import com.dalsemi.onewire.adapter.DSPortAdapter;
import com.dalsemi.onewire.application.monitor.DeviceMonitorEvent;
import com.dalsemi.onewire.container.OneWireContainer;
import com.sarxos.ow.OWController;
import com.sarxos.ow.OWService;
import com.sarxos.ow.device.Device;


/**
 * Aspekt logowania zdarzen.<br> 
 * @author Bartosz Firyn (SarXos)
 */
public aspect OWLoggingAspect {

	protected static OutputStream out = System.out;
	
	protected static final Logger controllerLogger = Logger.getLogger(OWController.class);
	protected static final Logger serviceLogger = Logger.getLogger(OWService.class);
	static {
		PropertyConfigurator.configure("owd.properties");
	}
	
	/**
	 * Pointcut okreslajacy miejsca w kodzie w ktorych nastepuje wykrycie/usuniecie 
	 * urzadzenia oraz jego wpisanie/usuniecie z mapy urzadzen.<br>
	 * @param owc
	 * @param e
	 */
	pointcut deviceChanged(OWController owc, DeviceMonitorEvent e) :	
		(
			execution(void OWController.deviceArrival(DeviceMonitorEvent)) 
			|| execution(void OWController.deviceDeparture(DeviceMonitorEvent))
		) && 
		target(owc)	&& 
		args(e); 

	pointcut scanIntervalChanged(OWService ows, int t) :
		execution(void OWService.setScanInterval(int)) &&
		target(ows) &&
		args(t);
	
	pointcut serviceStarted(OWService ows) : 
		execution(void OWService.startService()) &&
		target(ows);

	pointcut servicePaused() : 
		execution(void OWService.pauseService());

	pointcut serviceResumed() : 
		execution(void OWService.resumeService());

	pointcut serviceStopped() : 
		execution(void OWService.stopService());

	pointcut rmiProcessExecuted() :
		execution(boolean OWService.executeRMIRegistryProcess());
	
	pointcut adapterInitiated(OWService ows) :
		execution(DSPortAdapter OWService.findAdapter()) &&
		target(ows);
	
	pointcut deviceStarted(OWController owc, Device d) :
		execution(void OWController.startDevice(Device)) &&
		target(owc) &&
		args(d);

	pointcut deviceStopped(OWController owc, Device d) :
		execution(void OWController.stopDevice(Device)) &&
		target(owc) &&
		args(d);

	
	public OWLoggingAspect() {
	}
	
	/**
	 * Rada tworzaca log przed dodaniem/usunieciem urzadzenia z mapy urzadzen.<br>
	 * @param owc
	 * @param e
	 */
	before(OWController owc, DeviceMonitorEvent e) : deviceChanged(owc, e) {

		int n = e.getDeviceCount();
		String t = (e.getEventType() == DeviceMonitorEvent.ARRIVAL ? "connection" : "disconnection");
		StringBuffer messageBuffer = new StringBuffer("Device " + t + " event - found " + n + " devices:\n");

		for(int i = 0; i < n; i++) {
			OneWireContainer container = e.getContainerAt(i);
			String nl = (i == n - 1 ? "" : "\n");
			String f = String.format("         Device #%-3d : %-26s (%s)", i + 1, container, container.getClass().getSimpleName());
			messageBuffer.append(f + nl);
		}
		
		controllerLogger.info(messageBuffer);
	}
	
	after(OWService ows) : serviceStarted(ows) {
		if(ows.isStarted()) {
			serviceLogger.info("Service started");
		} else {
			serviceLogger.info("Ze wzgledu na blad krytyczny nie udalo sie uruchomic serwisu");
		}
	}

	after() : servicePaused() {
		serviceLogger.info("Service paused");
	}

	after() : serviceResumed() {
		serviceLogger.info("Service resumed");
	}

	after() : serviceStopped() {
		serviceLogger.info("Service stopped");
	}
	 
	before(OWService ows) : adapterInitiated(ows) {
		serviceLogger.info("Rozpoczeto sekwencje inicjalizacji adaptera OneWire.");
	}
	
	after(OWService ows) returning (DSPortAdapter a) : adapterInitiated(ows) {
		if(a == null) {
			serviceLogger.fatal("Nie udalo sie zainicjowac adaptera OneWire.");
		} else {
			try {
				serviceLogger.info(
						"Adapter OneWire " + a.getAdapterName() + 
						" zostal pomyslnie zainicjowany na porcie " + a.getPortName() + "."
				);
			} catch(OneWireException e) {
				serviceLogger.fatal("Nastapil problem w komunikacji na porcie.");
			}
		}
	}
	
	after(OWController owc, Device d) : deviceStarted(owc, d) {
		controllerLogger.info("Uruchomiono urzadzenie " + d.getAddress());
	}

	after(OWController owc, Device d) : deviceStopped(owc, d) {
		controllerLogger.info("Zatrzymano urzadzenie " + d.getAddress());
	}
	
	Object around() : rmiProcessExecuted() {
		Object ret = proceed();
		if(ret instanceof Boolean) {
			boolean ob = (Boolean) ret;
			if(ob) {
				serviceLogger.info("RMI Registry process executed.");
			} else {
				serviceLogger.fatal("Can't execute RMI Registry process.");
			}
		}
		return ret;
	}
}

