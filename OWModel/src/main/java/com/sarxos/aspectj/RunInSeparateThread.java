package com.sarxos.aspectj;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation means that annotated method will be executed in
 * separate {@link Thread}.
 * 
 * <br><br>
 * <b>Important:</b><br>
 * AspectJ support is obligatory if you want to use it.
 * 
 * @author Bartosz Firyn (SarXos)
 * @see SeparateThreadExecution
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RunInSeparateThread {

}
