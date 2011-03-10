package com.sarxos.transproxy;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("transport-request")
@SuppressWarnings("serial")
public class TransportRequest implements Serializable {

	/**
	 * Nazwa metody, która ma byæ zdalnie wywo³ana.<br>
	 */
	@XStreamAlias("method-name")
	private String methodName = null;
	
	/**
	 * Typy argumentów zdalnej metody (aby wywo³aæ odpowiedni¹ 
	 * metodê jeœli jest przeci¹¿ona).<br>
	 */
	@XStreamAlias("parameter-types")
	private Class[] parameterTypes = null;
	
	/**
	 * Argumenty do wywo³ania zdalnej metody.<br>
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
