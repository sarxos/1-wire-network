importPackage(com.sarxos.ow.util);
importPackage(com.sarxos.ow.device);

var handle = null;

function initDevice() {
	
	// resetowanie ukladu.
	device.setTemperatureResolution(Device28.RESOLUTION_B);
	device.measTemperature();
	
	// ustawiamy alarm
	var min = 20;
	var max = 28;
	var alarm = AlarmFactory.createTemperatureAlarm(min, max);
	device.setTemperatureAlarm(alarm);
	
	// interwal odczytu - 10s
	handle = device.setInterval("measTemp()", 10000);
}

function stopDevice() {
	if (handle != null) {
		device.clearInterval(handle);
	}
}

function measTemp() {
	device.measTemperature();
}
