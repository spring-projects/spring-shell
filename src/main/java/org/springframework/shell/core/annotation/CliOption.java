/*
 * Copyright 2011-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.shell.core.Converter;

/**
 * Annotates the arguments of a command methods, allowing it to declare the argument value as mandatory or optional with a default value.
 * 
 * @author Ben Alex
 * @since 1.0
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface CliOption {

	/**
	 * @return if true, the user cannot specify this option and it is provided by the shell infrastructure
	 * (defaults to false)
	 */
	boolean systemProvided() default false;

	/**
	 * @return the name of the option, which must be unique within this {@link CliCommand} (an empty String may
	 * be given, which would denote this option is the default for the command)
	 */
	String[] key();

	/**
	 * @return true if this option must be specified one way or the other by the user (defaults to false)
	 */
	boolean mandatory() default false;

	/**
	 * @return the default value to use if this option is unspecified by the user (defaults to __NULL__, which causes null to
	 * be presented to any non-primitive parameter)
	 */
	String unspecifiedDefaultValue() default "__NULL__";

	/**
	 * @return the default value to use if this option is included by the user, but they didn't specify an
	 * actual value (most commonly used for flags; defaults to __NULL__, which causes null to
	 * be presented to any non-primitive parameter)
	 */
	String specifiedDefaultValue() default "__NULL__";

	/**
	 * Returns a string providing context-specific information (e.g. a comma-delimited
	 * set of keywords) to the {@link Converter} that handles the annotated parameter's type.
	 * <p>
	 * For example, if a method parameter "thing" of type "Thing" is annotated as
	 * follows:
	 * <pre>@CliOption(..., optionContext = "foo,bar", ...) Thing thing</pre>
	 * ... then the {@link Converter} that converts the text entered by the user
	 * into an instance of Thing will be passed "foo,bar" as the value of the
	 * <code>optionContext</code> parameter in its public methods. This allows
	 * the behaviour of that Converter to be individually customised for each
	 * {@link CliOption} of each {@link CliCommand}.
	 *
	 * @return a non-<code>null</code> string (can be empty)
	 */
	String optionContext() default "";

	/**
	 * @return a help message for this option (the default is a blank String, which means there is no help)
	 */
	String help() default "";
}
