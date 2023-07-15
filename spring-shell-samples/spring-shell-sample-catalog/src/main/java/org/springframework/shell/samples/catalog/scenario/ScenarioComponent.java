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
package org.springframework.shell.samples.catalog.scenario;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

/**
 * Annotation needed for a scenarios to get hooked up into a catalog app.
 * Typically all fields in this annotation needs to have content to get attached
 * into a catalog app.
 *
 * @author Janne Valkealahti
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@Component
public @interface ScenarioComponent {

	/**
	 * Define a name of a scenario.
	 *
	 * @return name of a scenario
	 */
	String name() default "";

	/**
	 * Define a short description of a scenario.
	 *
	 * @return short description of a scenario
	 */
	String description() default "";

	/**
	 * Define a categories of a scenario.
	 *
	 * @return categories of a scenario
	 */
	String[] category() default {};
}
