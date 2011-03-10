package com.sarxos.ow.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotacja okreslajaca czy dana metoda klasy ma byc dostepna z poziomu skryptu. 
 * Annotacja musi byc uzywana lacznie z aspektem dostepu do klas.<br>
 * @author Bartosz Firyn (SarXos)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface AllowScriptAcces {
	boolean value() default true;
}
