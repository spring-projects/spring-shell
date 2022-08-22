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

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jline.utils.AttributedString;

import org.springframework.shell.component.ConfirmationInput.ConfirmationInputContext;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.flow.ComponentFlow.Builder;

/**
 * Interface for string input spec builder.
 *
 * @author Janne Valkealahti
 */
public interface ConfirmationInputSpec extends BaseInputSpec<ConfirmationInputSpec> {

	/**
	 * Sets a name.
	 *
	 * @param name the name
	 * @return a builder
	 */
	ConfirmationInputSpec name(String name);
	/**
	 * Sets a result value.
	 *
	 * @param resultValue the result value
	 * @return a builder
	 */
	ConfirmationInputSpec resultValue(Boolean resultValue);

	/**
	 * Sets a result mode.
	 *
	 * @param resultMode the result mode
	 * @return a builder
	 */
	ConfirmationInputSpec resultMode(ResultMode resultMode);

	/**
	 * Sets a default value.
	 *
	 * @param defaultValue the defult value
	 * @return a builder
	 */
	ConfirmationInputSpec defaultValue(Boolean defaultValue);

	/**
	 * Sets a renderer function.
	 *
	 * @param renderer the renderer
	 * @return a builder
	 */
	ConfirmationInputSpec renderer(Function<ConfirmationInputContext, List<AttributedString>> renderer);

	/**
	 * Sets a default renderer template location.
	 *
	 * @param location the template location
	 * @return a builder
	 */
	ConfirmationInputSpec template(String location);

	/**
	 * Adds a pre-run context handler.
	 *
	 * @param handler the context handler
	 * @return a builder
	 */
	ConfirmationInputSpec preHandler(Consumer<ConfirmationInputContext> handler);

	/**
	 * Adds a post-run context handler.
	 *
	 * @param handler the context handler
	 * @return a builder
	 */
	ConfirmationInputSpec postHandler(Consumer<ConfirmationInputContext> handler);

	/**
	 * Automatically stores result from a {@link ConfirmationInputContext} into
	 * {@link ComponentContext} with key given to builder. Defaults to {@code true}.
	 *
	 * @param store the flag if storing result
	 * @return a builder
	 */
	ConfirmationInputSpec storeResult(boolean store);

	/**
	 * Define a function which may return id of a next component to go. Returning a
	 * {@code null} or non existent id indicates that flow should stop.
	 *
	 * @param next next component function
	 * @return a builder
	 */
	ConfirmationInputSpec next(Function<ConfirmationInputContext, String> next);

	/**
	 * Build and return parent builder.
	 *
	 * @return the parent builder
	 */
	Builder and();
}