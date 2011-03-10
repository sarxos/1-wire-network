package com.sarxos.ow.model;


/**
 * This interface implements only one method used to process the 
 * {@link Event}.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public interface EventProcessor {

	/**
	 * Process given {@link Event}. For {@link Device} it means to consume
	 * {@link Event}, but for {@link Pipe} it means that {@link Event} must be
	 * passed from source and processed be target.
	 * 
	 * <br><br>
	 * The idea is simple:
	 * <ol>
	 * <li>source {@link Device} passes the {@link Event} to {@link Pipe},</li>
	 * <li>{@link Pipe} passes the Event to target {@link Device},</li>
	 * <li>target {@link Device} processes the {@link Event}.</li>
	 * </ol>
	 * 
	 * @param event - the {@link Event} to process.
	 */
	public void process(Event event) throws ProcessException;
	
	/**
	 * Return true if {@link Event} can be processed by this {@link Device}.
	 * 
	 * @param event - {@link Event} to check if can be processed
	 * @return true if {@link Event} can be processed, false in other case
	 */
	public boolean canProcess(Event event); 
	
}
