package com.sarxos.transproxy;

/**
 * Okre�la stopie� przezroczysto�ci dla wywo�a� przez Transparent Proxy.<br>
 * @author Bartosz Firyn (SarXos)
 */
public enum Transparency {

	/**
	 * Bez przezroczysto�ci. Metoda jest wykonywana tylko w kontek�cie
	 * lokalnym.<br>
	 */
	NONE(1),
	/**
	 * P�przezroczysto��. Metoda jest wywo�ywana w kontek�cie lokalnym 
	 * i zdalnym jednocze�nie.<br> 
	 */
	HALF(2),
	/**
	 * Pe�na przezroczysto��. Metoda jest wywo�ywana tylko w kontek�cie
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
