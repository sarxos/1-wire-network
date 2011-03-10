package com.sarxos.ow.model.impl;

import com.sarxos.ow.model.Device;
import com.sarxos.ow.model.Event;
import com.sarxos.ow.model.ProcessException;
import com.sarxos.ow.model.annotation.EventProcessorPolicy;
import com.sarxos.ow.model.annotation.ProcessPolicy;


/**
 * This is null {@link Device}. All events passed to the {@link Device#process(Event)}
 * method do nothing. 
 *   
 * @author Bartosz Firyn (SarXos)
 */
@ProcessPolicy(EventProcessorPolicy.ALL)
public class NullDevice extends AbstractDevice {

	/* (non-Javadoc)
	 * @see com.sarxos.ow.model.EventProcessor#process(com.sarxos.ow.model.Event)
	 */
	@Override
	public void process(Event e) throws ProcessException {
		// do nothing
	}
}
