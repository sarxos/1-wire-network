package com.sarxos.transproxy;

import java.net.Socket;

public class TransparentProxyClient extends Thread {

	private Socket socket = null;
	private TransparentProxyService service = null;
	
	public TransparentProxyClient(Socket socket, TransparentProxyService service) {
		
	}
	
}
