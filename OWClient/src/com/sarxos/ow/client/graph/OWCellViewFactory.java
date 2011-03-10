package com.sarxos.ow.client.graph;

import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.VertexView;

import com.sarxos.ow.client.ApplicationContext;
import com.sarxos.ow.client.graph.devices.ThermometerView;
import com.sarxos.ow.client.wrappers.AbstractDeviceWrapper;
import com.sarxos.ow.client.wrappers.Thermometer;


public class OWCellViewFactory extends DefaultCellViewFactory {

	/** 
	 * Tworzy widoki vertexów.<br>
	 * @param vertex dla którego chemy utworzyæ widok
	 * @return Zwraca nowy widok dla vertexu
	 * @see org.jgraph.graph.DefaultCellViewFactory#createVertexView(java.lang.Object)
	 */
	protected VertexView createVertexView(Object vertex) {
		
		if(vertex instanceof OWCell) {
			
			OWGraph graph = ApplicationContext.getInstance().getCurrentGraph();
			CellView cv = graph.getGraphLayoutCache().getMapping(vertex, false);
			
			if(cv != null) {
				return (VertexView) cv; 
			}
			
			OWCell owcell = (OWCell) vertex;
			AbstractDeviceWrapper wrapper = owcell.getDeviceWrapper();
			if (wrapper instanceof Thermometer) {
				return new ThermometerView(owcell);
			}
			
			return new OWCellView(owcell);
			
		}
		
		return super.createVertexView(vertex);
	}

}
