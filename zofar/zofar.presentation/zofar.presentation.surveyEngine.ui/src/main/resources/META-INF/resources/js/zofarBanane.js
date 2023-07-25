function addZofarEventListener(obj, eventType, method) {
	// obj.addEventListener(eventType,method,false);
	// obj.attachEvent("on"+eventType,method);
	if (obj.addEventListener) {
		obj.addEventListener(eventType, method, false);
	} else if (obj.attachEvent) {
		obj.attachEvent('on' + eventType, function() {
			return method.call(obj, window.event);
		});
	}
}
