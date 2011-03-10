package com.sarxos.ow.device;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.dalsemi.onewire.container.OneWireContainer;
import com.sarxos.ow.Controller;
import com.sarxos.ow.ScriptTask;
import com.sarxos.ow.annotations.AllowScriptAcces;
import com.sarxos.ow.device.ScriptDevice;
import com.sarxos.ow.device.rmi.RemoteDevice;
import com.sun.script.javascript.RhinoScriptEngine;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("device")
public abstract class AbstractDevice extends UnicastRemoteObject implements Device, 
	ScriptDevice, RemoteDevice {

	private transient OneWireContainer container = null;
	private boolean enabled = false; 
	private transient String script = null;
	private transient ScriptEngine scriptEngine = null;
	private transient Timer timer = null;
	private transient long time = 0;
	private transient long tempTime = 0;
	private transient List <ScriptTask> tasks = null;
	private transient Controller controller = null;
	private String label = null;
	@XStreamAsAttribute
	private String address = null;
	private Long family = null;
	private long longAddress = 0;
	
	public AbstractDevice() throws RemoteException {
	}
	
	public AbstractDevice(OneWireContainer container) throws RemoteException {
		if(container == null) {
			throw new IllegalArgumentException(
					"OneWireContainer in " + getClass().getSimpleName() + 
					" can't be null."
			);
		}
	}
	
	public void init(OneWireContainer container, Controller controller) {
		this.container = container;
		this.controller = controller;
		this.time = 0;
		if(address == null || longAddress == 0) { 
			setAddress(container.getAddressAsString());
			longAddress = container.getAddressAsLong();
		}
		if(family == null) {
			family = Long.parseLong(address.substring(14), 16);
		}
		if(label == null) {
			setLabel(address + " " + container.getName()); 
		}
	}
	
	public OneWireContainer getContainer() {
		assert container != null;
		return container; 
	}

	/* (non-Javadoc)
	 * @see com.sarxos.onewire.devices.Device#getAddress()
	 */
	@AllowScriptAcces
	public String getAddress() {
		if(address == null && getContainer() != null) {
			setAddress(getContainer().getAddressAsString());
		}
		return address;
	}

	protected void setAddress(String address) {
		this.address = address;
	}

	public long getAddressAsLong() {
		return longAddress;
	}
	
	/* (non-Javadoc)
	 * @see com.sarxos.onewire.devices.Device#getLabel()
	 */
	@AllowScriptAcces
	public String getLabel() {
		if(label == null) {
			setLabel(getAddress() + " " + getContainer().getName()); 
		}
		return label;
	}

	/* (non-Javadoc)
	 * @see com.sarxos.onewire.devices.Device#setLabel(java.lang.String)
	 */
	@AllowScriptAcces
	public void setLabel(String label) {
		this.label = label;
	}

	/* (non-Javadoc)
	 * @see com.sarxos.onewire.devices.Device#isEnabled()
	 */
	@AllowScriptAcces
	public boolean isEnabled() {
		return enabled;
	}

	/* (non-Javadoc)
	 * @see com.sarxos.onewire.devices.Device#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
		// TODO: zaimplementowac trigger do zapisu skryptu
	}
	
	protected Controller getController() {
		return controller;
	}

	public boolean resetScript() {
		clearScriptEngineBindings();
		cancelAllTasks();
		getScriptEngine().getBindings(ScriptContext.GLOBAL_SCOPE).clear();
		getScriptEngine().getBindings(ScriptContext.ENGINE_SCOPE).clear();
		getScriptEngine().createBindings();
		setScriptEngineBindings();
		return false;
	}
	
	public synchronized boolean installScriptEngine(ScriptEngine engine) {
		if(engine == null) {
			throw new IllegalArgumentException("Script engine in device can't be null.");
		}
		if(scriptEngine != null) {
			clearScriptEngineBindings();
		}
		scriptEngine = engine;
		setScriptEngineBindings();
		if(getScript() != null) {
			try {
				engine.eval(getScript());
				return true;
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	protected void clearScriptEngineBindings() {
		scriptEngine.put("controller", null);
		scriptEngine.put("device", null);
		scriptEngine.put("out", null);
		scriptEngine.put("err", null);
	}
	
	protected void setScriptEngineBindings() {
		assert controller != null;
		scriptEngine.put("controller", controller);
		scriptEngine.put("device", this);
		scriptEngine.put("out", System.out);
		scriptEngine.put("err", System.err);
	}
	
	public ScriptEngine getScriptEngine() {
		return scriptEngine;
	}
	
	private List <ScriptTask> getTasksList() {
		if(tasks == null) {
			tasks = new ArrayList<ScriptTask>();
		}
		return tasks;
	}
	
	/*
	 * SCRIPTABLE ACCES
	 */
	
	protected Timer getTimer() {
		if(timer == null) {
			timer = new Timer(getLabel() + " timer");
		}
		return timer;
	}
	
	protected ScriptTask createScriptTaskForFunction(String functionName) {
		ScriptTask tt = new ScriptTask(this, functionName, getScriptEngine());
		getTasksList().add(tt);
		return tt;
	}
	
	/* (non-Javadoc)
	 * @see com.sarxos.ow.devices.ScriptMethods#setInterval(java.lang.String, long)
	 */
	@AllowScriptAcces
	public TimerTask setInterval(String functionName, long period) {
		ScriptTask tt = createScriptTaskForFunction(functionName);
		getTimer().scheduleAtFixedRate(tt, period, period);
		return tt;
	}


	/* (non-Javadoc)
	 * @see com.sarxos.ow.devices.ScriptMethods#setInterval(java.lang.String, long, long)
	 */
	@AllowScriptAcces
	public TimerTask setInterval(String functionName, long period, long delay) {
		ScriptTask tt = createScriptTaskForFunction(functionName);
		getTimer().scheduleAtFixedRate(tt, delay, period);
		return tt;
	}
	
	/* (non-Javadoc)
	 * @see com.sarxos.ow.devices.ScriptMethods#setTimeout(java.lang.String, long)
	 */
	@AllowScriptAcces
	public TimerTask setTimeout(String functionName, long period) {
		ScriptTask tt = createScriptTaskForFunction(functionName);
		getTimer().schedule(tt, period, period);
		return tt;
	}

	/* (non-Javadoc)
	 * @see com.sarxos.ow.devices.ScriptMethods#setTimeout(java.lang.String, long, long)
	 */
	@AllowScriptAcces
	public TimerTask setTimeout(String functionName, long period, long delay) {
		ScriptTask tt = createScriptTaskForFunction(functionName);
		getTimer().schedule(tt, period, period);
		return tt;
	}

	/* (non-Javadoc)
	 * @see com.sarxos.ow.devices.ScriptMethods#clearInterval(java.util.TimerTask)
	 */
	@AllowScriptAcces
	public void clearInterval(TimerTask task) {
		try {
			if(task != null) {
				task.cancel();
				getTasksList().remove(task);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cancelAllTasks() {
		for(ScriptTask st : getTasksList()) {
			st.cancel();
		}
		getTasksList().clear();
	}

	public long getTime() {
		return time;
	}
	
	public void beginTime() {
		tempTime = System.currentTimeMillis();
	}
	
	public void endTime() {
		time = System.currentTimeMillis() - tempTime;
	}
}
