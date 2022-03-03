/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.shell.component.flow;

/**
 * Base spec for other specs.
 *
 * @author Janne Valkealahti
 */
public interface BaseInputSpec<T extends BaseInputSpec<T>> {

	/**
	 * Sets order of this component.
	 *
	 * @param order the order
	 * @return a builder
	 */
	T order(int order);

	/**
	 * Usual this trick to get typed child.
	 *
	 * @return a builder
	 */
	T getThis();
}