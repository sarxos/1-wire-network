importPackage(com.sarxos.ow.util);
importPackage(com.sarxos.ow.device);

var handle = null;

function initDevice() {
	
	// resetowanie ukladu.
	device.setTemperatureResolution(Device10.RESOLUTION_A);
	device.measTemperature();
	
	var min = 20;
	var max = 28;
	var alarm = AlarmFactory.createTemperatureAlarm(min, max);
	device.setTemperatureAlarm(alarm);
	
	handle = device.setInterval("wypiszTemp()", 2000);
}

function stopDevice() {
	if (handle != null) {
		device.clearInterval(handle);
	}
}

function wypiszTemp() {
	var temp = device.measTemperature();
}

function alarmBegin() {
	try {
		var t = device.measTemperature();
		out.println("UWAGA ALARM: " + t);
	} catch(e) {
		out.println(e);
	}
}

function alarmFinish() {
	try {
		var t = device.measTemperature();
		out.println("KOENIC ALARM: " + t);
	} catch(e) {
		out.println(e);
	}
}

