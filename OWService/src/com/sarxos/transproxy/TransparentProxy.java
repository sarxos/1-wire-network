package com.sarxos.transproxy;

import java.io.IOException;
import java.lang.reflect.Method;

public class TransparentProxy {

	private static TransparentProxyService service = null;

	public static TransparentProxyService getConnection() {
		return service;
	}

	public static void setRemoteObject(Object remote, int port) {
		if(port < 1) {
			throw new IllegalArgumentException(
					"Port number can't be smaller then 1"
			);
		}
		if(remote != null) { 
			try {
				TransparentProxy.service = new TransparentProxyService(remote, port);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void invokeRemote(Method method, Object[] args) {
		if(service != null) {
			service.invoke(method, args);
		}
	}
}
