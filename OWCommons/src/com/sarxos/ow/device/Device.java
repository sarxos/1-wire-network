package com.sarxos.ow.device;

import java.io.Serializable;

import javax.script.ScriptEngine;

import com.dalsemi.onewire.container.OneWireContainer;
import com.sarxos.ow.Controller;
import com.sarxos.ow.device.rmi.RemoteDevice;

public interface Device extends Serializable {

	public OneWireContainer getContainer();
	public String getAddress();
	public long getAddressAsLong();
	public String getLabel();
	public void setLabel(String label);
	public boolean isEnabled();
	public void setEnabled(boolean enabled);
	public void init(OneWireContainer container, Controller controller);
	
	public void setScript(String script);
	public String getScript();
	public boolean installScriptEngine(ScriptEngine engine);
	public ScriptEngine getScriptEngine();
	public boolean resetScript();

	public void cancelAllTasks();
	
	// timing methods
	public void beginTime();
	public void endTime();
	public long getTime();
}