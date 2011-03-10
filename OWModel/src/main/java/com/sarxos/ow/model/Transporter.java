package com.sarxos.ow.model;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Transporter {

	private static final Transporter INSTANCE = new Transporter();			
	
	/**
	 * Executor to execute {@link Transport} objects.
	 */
	private ExecutorService executor = null;
	
	/**
	 * Transport is a singleton instance.
	 */
	private Transporter() {
		this.executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
				60L, TimeUnit.SECONDS,
	            new SynchronousQueue<Runnable>()
		);
	}
	
	public static Transporter getInstance() {
		return INSTANCE; 
	}
	
	/**
	 * Execute given transport.
	 * 
	 * @param t - transport to execute.
	 * @return Simple {@link Future} object.
	 */
	public Future<Void> execute(Transport t) {
		return this.executor.submit(t);
	}
	
	public void shutdown() {
		this.executor.shutdownNow();
	}
}
