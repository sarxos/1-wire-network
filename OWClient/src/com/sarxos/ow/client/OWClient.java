package com.sarxos.ow.client;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.sarxos.file.JarFileFilter;
import com.sarxos.ow.device.Device;
import com.sarxos.ow.device.rmi.RemoteDevice;
import com.sarxos.ow.rmi.RemoteOWService;
import com.sarxos.ow.rmi.RemoteOWUserImpl;
import com.sarxos.ow.util.PropertyRegistry;

public class OWClient {

	private RemoteOWUserImpl user = null;
	private RemoteOWService service = null;
	private Process rmiProcess= null;
	
	private static OWClient instance = null;
	
	/**
	 * Private cobnstructor - this class is singleton type. 
	 */
	private OWClient() {
		
		Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));
		
		startRMI();
		bindOWService();
	}
	
	/**
	 * Zwraca singleton klasy klienta OW. Dzieki temu mamy zawsze jeden egzeplarz 
	 * tej klasy i unikamy konfliktow RMI.
	 * @return
	 */
	public static OWClient getClient() {
		if (instance == null) {
			instance = new OWClient();
		}
		return instance;
	}
	
	/**
	 * Uruchamia rejestr RMI.
	 */
	protected void startRMI() {
		rmiProcess = executeRMIProcess();
	}
	
	/**
	 * Binduje klient do servisu OW.
	 */
	protected void bindOWService() {
		try {
			user = new RemoteOWUserImpl();
			Naming.rebind("OWClient", user);
			
			System.out.println("Obiekt klienta zostal zbindowany do przestrzeni nazw RMI");

			Properties p = PropertyRegistry.getProperties("owc.properties");
			
			String host = p.getProperty("owc.remote.host");
			String port = p.getProperty("owc.remote.port");
			String name = p.getProperty("owc.remote.name");
			
			String serverUri = "rmi://" + host + ":" + port + "/" + name;
			
			service = (RemoteOWService) Naming.lookup(serverUri);
			
			if(service != null) {
				System.out.println("Uzyskano polaczenie z serwerem OW.");
			}
			
			try {
				service.addUser(user);
			} catch(ConnectException e) {
				System.err.println("Na serwerze nie znaleziono egzemplarza obiektu zdalnego.");
			}
			
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	protected Process executeRMIProcess() {
		Process p = null;
		try {
			
			File lib = new File("lib");
			if(!lib.exists()) {
				throw new RuntimeException("Can't find 'lib' folder.");
			}
			
			File[] files = lib.listFiles(new JarFileFilter());
			if(files == null || files.length == 0) {
				throw new RuntimeException("Folder 'lib' must contain at last OW Commons Library file.");
			}
			
			File proper_lib_file = null;
			for(File f : files) {
				if(f.getName().indexOf("ow_commons-") == 0) {
					proper_lib_file = f;
					break;
				}
			}
			
			ProcessBuilder pb = new ProcessBuilder("rmiregistry");
			Map <String, String> env = pb.environment();
			env.put("CLASSPATH", "lib/" + proper_lib_file.getName());
			
			p = pb.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}
	
	public Process getRMIProcess() {
		return rmiProcess;
	}

	public RemoteOWUserImpl getRemoteUser() {
		return user;
	}
	
	public List <RemoteDevice> getDevices() {
		try {
			if (service == null) {
				return new ArrayList<RemoteDevice>();
			}
			return service.getAllDevices();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<RemoteDevice>();
	}
}
