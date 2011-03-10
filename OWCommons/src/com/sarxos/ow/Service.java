package com.sarxos.ow;

import com.dalsemi.onewire.adapter.DSPortAdapter;

public interface Service {

	public void startService();
	public void pauseService();
	public void resumeService();
	public void stopService();
	
	public DSPortAdapter getAdapter();
	public Controller getController();
	
	public void exceptionOccured(Throwable t);
}
