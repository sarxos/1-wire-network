package com.sarxos.transproxy;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("transport-response")
public class TransportResponse implements Serializable {

	private Object response = null;
	
	public TransportResponse(Object response) {
		this.response = response; 
	}
}
