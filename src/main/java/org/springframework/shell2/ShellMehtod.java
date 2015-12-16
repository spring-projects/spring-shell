package org.springframework.shell2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ericbottard on 27/11/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ShellMehtod {

	String[] value() default "";

	String help() default "";

	String prefix() default "--";

}
