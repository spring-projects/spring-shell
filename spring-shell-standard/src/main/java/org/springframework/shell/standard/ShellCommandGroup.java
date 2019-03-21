/*
 * Copyright 2017 the original author or authors.
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

/**
 * Used to indicate the default group of shell commands, either at the package or class level.
 *
 * @author Eric Bottard
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Documented
public @interface ShellCommandGroup {

	/**
	 * The default value for the group label, which when set<ul>
	 *     <li>on a class, will mean to look at the package level</li>
	 *     <li>on a package, to go back at the class level and infer a name from the class name.</li>
	 * </ul>
	 */
	String INHERIT_AND_INFER = "";

	/**
	 * @return
	 * An explicit value for the group, which will apply to all commands in the owning class or package, depending
	 * on where this annotation is set.
	 */
	String value() default INHERIT_AND_INFER;

}
