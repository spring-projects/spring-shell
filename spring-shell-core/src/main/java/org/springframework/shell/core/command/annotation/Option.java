/*
 * Copyright 2025-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.core.command.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation marking a method parameter as an option to a {@link Command}.
 *
 * @author Janne Valkealahti
 * @author Mahmoud Ben Hassine
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface Option {

	/**
	 * Short name of an option.
	 * @return Option short name, defaults to empty.
	 */
	char shortName() default ' ';

	/**
	 * Long name of an option.
	 * @return Option long name, defaults to empty.
	 */
	String longName() default "";

	/**
	 * Return a short description of the option.
	 * @return description of the option
	 */
	String description() default "";

	/**
	 * Mark option required.
	 * @return true if option is required, defaults to false.
	 */
	boolean required() default false;

	/**
	 * Define option default value.
	 * @return default value
	 */
	String defaultValue() default "";

	/**
	 * Indicates whether this option should be completed in the command completer.
	 * @return true if the option should be completed, defaults to true.
	 */
	boolean completion() default true;

}
