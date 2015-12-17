package org.springframework.shell2;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to customize handling of a {@link ShellMethod} parameters.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ShellOption {

	String NULL = "__NULL__";

	/**
	 * The key(s) (without the {@link ShellMethod#prefix()}) by which this parameter can be referenced
	 * when using named parameters. If none is specified, the actual method parameter name will be used.
	 */
	String[] value() default {};

	/**
	 * Return the number of input "words" this parameter consumes.
	 */
	int arity() default 1;

	/**
	 * The textual (pre-conversion) value to assign to this parameter if no value is provided by the user.
	 */
	String defaultValue() default NULL;
}
