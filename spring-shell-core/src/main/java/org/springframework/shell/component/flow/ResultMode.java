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
 * Enumeration of a modes instructing how {@code resultValue} is handled.
 *
 * @author Janne Valkealahti
 */
public enum ResultMode {

	/**
	 * Blindly accept a given {@code resultValue} resulting component run to be
	 * skipped and value set as a result automatically.
	 */
	ACCEPT,

	/**
	 * Verify a given {@code resultValue} with a component run. It is up to a
	 * component to define how it's done but usually result value is set as a
	 * default value which allows user to just continue.
	 */
	VERIFY
}