package com.sarxos.ow.xml;

import java.io.OutputStream;
import java.io.PrintStream;

import com.sarxos.ow.device.Device10;
import com.sarxos.ow.device.Device28;
import com.thoughtworks.xstream.XStream;

public class XmlDeviceSerializer extends XStream {

	private boolean addXmlDeclaration = true;
	
	public XmlDeviceSerializer() {
		autodetectAnnotations(true);
		processAnnotations(Device28.class);
		processAnnotations(Device10.class);
		alias("remote", java.rmi.server.RemoteObject.class);
		alias("unicast", java.rmi.server.UnicastRemoteObject.class);
	}
	
	@Override
	public void toXML(Object obj, OutputStream out) {
		if(isAddXmlDeclaration()) {
			PrintStream ps = new PrintStream(out);
			ps.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		}
		super.toXML(obj, out);
	}

	/**
	 * Czy na poczatku pliku ma byc dodawana deklaracja XML.<br>
	 * @return <code>true</code> jesli tak lub <code>false</code> jesli nie
	 */
	public boolean isAddXmlDeclaration() {
		return addXmlDeclaration;
	}

	/**
	 * Ustawia czy na poczatku pliku ma byc dodawana deklaracja XML.<br>
	 */
	public void setAddXmlDeclaration(boolean addXmlDeclaration) {
		this.addXmlDeclaration = addXmlDeclaration;
	}
}
