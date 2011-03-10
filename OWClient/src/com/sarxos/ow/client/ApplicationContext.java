package com.sarxos.ow.client;

import com.sarxos.ow.client.graph.OWGraph;

public class ApplicationContext {

	private static ApplicationContext instance = new ApplicationContext();;
	
	private OWGraph graph = null;
	
	
	private ApplicationContext() {
	}
	
	public static ApplicationContext getInstance() {
		return instance;
	}
	
	public OWGraph getCurrentGraph() {
		return graph;
	}

	public void setCurrentGraph(OWGraph graph) {
		this.graph = graph;
	}

}
