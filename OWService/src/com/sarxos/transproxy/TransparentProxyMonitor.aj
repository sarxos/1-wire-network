package com.sarxos.transproxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

public abstract aspect TransparentProxyMonitor {

	public TransparentProxyMonitor() {
	}
	
	/**
	 * Wywo³uje zdalnie wykonywan¹ metodê.<br> 
	 * @param jp - aktualny join point
	 * @param remote - obiekt zdalny
	 */
	public Object adviceRemote(JoinPoint jp, Object remote) {

		Signature signature = jp.getSignature();
		if(signature instanceof MethodSignature) {

			MethodSignature ms = (MethodSignature) signature;
			Method method = ms.getMethod();
			Annotation[] annotations = method.getAnnotations();
			
			for(int i = 0; i < annotations.length; i++) {
				Annotation a = annotations[i];
				if(a.annotationType() == Transparent.class) {
					
					Object[] args = jp.getArgs();
					Transparent t = (Transparent) a;
					Transparency trans = t.value();
					
					if(trans == Transparency.HALF) {
						invokeRemote(method, args);
						return remote;
					} else if(trans == Transparency.FULL) {
						invokeRemote(method, args);
						return null;
					} else {
						return remote;
					}
				}
			}
		}
		
		return remote;
	}
	
	public void invokeRemote(Method method, Object[] args) {
		TransparentProxy.invokeRemote(method, args);
	}
}
