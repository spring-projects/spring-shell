/*
 * Copyright 2022-2023 the original author or authors.
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
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jline.utils.AttributedString;

import org.springframework.shell.component.SingleItemSelector.SingleItemSelectorContext;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.flow.ComponentFlow.Builder;
import org.springframework.shell.component.support.SelectorItem;

/**
 * Interface for single item selector spec builder.
 *
 * @author Janne Valkealahti
 */
public interface SingleItemSelectorSpec extends BaseInputSpec<SingleItemSelectorSpec> {

	/**
	 * Sets a name.
	 *
	 * @param name the name
	 * @return a builder
	 */
	SingleItemSelectorSpec name(String name);

	/**
	 * Sets a result value.
	 *
	 * @param resultValue the result value
	 * @return a builder
	 */
	SingleItemSelectorSpec resultValue(String resultValue);

	/**
	 * Sets a result mode.
	 *
	 * @param resultMode the result mode
	 * @return a builder
	 */
	SingleItemSelectorSpec resultMode(ResultMode resultMode);

	/**
	 * Adds a select item.
	 *
	 * @param name the name
	 * @param item the item
	 * @return a builder
	 * @see #selectItems(List)
	 */
	SingleItemSelectorSpec selectItem(String name, String item);

	/**
	 * Adds a map of select items.
	 *
	 * @param selectItems the select items
	 * @return a builder
	 * @see #selectItems(List)
	 */
	SingleItemSelectorSpec selectItems(Map<String, String> selectItems);

	/**
	 * Adds a list of select items.
	 *
	 * @param selectItems the select items
	 * @return a builder
	 */
	SingleItemSelectorSpec selectItems(List<SelectItem> selectItems);

	/**
	 * Automatically selects and exposes a given item.
	 *
	 * @param name the name
	 * @return a builder
	 */
	SingleItemSelectorSpec defaultSelect(String name);

	/**
	 * Sets a {@link Comparator} for sorting items.
	 *
	 * @param comparator the item comparator
	 * @return a builder
	 */
	SingleItemSelectorSpec sort(Comparator<SelectorItem<String>> comparator);

	/**
	 * Sets a renderer function.
	 *
	 * @param renderer the renderer
	 * @return a builder
	 */
	SingleItemSelectorSpec renderer(Function<SingleItemSelectorContext<String, SelectorItem<String>>, List<AttributedString>> renderer);

	/**
	 * Sets a default renderer template location.
	 *
	 * @param location the template location
	 * @return a builder
	 */
	SingleItemSelectorSpec template(String location);

	/**
	 * Sets a maximum number of items in a selector list;
	 *
	 * @param max the maximum number of items
	 * @return a builder
	 */
	SingleItemSelectorSpec max(int max);

	/**
	 * Adds a pre-run context handler.
	 *
	 * @param handler the context handler
	 * @return a builder
	 */
	SingleItemSelectorSpec preHandler(Consumer<SingleItemSelectorContext<String, SelectorItem<String>>> handler);

	/**
	 * Adds a post-run context handler.
	 *
	 * @param handler the context handler
	 * @return a builder
	 */
	SingleItemSelectorSpec postHandler(Consumer<SingleItemSelectorContext<String, SelectorItem<String>>> handler);

	/**
	 * Automatically stores result from a {@link SingleItemSelectorContext} into
	 * {@link ComponentContext} with key given to builder. Defaults to {@code true}.
	 *
	 * @param store the flag if storing result
	 * @return a builder
	 */
	SingleItemSelectorSpec storeResult(boolean store);

	/**
	 * Define a function which may return id of a next component to go. Returning a
	 * {@code null} or non existent id indicates that flow should stop.
	 *
	 * @param next next component function
	 * @return a builder
	 */
	SingleItemSelectorSpec next(Function<SingleItemSelectorContext<String, SelectorItem<String>>, String> next);

	/**
	 * Build and return parent builder.
	 *
	 * @return the parent builder
	 */
	Builder and();
}
