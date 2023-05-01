/*
 * Copyright 2023 the original author or authors.
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
package org.springframework.shell.command.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.shell.command.CommandRegistration.OptionArity;

/**
 * Annotation marking a method parameter to be a candicate for an option.
 *
 * @author Janne Valkealahti
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface Option {

	/**
	 * Long names of an option. There can be multiple names where first is primary
	 * one and other are aliases.
	 *
	 * @return Option long names, defaults to empty.
	 */
	String[] longNames() default {};

	/**
	 * Short names of an option. There can be multiple names where first is primary
	 * one and other are aliases.
	 *
	 * @return Option short names, defaults to empty.
	 */
	char[] shortNames() default {};

	/**
	 * Mark option required.
	 *
	 * @return true if option is required, defaults to false.
	 */
	boolean required() default false;

	/**
	 * Define option default value.
	 *
	 * @return default value
	 */
	String defaultValue() default "";

	/**
	 * Return a short description of the option.
	 *
	 * @return description of the option
	 */
	String description() default "";

	/**
	 * Return a label of the option.
	 *
	 * @return label of the option
	 */
	String label() default "";

	/**
	 * Define option arity.
	 *
	 * @return option arity
	 * @see #arityMin()
	 * @see #arityMax()
	 */
	OptionArity arity() default OptionArity.NONE;

	/**
	 * Define option arity min. If Defined non-negative will be used instead of
	 * {@link #arity()}. If {@code arityMax} is not set non-negative it is set to
	 * same as this.
	 *
	 * @return option arity min
	 * @see #arity()
	 */
	int arityMin() default -1;

	/**
	 * Define option arity max. If Defined non-negative will be used instead of
	 * {@link #arity()}. If {@code arityMin} is not set non-negative it is set to
	 * zero.
	 *
	 * @return option arity max
	 * @see #arity()
	 */
	int arityMax() default -1;
}
