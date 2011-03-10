package com.sarxos.ow.model.impl;

import com.sarxos.ow.model.Device;
import com.sarxos.ow.model.Event;
import com.sarxos.ow.model.ProcessException;
import com.sarxos.ow.model.annotation.EventProcessorPolicy;
import com.sarxos.ow.model.annotation.ProcessPolicy;


/**
 * The simplest {@link Device} ever.
 * 
 * @author Bartosz Firyn (SarXos)
 */
@ProcessPolicy(EventProcessorPolicy.ALL)
public class BasicDevice extends AbstractDevice {

	/* (non-Javadoc)
	 * @see com.sarxos.ow.model.EventProcessor#process(com.sarxos.ow.model.Event)
	 */
	@Override
	public void process(Event e) throws ProcessException {
		System.out.println(e);
	}
}
