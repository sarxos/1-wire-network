package com.sarxos.aspectj;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public aspect SeparateThreadExecution {

	private Executor executor = Executors.newCachedThreadPool();
	
	pointcut annotatedMethod() :
		execution(@RunInSeparateThread void * (..));
	
	void around() : annotatedMethod() {
		executor.execute(
				new Runnable() {
					@Override
					public void run() {
						proceed();
					}
				}
		);
	}
}
