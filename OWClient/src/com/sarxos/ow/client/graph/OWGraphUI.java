package com.sarxos.ow.client.graph;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

import javax.swing.JComponent;

import org.jgraph.plaf.basic.BasicGraphUI;

import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGUniverse;
import com.kitfox.svg.app.beans.SVGIcon;

public class OWGraphUI extends BasicGraphUI {

	private static final long serialVersionUID = 1L;
	
	private SVGIcon svgicon = null;
	
	public OWGraphUI() {
		super();
		
		SVGUniverse universe = SVGCache.getSVGUniverse();
		File f = new File("plan.svg");
		URI svguri = null;
		try {
			FileInputStream fis = new FileInputStream(f);
			svguri = universe.loadSVG(fis , "plan");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.svgicon = new SVGIcon();
		this.svgicon.setAntiAlias(true);
		this.svgicon.setSvgURI(svguri);
	}
	
	@Override
	protected void paintBackground(Graphics g) {
		Rectangle clip = g.getClipBounds();
		paintSchamatic(g);
		if (graph.isGridVisible()) {
			paintGrid(graph.getGridSize(), g, clip);
		}
	}
	
	protected void paintSchamatic(Graphics g) {
		this.svgicon.paintIcon(this.graph, g, 0, 0);
	}
	
}
