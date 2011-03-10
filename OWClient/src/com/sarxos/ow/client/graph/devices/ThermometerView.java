package com.sarxos.ow.client.graph.devices;

import org.jgraph.graph.CellViewRenderer;

import com.sarxos.ow.client.graph.OWCellRenderer;
import com.sarxos.ow.client.graph.OWCellView;

public class ThermometerView extends OWCellView {

	protected static CellViewRenderer renderer = new ThermometerRenderer();
	
	public ThermometerView(Object cell) {
		super(cell);
	}

	/** 
	 * Zwraca renderer dla danego widoku.
	 * @return Zwraca komponent renderuj¹cy
	 * @see org.jgraph.graph.AbstractCellView#getRenderer()
	 */
	public CellViewRenderer getRenderer() {
		return (OWCellRenderer) renderer;
	}
	
}
