package com.sarxos.transproxy;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("transport-request")
@SuppressWarnings("serial")
public class TransportRequest implements Serializable {

	/**
	 * Nazwa metody, kt�ra ma by� zdalnie wywo�ana.<br>
	 */
	@XStreamAlias("method-name")
	private String methodName = null;
	
	/**
	 * Typy argument�w zdalnej metody (aby wywo�a� odpowiedni� 
	 * metod� je�li jest przeci��ona).<br>
	 */
	@XStreamAlias("parameter-types")
	private Class[] parameterTypes = null;
	
	/**
	 * Argumenty do wywo�ania zdalnej metody.<br>
	 */
	private Object[] args = null;
	
	public TransportRequest(Method method, Object[] args) {
		this.methodName = method.getName();
		this.parameterTypes = method.getParameterTypes();
		this.args = args;
	}

	public Object[] getArgs() {
		return args;
	}

	public String getMethodName() {
		return methodName;
	}

	public Class[] getParameterTypes() {
		return parameterTypes;
	}
}
