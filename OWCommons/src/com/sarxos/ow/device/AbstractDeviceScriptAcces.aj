package com.sarxos.ow.device;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import com.sarxos.ow.annotations.AllowScriptAcces;

public aspect AbstractDeviceScriptAcces {

	pointcut scriptVoidMethodInvocation() :	
		execution(@AllowScriptAcces void *(..));
	
	pointcut scriptMethodInvocation() :	
		execution(@AllowScriptAcces * *(..));
	
	Object around() : scriptMethodInvocation() {

//		Signature signature = thisJoinPoint.getSignature();
//		if(signature instanceof MethodSignature) {
//
//			MethodSignature ms = (MethodSignature) signature;
//			Method method = ms.getMethod();
//
//			Annotation[] annotations = method.getAnnotations();
//			for(int i = 0; i < annotations.length; i++) {
//				Annotation a = annotations[i];
//				if(a.annotationType() == AllowScriptAcces.class) {
//					AllowScriptAcces asa = (AllowScriptAcces) a;
//					if(asa.value() == true) {
//						return proceed();
//					}
//				}
//			}
//		}
//		
//		throw new RuntimeException("Acces to this method from script is restricted.");
		return proceed();
	}

	void around() : scriptVoidMethodInvocation() {
		proceed();
	}
	
}
