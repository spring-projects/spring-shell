/*
 * Copyright 2023-present the original author or authors.
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

import org.springframework.aot.hint.annotation.Reflective;

/**
 * Annotation marking a method to be a shell command. The declaring class should be
 * defined as a Spring bean in the application context.
 *
 * @author Janne Valkealahti
 * @author Mahmoud Ben Hassine
 * @author Piotr Olaszewski
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Reflective
public @interface Command {

	/**
	 * Define command as an array. Given that command should be {@code command1 sub1} it
	 * can be defined as:
	 *
	 * <pre class="code">
	 * name = { "command1", "sub1" }
	 * name = "command1 sub1"
	 * </pre>
	 *
	 * Values are split and trimmed meaning spaces doesn't matter.
	 * @return the command as an array
	 */
	String name() default "";

	/**
	 * Define alias as an array. Given that alias should be {@code alias1 sub1} it can be
	 * defined as:
	 *
	 * <pre class="code">
	 * alias = { "alias1", "sub1" }
	 * alias = "alias1 sub1"
	 * </pre>
	 *
	 * Values are split and trimmed meaning spaces doesn't matter.
	 * @return the aliases as an array
	 */
	String[] alias() default {};

	/**
	 * Define a command group.
	 * @return the command group
	 */
	String group() default "";

	/**
	 * Define a command description.
	 * @return the command description
	 */
	String description() default "";

	/**
	 * Define command to be hidden.
	 * @return true if command should be hidden
	 */
	boolean hidden() default false;

	/**
	 * Define a command help message.
	 * @return the command help message
	 */
	String help() default "";

}
