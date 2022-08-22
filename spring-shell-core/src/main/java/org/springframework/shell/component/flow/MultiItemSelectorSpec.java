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

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jline.utils.AttributedString;

import org.springframework.shell.component.MultiItemSelector.MultiItemSelectorContext;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.flow.ComponentFlow.Builder;
import org.springframework.shell.component.support.SelectorItem;

/**
 * Interface for multi input spec builder.
 *
 * @author Janne Valkealahti
 */
public interface MultiItemSelectorSpec extends BaseInputSpec<MultiItemSelectorSpec>{

	/**
	 * Sets a name.
	 *
	 * @param name the name
	 * @return a builder
	 */
	MultiItemSelectorSpec name(String name);

	/**
	 * Sets a result values.
	 *
	 * @param resultValues the result values
	 * @return a builder
	 */
	MultiItemSelectorSpec resultValues(List<String> resultValues);

	/**
	 * Sets a result mode.
	 *
	 * @param resultMode the result mode
	 * @return a builder
	 */
	MultiItemSelectorSpec resultMode(ResultMode resultMode);

	/**
	 * Adds a list of select items.
	 *
	 * @param selectItems the select items
	 * @return a builder
	 */
	MultiItemSelectorSpec selectItems(List<SelectItem> selectItems);

	/**
	 * Sets a {@link Comparator} for sorting items.
	 *
	 * @param comparator the item comparator
	 * @return a builder
	 */
	MultiItemSelectorSpec sort(Comparator<SelectorItem<String>> comparator);

	/**
	 * Sets a renderer function.
	 *
	 * @param renderer the renderer
	 * @return a builder
	 */
	MultiItemSelectorSpec renderer(Function<MultiItemSelectorContext<String, SelectorItem<String>>, List<AttributedString>> renderer);

	/**
	 * Sets a default renderer template location.
	 *
	 * @param location the template location
	 * @return a builder
	 */
	MultiItemSelectorSpec template(String location);

	/**
	 * Sets a maximum number of items in a selector list;
	 *
	 * @param max the maximum number of items
	 * @return a builder
	 */
	MultiItemSelectorSpec max(int max);

	/**
	 * Adds a pre-run context handler.
	 *
	 * @param handler the context handler
	 * @return a builder
	 */
	MultiItemSelectorSpec preHandler(Consumer<MultiItemSelectorContext<String, SelectorItem<String>>> handler);

	/**
	 * Adds a post-run context handler.
	 *
	 * @param handler the context handler
	 * @return a builder
	 */
	MultiItemSelectorSpec postHandler(Consumer<MultiItemSelectorContext<String, SelectorItem<String>>> handler);

	/**
	 * Automatically stores result from a {@link MultiItemSelectorContext} into
	 * {@link ComponentContext} with key given to builder. Defaults to {@code true}.
	 *
	 * @param store the flag if storing result
	 * @return a builder
	 */
	MultiItemSelectorSpec storeResult(boolean store);

	/**
	 * Define a function which may return id of a next component to go. Returning a
	 * {@code null} or non existent id indicates that flow should stop.
	 *
	 * @param next next component function
	 * @return a builder
	 */
	MultiItemSelectorSpec next(Function<MultiItemSelectorContext<String, SelectorItem<String>>, String> next);

	/**
	 * Build and return parent builder.
	 *
	 * @return the parent builder
	 */
	Builder and();
}