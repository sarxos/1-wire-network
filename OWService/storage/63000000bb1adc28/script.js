importPackage(com.sarxos.ow.util);
importPackage(com.sarxos.ow.device);

var handle = null;

// podlaczenie ukladu
function initDevice() {
	
	// resetowanie ukladu.
	device.setTemperatureResolution(Device28.RESOLUTION_A);
	device.measTemperature();
	// koniec
	
	var min = 20;
	var max = 30;
	var alarm = AlarmFactory.createTemperatureAlarm(min, max);
	device.setTemperatureAlarm(alarm);
	
	handle = device.setInterval("measTemp()", 5000);
}

// pomiar
function measTemp() {
	device.measTemperature();
}

// odlaczenie ukladu
function stopDevice() {
	if (handle != null) {
		device.clearInterval(handle);
	}
}