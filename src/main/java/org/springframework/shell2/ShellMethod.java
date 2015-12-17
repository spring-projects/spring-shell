package org.springframework.shell2;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a method as invokable via Spring Shell.
 *
 * @author Eric Bottard
 * @author Florent Biville
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ShellMethod {

	/**
	 * The name(s) by which this method can be invoked via Spring Shell. If not specified, the actual method name
	 * will be used (turning camelCase humps into "-").
	 */
	String[] value() default {};

	/**
	 * A description for the command. Should not contain any formatting (e.g. html) characters and would typically
	 * start with a capital letter and end with a dot.
	 */
	String help() default "";

	/**
	 * The prefix to use for assigning parameters by name.
	 */
	String prefix() default "--";

}
