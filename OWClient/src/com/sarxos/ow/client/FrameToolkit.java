package com.sarxos.ow.client;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;

/**
 * Kilka u¿ytecznych metod.<br>s
 * @author Bartosz Firyn (SarXos)
 * @version 0.1 2007-01-25
 */
public class FrameToolkit {

	/**
	 * Wyœrodkowywuje okno do centrum ekranu.<br>
	 * @param w
	 */
	public static void centerWindow(Window w) {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Point xy = new Point(
				 (int) ((d.getWidth() - w.getWidth()) / 2),
				 (int) ((d.getHeight() - w.getHeight()) / 2)
				
		); 
		w.setLocation(xy);
	}
	
	/**
	 * Robi maksymalne okno.<br>
	 * @param w
	 */
	public static void maxWindow(Window w) {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		w.setLocation(0, 0);
		w.setPreferredSize(d);
	}
}
