package org.springframework.shell2;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ericbottard on 27/11/15.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ShellOption {

	String NULL = "__NULL__";

	String[] value() default {};

	int arity() default 1;

	String defaultValue() default NULL;
}
