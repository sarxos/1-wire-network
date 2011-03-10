package com.sarxos.transproxy;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

import com.thoughtworks.xstream.XStream;

public class TransparentProxyService extends ServerSocket implements Runnable {

	private Object remoteObject = null;
	private XStream serializer = null;
	private boolean isWorking = true;
	
	/**
	 * @param remoteObject - obiekt na którym bêd¹ wykonywane przychodz¹ce zdalne metody
	 */
	public TransparentProxyService(Object remoteObject, int port) throws IOException {
		super(port);
		this.remoteObject = remoteObject;
	}
	
	public Object getRemoteObject() {
		return remoteObject;
	}
	
	public Object invoke(Method method, Object[] args) {
		System.err.println("aaaaaaaaaaaaaa");
		return null;
	}
	
	public XStream getSerializer() {
		if(serializer == null) {
			serializer = new XStream();
			serializer.processAnnotations(TransportRequest.class);
			serializer.processAnnotations(TransportResponse.class);
		}
		return serializer;
	}
	
	public void run() {
		while(isWorking) {
			try {
				Socket socket = this.accept();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
