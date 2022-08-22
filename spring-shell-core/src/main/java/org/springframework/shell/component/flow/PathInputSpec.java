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

import org.springframework.shell.component.PathInput.PathInputContext;
import org.springframework.shell.component.StringInput.StringInputContext;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.flow.ComponentFlow.Builder;

/**
 * Interface for path input spec builder.
 *
 * @author Janne Valkealahti
 */
public interface PathInputSpec extends BaseInputSpec<PathInputSpec> {

	/**
	 * Sets a name.
	 *
	 * @param name the name
	 * @return a builder
	 */
	PathInputSpec name(String name);

	/**
	 * Sets a result value.
	 *
	 * @param resultValue the result value
	 * @return a builder
	 */
	PathInputSpec resultValue(String resultValue);

	/**
	 * Sets a result mode.
	 *
	 * @param resultMode the result mode
	 * @return a builder
	 */
	PathInputSpec resultMode(ResultMode resultMode);

	/**
	 * Sets a default value.
	 *
	 * @param defaultValue the defult value
	 * @return a builder
	 */
	PathInputSpec defaultValue(String defaultValue);

	/**
	 * Sets a renderer function.
	 *
	 * @param renderer the renderer
	 * @return a builder
	 */
	PathInputSpec renderer(Function<PathInputContext, List<AttributedString>> renderer);

	/**
	 * Sets a default renderer template location.
	 *
	 * @param location the template location
	 * @return a builder
	 */
	PathInputSpec template(String location);

	/**
	 * Adds a pre-run context handler.
	 *
	 * @param handler the context handler
	 * @return a builder
	 */
	PathInputSpec preHandler(Consumer<PathInputContext> handler);

	/**
	 * Adds a post-run context handler.
	 *
	 * @param handler the context handler
	 * @return a builder
	 */
	PathInputSpec postHandler(Consumer<PathInputContext> handler);

	/**
	 * Automatically stores result from a {@link StringInputContext} into
	 * {@link ComponentContext} with key given to builder. Defaults to {@code true}.
	 *
	 * @param store the flag if storing result
	 * @return a builder
	 */
	PathInputSpec storeResult(boolean store);

	/**
	 * Define a function which may return id of a next component to go. Returning a
	 * {@code null} or non existent id indicates that flow should stop.
	 *
	 * @param next next component function
	 * @return a builder
	 */
	PathInputSpec next(Function<PathInputContext, String> next);

	/**
	 * Build and return parent builder.
	 *
	 * @return the parent builder
	 */
	Builder and();
}