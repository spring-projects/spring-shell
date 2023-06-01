/*
 * Copyright 2015 the original author or authors.
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
import org.springframework.shell.AvailabilityReflectiveProcessor;
import org.springframework.stereotype.Component;

/**
 * Indicates that an annotated class may contain shell methods (themselves annotated with {@link ShellMethod}) that
 * is,
 * methods that may be invoked reflectively by the shell.
 *
 * <p>This annotation is a specialization of {@link Component}.</p>
 *
 * @author Eric Bottard
 * @see Component
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
@Reflective(AvailabilityReflectiveProcessor.class)
public @interface ShellComponent {

	/**
	 * Used to indicate a suggestion for a logical name for the component.
	 * @return the suggested component name, if any
	 */
	String value() default "";
}
