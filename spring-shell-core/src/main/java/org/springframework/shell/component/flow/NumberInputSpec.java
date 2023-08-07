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
import org.springframework.shell.component.NumberInput.NumberInputContext;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.flow.ComponentFlow.Builder;

/**
 * Interface for number input spec builder.
 *
 * @author Nicola Di Falco
 */
public interface NumberInputSpec extends BaseInputSpec<NumberInputSpec> {

	/**
	 * Sets a name.
	 *
	 * @param name the name
	 * @return a builder
	 */
	NumberInputSpec name(String name);

	/**
	 * Sets a result value.
	 *
	 * @param resultValue the result value
	 * @return a builder
	 */
	NumberInputSpec resultValue(Number resultValue);

	/**
	 * Sets a result mode.
	 *
	 * @param resultMode the result mode
	 * @return a builder
	 */
	NumberInputSpec resultMode(ResultMode resultMode);

	/**
	 * Sets a default value.
	 *
	 * @param defaultValue the defult value
	 * @return a builder
	 */
	NumberInputSpec defaultValue(Number defaultValue);

	/**
	 * Sets the class of the number. Defaults to Integer.
	 *
	 * @param clazz the specific number class
	 * @return a builder
	 */
	NumberInputSpec numberClass(Class<? extends Number> clazz);

	/**
	 * Sets input to required
	 *
	 * @return a builder
	 */
	NumberInputSpec required();

	/**
	 * Sets a renderer function.
	 *
	 * @param renderer the renderer
	 * @return a builder
	 */
	NumberInputSpec renderer(Function<NumberInputContext, List<AttributedString>> renderer);

	/**
	 * Sets a default renderer template location.
	 *
	 * @param location the template location
	 * @return a builder
	 */
	NumberInputSpec template(String location);

	/**
	 * Adds a pre-run context handler.
	 *
	 * @param handler the context handler
	 * @return a builder
	 */
	NumberInputSpec preHandler(Consumer<NumberInputContext> handler);

	/**
	 * Adds a post-run context handler.
	 *
	 * @param handler the context handler
	 * @return a builder
	 */
	NumberInputSpec postHandler(Consumer<NumberInputContext> handler);

	/**
	 * Automatically stores result from a {@link NumberInputContext} into
	 * {@link ComponentContext} with key given to builder. Defaults to {@code true}.
	 *
	 * @param store the flag if storing result
	 * @return a builder
	 */
	NumberInputSpec storeResult(boolean store);

	/**
	 * Define a function which may return id of a next component to go. Returning a
	 * {@code null} or non existent id indicates that flow should stop.
	 *
	 * @param next next component function
	 * @return a builder
	 */
	NumberInputSpec next(Function<NumberInputContext, String> next);

	/**
	 * Build and return parent builder.
	 *
	 * @return the parent builder
	 */
	Builder and();
}
