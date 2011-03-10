package com.sarxos.transproxy;

/**
 * Okreœla stopieñ przezroczystoœci dla wywo³añ przez Transparent Proxy.<br>
 * @author Bartosz Firyn (SarXos)
 */
public enum Transparency {

	/**
	 * Bez przezroczystoœci. Metoda jest wykonywana tylko w kontekœcie
	 * lokalnym.<br>
	 */
	NONE(1),
	/**
	 * Pó³przezroczystoœæ. Metoda jest wywo³ywana w kontekœcie lokalnym 
	 * i zdalnym jednoczeœnie.<br> 
	 */
	HALF(2),
	/**
	 * Pe³na przezroczystoœæ. Metoda jest wywo³ywana tylko w kontekœcie
	 * zdalnym.<br> 
	 */
	FULL(4);

	private int id;
	
	private Transparency(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
