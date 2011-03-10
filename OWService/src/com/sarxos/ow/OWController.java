package com.sarxos.ow;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import sun.org.mozilla.javascript.internal.WrappedException;

import com.dalsemi.onewire.OneWireException;
import com.dalsemi.onewire.adapter.DSPortAdapter;
import com.dalsemi.onewire.adapter.OneWireIOException;
import com.dalsemi.onewire.application.monitor.DeviceMonitorEvent;
import com.dalsemi.onewire.application.monitor.DeviceMonitorException;
import com.sarxos.ow.device.Device;
import com.sarxos.ow.device.DeviceException;
import com.sun.script.javascript.RhinoScriptEngine;


public class OWController implements Controller, Runnable, UncaughtExceptionHandler {

	private transient OWService service = null;
	private OWDeviceMonitor monitor = null;
	private Cache cache = null;


	protected ScriptEngineManager engineManager = null;

	public OWController(OWService service) throws OWException {

		if(service == null) {
			throw new IllegalArgumentException(
					OWService.class.getSimpleName() + " object in " + 
					OWController.class.getSimpleName() + " can't be null!"
			);
		}

		this.service = service;

		monitor = getMonitor();
		cache = getCache();

		Thread t = new Thread(this);
		t.start();

		if(cache == null) {
			throw new OWException(
					"Nie udalo sie zaladowac zestawu urzadzen z magazynu."
			);
		}
	}

	/* (non-Javadoc)
	 * @see com.sarxos.onewire.Controller#getMonitor()
	 */
	public OWDeviceMonitor getMonitor() {
		if(monitor == null) {
			monitor = createMonitor();
		}
		return monitor;
	}

	/* (non-Javadoc)
	 * @see com.sarxos.onewire.Controller#getCache()
	 */
	public Cache getCache() {
		if(cache == null) {
			try {
				cache = new OWCache(this);
			} catch (Exception e) {
				getService().exceptionOccured(e);
				return null;
			}
		}
		return cache;
	}

	protected OWDeviceMonitor createMonitor() {
		DSPortAdapter adapter = getService().getAdapter();
		monitor = new OWDeviceMonitor(adapter);
		monitor.addDeviceMonitorEventListener(this);
		monitor.setMaxErrorCount(5);
		return monitor;
	}

