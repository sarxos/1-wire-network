package com.sarxos.ow.client.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.jgraph.JGraph;
import org.jgraph.graph.CellViewFactory;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import com.sarxos.ow.client.wrappers.AbstractDeviceWrapper;

public class OWGraph extends JGraph {

	private static final long serialVersionUID = 1L;

	/**
	 * Glowny kontruktor.
	 */
	public OWGraph() {
		super();

		setUI(new OWGraphUI());
		
		MarqueeHandler marquee = new MarqueeHandler();
		GraphModel model = new DefaultGraphModel();
		CellViewFactory factory = new OWCellViewFactory();
		GraphLayoutCache cache = new GraphLayoutCache(model, factory, true);
		
		setMarqueeHandler(marquee);
		setGraphLayoutCache(cache);
		
		setGridColor(new Color(0, 0, 128, 32));
		setGridMode(OWGraph.LINE_GRID_MODE);
		setGridVisible(true);
		setAntiAliased(true);
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(1052, 744));
	}
	
	public void insertDevice(AbstractDeviceWrapper wrapper) {
		if (wrapper == null) {
			throw new IllegalArgumentException("Wrapper can't be null.");
		}
		
		OWCell cell = new OWCell(wrapper);
		Map attr = cell.getAttributes();
		
		GraphConstants.setOpaque(attr, true);
		GraphConstants.setBackground(attr, new Color(0, 128, 0, 32));
		GraphConstants.setGradientColor(attr, new Color(0, 255, 0, 64));
		GraphConstants.setBounds(attr, new Rectangle2D.Double(25, 25, 100, 40));
		GraphConstants.setMoveable(attr, true);
		
		getGraphLayoutCache().insert(cell);
	}
}
