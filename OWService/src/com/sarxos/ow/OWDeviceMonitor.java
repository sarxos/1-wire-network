package com.sarxos.ow;

import java.util.Enumeration;
import java.util.Vector;

import com.dalsemi.onewire.OneWireException;
import com.dalsemi.onewire.adapter.DSPortAdapter;
import com.dalsemi.onewire.adapter.OneWireIOException;
import com.dalsemi.onewire.application.monitor.DeviceMonitor;

public class OWDeviceMonitor extends DeviceMonitor {

	public OWDeviceMonitor(DSPortAdapter adapter) {
		super(adapter);
	}

	@Override
	public void run () {

		synchronized (sync_flag) {
			hasCompletelyStopped = false;
		}

		Vector arrivals = new Vector();
		Vector departures = new Vector();

		while (keepRunning) {
			if (startRunning) {
				synchronized (sync_flag) {
					isRunning = true;
				}

				arrivals.setSize(0);
				departures.setSize(0);

				// ilosc wystapien bledow w czasie 1-Wire search
				int errorCount = 0;
				boolean done = false;
				while(!done) {
					try	{
						// szukamy nowych urzadzen, usuwamy nieuzywane urzadzenia
						search(arrivals, departures);
						done = true;
					} catch(Exception e) {
						if(++errorCount == max_error_count) {
							fireException(adapter, e);
							done = true;
						}
					}
				}

				// sleep aby inne watki tez mialy mozliwosc dostepu do adaptera
				msSleep(200);
			} else {
				// zatrzymany wiec czyscimy flage
				synchronized(sync_flag) {
					isRunning = false;
				}
				msSleep(200);
			}
		}

		// not running so clear flag
		synchronized (sync_flag)	{
			isRunning = false;
			hasCompletelyStopped = true;
		}
	}

	public void fireException(DSPortAdapter adapter, Exception e) {
		throw new RuntimeException("Wystapil blad w czasie dostepu do adaptera.", e);
	}
}
