package com.sarxos.ow;

import java.lang.Thread.UncaughtExceptionHandler;

public aspect ThreadPooling implements UncaughtExceptionHandler {
	
    ThreadPool pool = new ThreadPool();

    // Utworzenie nowego watku
    pointcut threadCreation(Runnable runnable) : 
    	call(Thread.new(Runnable)) &&
    	args(runnable);
    
    Thread around(Runnable runnable) : threadCreation(runnable) {
        ThreadPool.DelegatingThread availableThread = pool.get();
        if (availableThread == null) {
            availableThread = new ThreadPool.DelegatingThread();
            availableThread.setUncaughtExceptionHandler(this);
        }
        availableThread.setDelegatee(runnable);
        return availableThread;
    }
    
    // Czas pracy watku - sesja
    pointcut session(ThreadPool.DelegatingThread thread) : 
    	execution(void ThreadPool.DelegatingThread.run()) && 
    	this(thread);
    
    void around(ThreadPool.DelegatingThread thread) : session(thread) {
        while(true) {
            proceed(thread);
            pool.put(thread);
            synchronized(thread) {
                try {
                    thread.wait();
                } catch(InterruptedException ex) {
                }
            }
        }
    }
    
    // Uruchomienie watku    
    pointcut threadStart(ThreadPool.DelegatingThread thread) : 
    	call(void Thread.start()) && 
    	target(thread);
    
    void around(Thread thread) : threadStart(thread) {
        if (thread.isAlive()) {
            // wake it up
            synchronized(thread) {
                thread.notifyAll();
            }
        } else {
            proceed(thread);
        }
    }

	public void uncaughtException(Thread t, Throwable e) {
		e.printStackTrace();
	}
}