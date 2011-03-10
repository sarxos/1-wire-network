package com.sarxos.ow.client.graph.devices;

import org.jgraph.graph.CellView;

import com.sarxos.ow.client.graph.OWCell;
import com.sarxos.ow.client.graph.OWCellRenderer;
import com.sarxos.ow.client.wrappers.Thermometer;

public class ThermometerRenderer extends OWCellRenderer {

	private static final long serialVersionUID = 1L;

	public ThermometerRenderer() {
		super();
	}
	
	@Override
	protected void installAttributes(CellView view) {
		super.installAttributes(view);
		
		OWCell cell = (OWCell) view.getCell();
		Thermometer therm = (Thermometer) cell.getDeviceWrapper();
		setText(therm.getTemp() + "'C");
	}
	
	@Override
	protected void resetAttributes() {
		super.resetAttributes();
		setText("");
	}
}
