package com.sarxos.ow;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ThreadPool {
	
    private List <DelegatingThread> threads = new ArrayList<DelegatingThread>();

    static class DelegatingThread extends Thread {
    
        private Runnable delegatee = null;
        
        public void setDelegatee(Runnable delegatee) {
            this.delegatee = delegatee;
        }

		@Override
		public void run() {
            this.delegatee.run();
		}        
    }
    
    public void put(DelegatingThread t) {
        threads.add(t);
    }
    
    public DelegatingThread get() {
        if (threads.size() != 0) {
            DelegatingThread available = (DelegatingThread) threads.remove(0);
            return available;
        }
        return null;
    }
}