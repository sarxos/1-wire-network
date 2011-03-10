package com.sarxos.ow.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sarxos.ow.model.Event;
import com.sarxos.ow.model.EventProcessor;


/**
 * This annotation adds given class to the list of {@link Event} subclasses which
 * can be processed by selected {@link EventProcessor} instance.
 * 
 * @author Bartosz Firyn (SarXos)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessEventClass {
	Class<? extends Event> value();
	EventProcessorPolicy policy() default EventProcessorPolicy.CLASS; 
}
