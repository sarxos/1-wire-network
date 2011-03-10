package com.sarxos.ow.client.graph;

import org.jgraph.graph.DefaultGraphCell;

import com.sarxos.ow.client.wrappers.AbstractDeviceWrapper;

public class OWCell extends DefaultGraphCell {

	private static final long serialVersionUID = 1L;
	
	private AbstractDeviceWrapper deviceWrappe = null;
	
	public OWCell(AbstractDeviceWrapper wrapper) {
		this.deviceWrappe = wrapper;
	}

	public AbstractDeviceWrapper getDeviceWrapper() {
		return deviceWrappe;
	}
}
