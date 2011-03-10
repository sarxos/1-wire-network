package com.sarxos.ow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import com.dalsemi.onewire.OneWireAccessProvider;
import com.dalsemi.onewire.OneWireException;
import com.dalsemi.onewire.adapter.DSPortAdapter;
import com.dalsemi.onewire.application.monitor.DeviceMonitor;
import com.sarxos.file.JarFileFilter;
import com.sarxos.ow.rmi.RemoteOWService;
import com.sarxos.ow.rmi.RemoteOWServiceImpl;
import com.sarxos.ow.util.PropertyRegistry;

public class OWService implements Runnable, Service, UncaughtExceptionHandler {

	private OWServiceState state = OWServiceState.DISABLED;
	
	private DSPortAdapter adapter = null;
	private Controller controller = null;
	private RemoteOWService remote = null;
	
	private boolean started = false; 
	private boolean running = false;
	
	private int serviceSleepInterval = 1000;
	
	private Thread serviceRunner = null;
	private Thread monitorRunner = null;
	
	private Process rmiProcess = null;
	
	protected Properties properties = null;
	
	public OWService() throws IOException {

		// wczytujemy propertisy
		properties = PropertyRegistry.getProperties("owd.properties");
		if(properties == null) {
			setState(OWServiceState.FATAL);
			return;
		}

		// hook dla zatrzymania wirtualnej maszyny 
		Runtime.getRuntime().addShutdownHook(new ServiceShutodownHook(this));
		
		// uruchamianie rejestru RMI
		boolean isRMIExecuted = executeRMIRegistryProcess();
		if(!isRMIExecuted) {
			setState(OWServiceState.FATAL);
			return;
		}
		
		// pobieranie adaptera
		adapter = getAdapter();
		if(adapter == null) {
			setState(OWServiceState.FATAL);
			return;
		}
		
		// uruchamianie kontrolera
		controller = getController();
		if(controller == null) {
			setState(OWServiceState.FATAL);
			return;
		}
		
		// uruchamianie obiektu zdalnego
		remote = getRemote();
	}
	
	protected void setState(OWServiceState state) {
		this.state = state; 
	}
	
	public OWServiceState getState() {
		return state;
	}
	
	protected boolean executeRMIRegistryProcess() {
		try {
			
			int port = 1099;
			String portObj = properties.getProperty("owd.rmi.port");
			if(portObj != null) {
				try {
					port = Integer.parseInt(portObj);
				} catch(NumberFormatException e) {
				}
			}
			
			File lib = new File("lib");
			if(!lib.exists()) {
				throw new RuntimeException("Can't find 'lib' folder.");
			}
			
			File[] files = lib.listFiles(new JarFileFilter());
			if(files == null || files.length == 0) {
				throw new RuntimeException("Folder 'lib' must contain at last OW Commons Library file.");
			}
			
			File proper_lib = null;
			for(File f : files) {
				if(f.getName().indexOf("ow_commons-") == 0) {
					proper_lib = f;
					break;
				}
			}
			
			ProcessBuilder pb = new ProcessBuilder("rmiregistry", "" + port);
			Map <String, String> env = pb.environment();
			env.put("CLASSPATH", "lib/" + proper_lib.getName());
			
			rmiProcess = pb.start();
			
			return true;
		} catch (Exception e) {
			setState(OWServiceState.FATAL);
			exceptionOccured(e);
		}
		return false;
	}
	
	public Enumeration getAllDevicesAddress() {
		return getController().getMonitor().getAllAddresses();
	}
	
	protected RemoteOWService getRemote() {
		if(remote == null) {
			try {
				remote = new RemoteOWServiceImpl(this);
				String name = properties.getProperty("owd.rmi.name").trim();
				Naming.rebind(name, remote);
			} catch (Exception e) {
				exceptionOccured(e);
				return null;
			}
		}
		return remote;
	}
	
	/**
	 * Metoda zwraca <code>{@link DSPortAdapter}</code> z ktorego korzysta serwis 
	 * lub <code>null</code> jesli nie udalo sie go zainicjowac.<br>
	 * @return <code>{@link DSPortAdapter}</code> (lub <code>null</code> jesli nie 
	 * 			udalo sie zainicjowac adaptera)
	 */
	public DSPortAdapter getAdapter() {
		if(adapter == null) {
			adapter = findAdapter();
		}
		return adapter;
	}

