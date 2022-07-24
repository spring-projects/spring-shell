/*
 * Copyright 2015-2022 the original author or authors.
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
package org.springframework.shell.standard;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.shell.context.InteractionMode;

/**
 * Used to mark a method as invokable via Spring Shell.
 *
 * @author Eric Bottard
 * @author Florent Biville
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
@Reflective
public @interface ShellMethod {

	/**
	 * The default value for {@link #group()}, meaning that the group will be inherited from the explicit value set
	 * on the containing element (class then package) or ultimately inferred.
	 * @see ShellCommandGroup
	 */
	String INHERITED = "";

	/**
	 * The name(s) by which this method can be invoked via Spring Shell. If not specified, the actual method name
	 * will be used (turning camelCase humps into "-").
	 * @return explicit command name(s) to use
	 */
	String[] key() default {};

	/**
	 * A description for the command. Should not contain any formatting (e.g. html) characters and would typically
	 * start with a capital letter and end with a dot.
	 * @return short description of what the command does
	 */
	String value() default "";

	/**
	 * The prefix to use for assigning parameters by name.
	 * @return prefix to use when not specified as part of the parameter annotation
	 */
	String prefix() default "--";

	/**
	 * The command group which this command belongs to. The command group is used when printing a list of
	 * commands to group related commands. By default, group is first looked up from owning class then package,
	 * and if not explicitly set, is inferred from class name.
	 * @return name of the command group
	 */
	String group() default INHERITED;

	/**
	 * Defines interaction mode for a command as a hint when command should be
	 * available. For example presense of some commands doesn't make sense if shell
	 * is running as non-interactive mode and vice versa.
	 *
	 * Defaults to {@link InteractionMode#ALL}
	 *
	 * @return interaction mode
	 */
	InteractionMode interactionMode() default InteractionMode.ALL;
}
