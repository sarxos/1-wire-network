package com.sarxos.ow;

import java.util.List;

import com.dalsemi.onewire.application.monitor.DeviceMonitor;
import com.dalsemi.onewire.application.monitor.DeviceMonitorEventListener;
import com.sarxos.ow.device.Device;

public interface Controller extends DeviceMonitorEventListener {

	/**
	 * Zwraca monitor magistrali OneWire.<br>
	 * @return <code>{@link DeviceMonitor}</code>
	 */
	public DeviceMonitor getMonitor();

	public Cache getCache();

	/**
	 * Wykonuje jednorazowe skanowanie magistrali OneWire w celu
	 * okreœlenia jakie urz¹dzenia przyby³a, a jakie zosta³y usuniête. 
	 * Po wykryciu zmian urzadzen wykonywane sa odpowiednie metody 
	 * interfejsu <code>{@link DeviceMonitorEventListener}</code>.<br>
	 * @see <code>{@link DeviceMonitorEventListener#deviceArrival(DeviceMonitorEvent)}</code>
	 * @see <code>{@link DeviceMonitorEventListener#deviceArrival(DeviceMonitorEvent)}</code>
	 */
	public void scan();

	/**
	 * Zwraca obiekt serwisu dla ktorego dany kontroler zostal 
	 * utworzony.<br>
	 * @return Zwraca obiekt <code>{@link Service}</code>
	 */
	public Service getService();

	public void startDevice(Device d);
	public void stopDevice(Device d);
	
}