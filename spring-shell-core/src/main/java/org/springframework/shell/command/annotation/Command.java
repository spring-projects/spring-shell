/*
 * Copyright 2023-2024 the original author or authors.
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

import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.shell.context.InteractionMode;

/**
 * Annotation marking a method to be a candicate for a shell command target.
 *
 * @author Janne Valkealahti
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Reflective
public @interface Command {

	/**
	 * Define command as an array. Given that command should be
	 * {@code command1 sub1} it can be defined as:
	 *
	 * <pre class="code">
	 * command = { "command1", "sub1" }
	 * command = "command1 sub1"
	 * </pre>
	 *
	 * Values are split and trimmed meaning spaces doesn't matter.
	 *
	 * <p>
	 * <b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings inherit this primary
	 * command to use it as a prefix.
	 *
	 * <pre class="code">
	 * &#64;Command(command = "command1")
	 * class MyCommands {
	 *
	 *   &#64;Command(command = "sub1")
	 *   void sub1(){}
	 * }
	 * </pre>
	 *
	 * @return the command as an array
	 */
	String[] command() default {};

	/**
	 * Define alias as an array. Given that alias should be
	 * {@code alias1 sub1} it can be defined as:
	 *
	 * <pre class="code">
	 * alias = { "alias1", "sub1" }
	 * alias = "alias1 sub1"
	 * </pre>
	 *
	 * Values are split and trimmed meaning spaces doesn't matter.
	 *
	 * <p>
	 * <b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings inherit this primary
	 * alias to use it as a prefix.
	 *
	 * <pre class="code">
	 * &#64;Command(alias = "alias1")
	 * class MyCommands {
	 *
	 *   &#64;Command(alias = "sub1")
	 *   void sub1(){}
	 * }
	 * </pre>
	 *
	 * @return the aliases as an array
	 */
	String[] alias() default {};

	/**
	 * Define a command group.
	 *
	 * <p>
	 * <b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level group inherit this primary
	 * group. Can be overridden on method-level.
	 *
	 * @return the command group
	 */
	String group() default "";

	/**
	 * Define a command description.
	 *
	 * <p>
	 * <b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level descriptions inherit this primary
	 * field. Can be overridden on method-level.
	 *
	 * @return the command description
	 */
	String description() default "";

	/**
	 * Define command to be hidden.
	 *
	 * <p>
	 * <b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings inherit this primary
	 * hidden field.
	 *
	 * <pre class="code">
	 * &#64;Command(hidden = true)
	 * class MyCommands {
	 *
	 *   &#64;Command
	 *   void sub1(){
	 *     // sub1 command is hidden
	 *   }
	 * }
	 * </pre>
	 *
	 * @return true if command should be hidden
	 */
	boolean hidden() default false;

	/**
	 * Define interaction mode for a command as a hint when command should be
	 * available. For example presense of some commands doesn't make sense if shell
	 * is running as non-interactive mode and vice versa.
	 *
	 * <p>
	 * <b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings inherit this primary
	 * field.
	 *
	 * Type is an array to be able to indicate that default don't have anyting defined.
	 *
	 * @return interaction modes
	 */
	InteractionMode[] interactionMode() default {};

}