	/* (non-Javadoc)
	 * @see com.sarxos.onewire.Controller#scan()
	 */
	public void scan() {
		Vector a = new Vector();
		Vector d = new Vector();
		try {
			getMonitor().search(a, d);
		} catch(OneWireException e) {
			getService().exceptionOccured(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.sarxos.onewire.Controller#getService()
	 */
	public Service getService() {
		return service;
	}

	public void startDevice(final Device d) {
		try {
			d.setEnabled(true);
			d.installScriptEngine(createEngine(d));
			RhinoScriptEngine rse = (RhinoScriptEngine) d.getScriptEngine();
			if(rse != null) {
				if(d.getScript() != null) {
					try {
						rse.invokeFunction("initDevice", new Object[] {});
					} catch (NoSuchMethodException e) {
					} catch (Exception e) {
						Throwable cause = null;
						if (e instanceof ScriptException) {
							ScriptException se = (ScriptException) e;
							Throwable thr = se.getCause();
							if (thr instanceof WrappedException) {
								WrappedException we = (WrappedException) thr;
								System.out.println(we.details());
								cause = we.getWrappedException();
							}
						}
						getService().exceptionOccured(e);
						if (cause != null) {
							cause.printStackTrace();
						}
					}
				}
			}
		} catch(Exception ee) {
			ee.printStackTrace();
		}
	}

	public void stopDevice(final Device d) {
		try {
			d.setEnabled(false);
			RhinoScriptEngine rse = (RhinoScriptEngine) d.getScriptEngine();
			if(rse != null) {
				if(d.getScript() != null) {
					try {
						rse.invokeFunction("stopDevice", new Object[] {});
					} catch (NoSuchMethodException e) {
					} catch (Exception e) {
						getService().exceptionOccured(e);
					} finally {
						d.cancelAllTasks();
					}
				}
			}
		} catch(Exception ee) {
			ee.printStackTrace();
		}
	}

	public void deviceArrival(DeviceMonitorEvent e) {
		try {
			int n = e.getDeviceCount();
			List <Device> devices = new ArrayList<Device>((int) (n * 1.25));
			for(int i = 0; i < n; i++) {
				Device d = cache.getDevice(e.getContainerAt(i));
				devices.add(d);
				createEngine(d);
				startDevice(d);
			}
		} catch(Exception ee) {
			ee.printStackTrace();
		}
	}

	public void deviceDeparture(DeviceMonitorEvent e) {
		try {
			int n = e.getDeviceCount();
			List <Device> devices = new ArrayList<Device>((int) (n * 1.25));
			for(int i = 0; i < n; i++) {
				Device d = cache.getDevice(e.getContainerAt(i));
				devices.add(d);
				stopDevice(d);
			}
		} catch(Exception ee) {
			ee.printStackTrace();
		}
	}

	public synchronized List <Device> getDevicesList() {
		Map <String, Device> devices_map = getCache().getDevices();
		Collection <Device> collection = devices_map.values();
		Device[] array = collection.toArray(new Device[0]);
		return Arrays.asList(array);
	}

	public void deviceException(DeviceException e) {
		getService().exceptionOccured(e);
	}

	public void networkException(DeviceMonitorException e) {
		getService().exceptionOccured(e);
	}

	protected RhinoScriptEngine createEngine(Device d) {
		ScriptEngineManager engineManager = getEngineManager();
		RhinoScriptEngine engine = (RhinoScriptEngine) engineManager.getEngineByName("JavaScript");
		d.installScriptEngine(engine);
		return engine;
	}

	public ScriptEngineManager getEngineManager() {
		if(engineManager == null) {
			engineManager = new ScriptEngineManager();
		}
		return engineManager;
	}

	public void run() {
		DSPortAdapter adapter = getService().getAdapter();
		Hashtable <Long, Integer> deviceAddressHash = new Hashtable<Long, Integer>();
		Vector <Long> alarm_arrivals = new Vector<Long>();
		Vector <Long> alarm_departures = new Vector<Long>(); 
		while (true) {

			alarm_arrivals.clear();
			alarm_departures.clear();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			List<Device> devices = new ArrayList<Device>();
			for (Device d : getDevicesList()) {
				if (d.isEnabled()) {
					devices.add(d);
				}
			}
			
			synchronized (adapter) {
				try {
					adapter.beginExclusive(false);
					for (Device d : devices) {
						boolean alarming = adapter.isAlarming(d.getAddressAsLong()); 
						if (alarming) {
							Long longAddress = new Long(d.getAddressAsLong());
							if(!deviceAddressHash.containsKey(longAddress) && alarm_arrivals != null) {
								alarm_arrivals.addElement(longAddress);
							}
							deviceAddressHash.put(longAddress, new Integer(3));
						}
					}
				} catch (OneWireIOException e) {
					e.printStackTrace();
				} catch (OneWireException e) {
					e.printStackTrace();
				} finally {
					adapter.endExclusive();
				}
			}
			
			Enumeration e_devices = deviceAddressHash.keys();
			while(e_devices.hasMoreElements()) {
				Long longAddress = (Long)e_devices.nextElement();

				int seen = deviceAddressHash.get(longAddress);
				if(seen <= 0) {
					deviceAddressHash.remove(longAddress);
					if(alarm_departures != null) {
						alarm_departures.addElement(longAddress);
					}
				} else {
					deviceAddressHash.put(longAddress, new Integer(seen - 1));
				}
			}

			if(alarm_arrivals != null && alarm_arrivals.size() > 0) {
				DeviceAlarmEvent dme = new DeviceAlarmEvent(
						DeviceAlarmEvent.ARRIVAL, 
						monitor,
			            adapter, 
			            (Vector) alarm_arrivals.clone()
			    );
				alarmBegin(dme);
			}

			if(alarm_departures != null && alarm_departures.size() > 0) {
				DeviceAlarmEvent dme = new DeviceAlarmEvent(
						DeviceAlarmEvent.ARRIVAL, 
						monitor,
			            adapter, 
			            (Vector) alarm_departures.clone()
			    );
				alarmFinish(dme);
			}
		}
	}

	public void alarmBegin(DeviceAlarmEvent de) {
		try {
			int n = de.getDeviceCount();
			for(int i = 0; i < n; i++) {
				Device d = cache.getDevice(de.getContainerAt(i));
				RhinoScriptEngine rse = (RhinoScriptEngine) d.getScriptEngine();
				if(rse != null) {
					if(d.getScript() != null) {
						try {
							rse.invokeFunction("alarmBegin", new Object[] {});
						} catch (NoSuchMethodException e) {
						} catch (Exception e) {
							getService().exceptionOccured(e);
						}
					}
				}
			}
		} catch(Exception ee) {
			ee.printStackTrace();
		}
	}

	public void alarmFinish(DeviceAlarmEvent de) {
		try {
			int n = de.getDeviceCount();
			for(int i = 0; i < n; i++) {
				Device d = cache.getDevice(de.getContainerAt(i));
				RhinoScriptEngine rse = (RhinoScriptEngine) d.getScriptEngine();
				if(rse != null) {
					if(d.getScript() != null) {
						try {
							rse.invokeFunction("alarmFinish", new Object[] {});
						} catch (NoSuchMethodException e) {
						} catch (Exception e) {
							getService().exceptionOccured(e);
						}
					}
				}
			}
		} catch(Exception ee) {
			ee.printStackTrace();
		}
	}

	public void uncaughtException(Thread t, Throwable e) {
		e.printStackTrace();
	}
}
