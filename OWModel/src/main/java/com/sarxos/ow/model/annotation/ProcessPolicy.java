package com.sarxos.ow.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Allow processing for given policy. Default policy is
 * {@link EventProcessorPolicy#CLASS}.
 * 
 * @author Bartosz Firyn (SarXos)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessPolicy {
	EventProcessorPolicy value() default EventProcessorPolicy.CLASS; 
}
