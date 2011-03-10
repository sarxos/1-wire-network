package com.sarxos.ow.device;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.dalsemi.onewire.OneWireException;
import com.dalsemi.onewire.adapter.DSPortAdapter;
import com.dalsemi.onewire.container.OneWireContainer10;
import com.dalsemi.onewire.container.OneWireContainer28;
import com.dalsemi.onewire.container.TemperatureContainer;
import com.sarxos.ow.device.AbstractDevice;
import com.sarxos.ow.device.event.TemperatureChangeEvent;
import com.sarxos.ow.device.rmi.RemoteTemperatureDevice;
import com.sarxos.ow.device.rmi.RemoteTemperatureListener;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("device10")
public class Device10 extends AbstractDevice implements TemperatureDevice, 
RemoteTemperatureDevice {

	private static final long serialVersionUID = 1461333880592467691L;
	
	public static final double RESOLUTION_A = 0.5;		// 750 ms delay
	public static final double RESOLUTION_B = 0.1;		// 750 ms delay
	
	private transient List <RemoteTemperatureListener> tempListeners = new ArrayList<RemoteTemperatureListener>();
	private transient double temp = Integer.MIN_VALUE;
	
	public Device10(OneWireContainer10 owc) throws RemoteException {
		super(owc);
		this.tempListeners = new ArrayList<RemoteTemperatureListener>();
		this.temp = Integer.MIN_VALUE;
	}

	/* NOTE!
	 * Z interfejsu RemoteTemperatureDevice
	 */
	
	public boolean addTemperatureListener(RemoteTemperatureListener l) {
		checkListeners();
		return tempListeners.add(l);
	}

	public RemoteTemperatureListener[] getTemperatureListeners() {
		checkListeners();
		return tempListeners.toArray(new RemoteTemperatureListener[] {});
	}

	public boolean removeTemperatureListener(RemoteTemperatureListener l) {
		checkListeners();
		return tempListeners.remove(l);
	}

	protected void checkListeners() {
		if (tempListeners == null) {
			tempListeners = new ArrayList<RemoteTemperatureListener>();
		}
	}

	/* NOTE!
	 * Z interfejsu TemperatureDevice
	 */
	
	public double measTemperature() {
		try {
			OneWireContainer10 c = (OneWireContainer10) getContainer();
			DSPortAdapter adapter = c.getAdapter();
			double newTemp = 0;

			synchronized(adapter) {
				try {
					adapter.beginExclusive(false);
					c.doTemperatureConvert(c.readDevice());
					newTemp = c.getTemperature(c.readDevice());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					adapter.endExclusive();
				}
			}

			if(newTemp != temp) {
				checkListeners();
				final TemperatureChangeEvent e = new TemperatureChangeEvent(this, temp, newTemp);
				for(int i = 0; i < tempListeners.size(); i++) {
					try {
						// TODO: zaimplementowac egzekutor
						final RemoteTemperatureListener rtl = tempListeners.get(i);
						Runnable r = new Runnable() {
							public void run() {
								try {
									rtl.temparatureChange(e);
								} catch (RemoteException e) {
									e.printStackTrace();
								}
							}
						};
						Thread t = new Thread(r);
						t.start();
					} catch(Exception ee) {
						ee.printStackTrace();
					}
				}
				temp = newTemp;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return temp;
	}


	public double getTemperature() {
		return temp;
	}
	
	public double getMaxTemperature() {
		TemperatureContainer c = (TemperatureContainer) getContainer();
		return c.getMaxTemperature();
	}

	public double getMinTemperature() {
		TemperatureContainer c = (TemperatureContainer) getContainer();
		return c.getMinTemperature();
	}

	public double getTemperatureResolution() {
		OneWireContainer10 c = (OneWireContainer10) getContainer();
		DSPortAdapter adapter = c.getAdapter();
		double r = Integer.MIN_VALUE;
		synchronized(adapter) {
			try {
				adapter.beginExclusive(false);
				r = c.getTemperatureResolution(c.readDevice());
			} catch (OneWireException e) {
				e.printStackTrace();
			} finally {
				adapter.endExclusive();
			}
		}
		return r;
	}

	public double[] getTemperatureResolutions() {
		TemperatureContainer c = (TemperatureContainer) getContainer();
		return c.getTemperatureResolutions();
	}

	public boolean hasTemperatureAlarms() {
		TemperatureContainer c = (TemperatureContainer) getContainer();
		return c.hasTemperatureAlarms();
	}

	public boolean hasTemperatureResolutions() {
		TemperatureContainer c = (TemperatureContainer) getContainer();
		return c.hasSelectableTemperatureResolution();
	}

	public void setTemperatureResolution(double resolution) {
		if(!hasTemperatureResolutions()) {
			return;
		}
		OneWireContainer10 c = (OneWireContainer10) getContainer();
		DSPortAdapter adapter = c.getAdapter();
		synchronized(adapter) {
			try {
				adapter.beginExclusive(false);
				byte[] state = c.readDevice();
				c.setTemperatureResolution(resolution, state);
				c.writeDevice(state);
			} catch (OneWireException e) {
				e.printStackTrace();
			} finally {
				adapter.endExclusive();
			}
		}
	}

	public TemperatureAlarm getTemperatureAlarm() {
		if(!hasTemperatureAlarms()) {
			return null;
		}
		OneWireContainer10 c = (OneWireContainer10) getContainer();
		DSPortAdapter adapter = c.getAdapter();
		double[] r = new double[] {Integer.MIN_VALUE, Integer.MIN_VALUE};
		synchronized(adapter) {
			try {
				adapter.beginExclusive(false);
				byte[] state = c.readDevice();
				r[0] = c.getTemperatureAlarm(OneWireContainer28.ALARM_LOW, state);
				r[1] = c.getTemperatureAlarm(OneWireContainer28.ALARM_HIGH, state);
				adapter.endExclusive();
			} catch (OneWireException e) {
				e.printStackTrace();
			} finally {
				adapter.endExclusive();
			}
		}
		TemperatureAlarm a = new TemperatureAlarm(r[0], r[1]);
		return a;
	}

	public void setTemperatureAlarm(TemperatureAlarm a) {
		if(!hasTemperatureAlarms()) {
			return;
		}
		double min = a.getMinTemperature();
		double max = a.getMaxTemperature();
		boolean set_min = min != Integer.MIN_VALUE;
		boolean set_max = max != Integer.MIN_VALUE;
		OneWireContainer10 c = (OneWireContainer10) getContainer();
		DSPortAdapter adapter = c.getAdapter();
		synchronized(adapter) {
			try {
				adapter.beginExclusive(false);
				byte[] state = c.readDevice();
				if(set_min) c.setTemperatureAlarm(OneWireContainer10.ALARM_LOW, min, state);
				if(set_max) c.setTemperatureAlarm(OneWireContainer10.ALARM_HIGH, max, state);
				c.writeDevice(state);
				adapter.endExclusive();
			} catch (OneWireException e) {
				e.printStackTrace();
			} finally {
				adapter.endExclusive();
			}
		}
	}
}
