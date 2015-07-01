package org.springframework.shell.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation on a command method, that may be converter to a 
 * Spring Shell converter, allowing it to use Spring Type Converters 
 * for formatting the output of the command result instance
 *
 * @author robin
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface CliPrinter {
	
	public static final String NULL_VALUE = "__NULL__";

	/**
	 * @return the short name of the type converter
	 */
	String key() default "p";
	
	String defaultValue() default NULL_VALUE;

}