	/**
	 * Metoda wyszukuje <code>{@link DSPortAdapter}</code> skanujac porty USB w 
	 * zakresie 0 - 30. Jesi znajdzie
	 * adapter na ktoryms z tych portow to przeryuwa szukanie i go zwraca. Jesli adapter nie
	 * zostanie znaleziony zwracany jest <code>null</code>.<br>
	 * @return <code>{@link DSPortAdapter}</code> (lub <code>null</code> jesli nic nie znajdzie)
	 */
	protected DSPortAdapter findAdapter() {
		
		DSPortAdapter adapter = null;
		if(getState() == OWServiceState.FATAL) {
			return null;
		}
		
		for(int i = 0; i < 30; i++) {
			// TODO: zaimplementowac zmienna ilosc przeszukiwanych portow USB (z konfiguracji).
			try {
				adapter = OneWireAccessProvider.getAdapter("{DS9490}", "USB" + i);
				if(adapter != null) {
					break;
				}
			} catch(OneWireException e) { }
		}
		return adapter;
	}
	
	public void run() {
		boolean fatalError = (getState() == OWServiceState.FATAL);
		while(started && !fatalError) {
			try {
				Thread.sleep(serviceSleepInterval);
			} catch (InterruptedException e) {
				exceptionOccured(e);
			}
		}
	}
	
	/**
	 * Metoda zwraca kontroler sieci OneWire.<br>
	 * @return <code>{@link OWController}</code> dla tego serwisu
	 */
	public Controller getController() {
		if(getState() == OWServiceState.FATAL) {
			return null;
		}
		if(controller == null) {
			try {
				controller = new OWController(this);
			} catch (OWException e) {
				exceptionOccured(e);
				return null;
			}
		}
		return controller;
	}

	/**
	 * Starting SarXos 1-Wire Service. 
	 * @see com.sarxos.ow.Service#startService()
	 */
	public void startService() {
		
		if(getState() == OWServiceState.FATAL) {
			return;
		}
		
		setState(OWServiceState.INITIALIZATION);
		
		started = true;
		running = true;

		// uruchamianie serwisu
		serviceRunner = new Thread(
				this, 
				OWService.class.getSimpleName() + " Runner"
		);
		serviceRunner.setUncaughtExceptionHandler(this);
		serviceRunner.start();
		
		// uruchamianie monitora
		DeviceMonitor monitor = getController().getMonitor(); 
		monitorRunner = new Thread(
				monitor,
				monitor.getClass().getSimpleName() + " Thread"
		);
		monitorRunner.setUncaughtExceptionHandler(this);
		monitorRunner.start();
		
		setState(OWServiceState.RUNNING);
	}

	/**
	 * Zatrzymuje dzialanie serwisu 
	 * @see com.sarxos.ow.Service#pauseService()
	 */
	public synchronized void pauseService() {
		this.running = false;
		getController().getMonitor().pauseMonitor(false);
		setState(OWServiceState.PAUSED);
	}


	/** 
	 * Wznawia dzialanie serwisu.<br>
	 * @see com.sarxos.ow.Service#resumeService()
	 */
	public void resumeService() {
		if(!running) {
			running = true;
			getController().getMonitor().resumeMonitor(false);
		}
		setState(OWServiceState.RUNNING);
	}
	
	public synchronized void stopService() {
		
		started = false;
		try {
			
			Controller controller = getController();
			if (controller == null) {
				setState(OWServiceState.FATAL);
				return;
			}
			
			controller.getMonitor().killMonitor();
			monitorRunner.join();
			serviceRunner.join();
			
		} catch (InterruptedException e) {
			exceptionOccured(e);
		}
		setState(OWServiceState.DISABLED);
	}

	public void logMessage(String message) {
		System.out.println(getClass().getSimpleName() + ": " + message);
	}

	public int getServiceSleepInterval() {
		return serviceSleepInterval;
	}

	public void setServiceSleepInterval(int serviceSleepInterval) {
		if(serviceSleepInterval < 100) {
			throw new IllegalArgumentException(
					"Service sleep interval can't be less then 100ms."
			);
		}
		this.serviceSleepInterval = serviceSleepInterval;
	}
	
	public boolean isStarted() {
		return started;
	}

	public boolean isRunning() {
		return running;
	}

	public void uncaughtException(Thread t, Throwable e) {
		exceptionOccured(e);
	}
	
	public void exceptionOccured(Throwable e) {
		e.printStackTrace();
	}
	
	public Process getRMIProcess() {
		return rmiProcess;
	}
}
