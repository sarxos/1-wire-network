package com.sarxos.ow.model.impl;

import com.sarxos.ow.model.Device;
import com.sarxos.ow.model.Event;



/**
 * 
 * @author Bartosz Firyn (SarXos)
 */
public abstract class AbstractEvent implements Event {

	private Device source = null;
	
	/**
	 * @throws IllegalArgumentException when null source is passed.
	 */
	public AbstractEvent(Device source) {
		if (source == null) {
			throw new IllegalArgumentException("Event source cannot be null.");
		}
		this.source = source;
	}

	/* (non-Javadoc)
	 * @see com.sarxos.ow.model.Event#getSource()
	 */
	@Override
	public Device getSource() {
		return source;
	}

}
