package com.sarxos.ow.client.graph.devices;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;

import com.sarxos.ow.client.graph.OWCell;
import com.sarxos.ow.client.wrappers.AbstractDeviceWrapper;

public class ThermometerPopup extends JPopupMenu {

	protected class EditScript extends AbstractAction {

		public EditScript() {
			super("Edit device script");
		}
		
		public void actionPerformed(ActionEvent e) {
			AbstractDeviceWrapper adw = cell.getDeviceWrapper();
			String script = adw.getScript();
			System.out.println(script);
			
		}
	}
	
	private OWCell cell = null;
	
	public ThermometerPopup(Object cell) {
		super();
		
		this.cell = (OWCell) cell;
		
		add(new EditScript());
	}
}
