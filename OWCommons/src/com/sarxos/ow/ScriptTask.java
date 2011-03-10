package com.sarxos.ow;

import java.util.TimerTask;

import javax.script.ScriptEngine;

import com.sarxos.ow.device.Device;
import com.sun.script.javascript.RhinoScriptEngine;

public class ScriptTask extends TimerTask {

	private String functionName = null;
	private ScriptEngine scriptEngine = null;
	private long time = 0;
	private Device device = null;
	
	public ScriptTask(Device d, String functionName, ScriptEngine scriptEngine) {
		if(functionName == null) {
			throw new IllegalArgumentException(
					"Function name in " + getClass().getSimpleName() + 
					"can't be null."
			);
		}
		if(scriptEngine == null) {
			throw new IllegalArgumentException(
					"Script engine in " + getClass().getSimpleName() + 
					"can't be null."
			);
		}
		if(d == null) {
			throw new IllegalArgumentException(
					"Device object in " + getClass().getSimpleName() + 
					"can't be null."
			);
		}
		this.functionName = functionName.replaceAll("[();]", "").trim();
		this.scriptEngine = scriptEngine;
		this.device = d;
	}
	
	@Override
	public void run() {
		final Object[] args = new Object[0];	
		ScriptEngine se = scriptEngine;
		if(se instanceof RhinoScriptEngine) {
			RhinoScriptEngine rse = (RhinoScriptEngine) se;
			try {
				device.beginTime();
				rse.invokeFunction(functionName, args);
				device.endTime();
				time = device.getTime();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public long getTime() {
		return time;
	}

}
