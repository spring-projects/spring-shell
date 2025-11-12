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
package org.springframework.shell.core.command.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation marking a method parameter to be a candidate for an option.
 *
 * @author Janne Valkealahti
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface Option {

	/**
	 * Long name of an option.
	 * @return Option long name, defaults to empty.
	 */
	String longName() default "";

	/**
	 * Short name of an option.
	 * @return Option short name, defaults to empty.
	 */
	char shortName() default ' ';

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
	 * Return a short description of the option.
	 * @return description of the option
	 */
	String description() default "";

	/**
	 * Define option arity.
	 * @return option arity
	 * @see #arityMin()
	 * @see #arityMax()
	 */
	int arity() default 0;

	/**
	 * Define option arity min. If Defined non-negative will be used instead of
	 * {@link #arity()}. If {@code arityMax} is not set non-negative it is set to same as
	 * this.
	 * @return option arity min
	 * @see #arity()
	 */
	int arityMin() default -1;

	/**
	 * Define option arity max. If Defined non-negative will be used instead of
	 * {@link #arity()}. If {@code arityMin} is not set non-negative it is set to zero.
	 * @return option arity max
	 * @see #arity()
	 */
	int arityMax() default -1;

}
