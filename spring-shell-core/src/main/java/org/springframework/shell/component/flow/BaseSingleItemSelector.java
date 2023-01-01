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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jline.utils.AttributedString;

import org.springframework.shell.component.SingleItemSelector.SingleItemSelectorContext;
import org.springframework.shell.component.flow.ComponentFlow.BaseBuilder;
import org.springframework.shell.component.flow.ComponentFlow.Builder;
import org.springframework.shell.component.support.SelectorItem;

/**
 * Base impl for {@link SingleItemSelectorSpec}.
 *
 * @author Janne Valkealahti
 */
public abstract class BaseSingleItemSelector extends BaseInput<SingleItemSelectorSpec> implements SingleItemSelectorSpec {

	private String name;
	private String resultValue;
	private ResultMode resultMode;
	private Map<String, String> selectItems = new HashMap<>();
	private String defaultSelect;
	private Comparator<SelectorItem<String>> comparator;
	private Function<SingleItemSelectorContext<String, SelectorItem<String>>, List<AttributedString>> renderer;
	private Integer maxItems;
	private List<Consumer<SingleItemSelectorContext<String, SelectorItem<String>>>> preHandlers = new ArrayList<>();
	private List<Consumer<SingleItemSelectorContext<String, SelectorItem<String>>>> postHandlers = new ArrayList<>();
	private boolean storeResult = true;
	private String templateLocation;
	private Function<SingleItemSelectorContext<String, SelectorItem<String>>, String> next;

	public BaseSingleItemSelector(BaseBuilder builder, String id) {
		super(builder, id);
	}

	@Override
	public SingleItemSelectorSpec name(String name) {
		this.name = name;
		return this;
	}

	@Override
	public SingleItemSelectorSpec resultValue(String resultValue) {
		this.resultValue = resultValue;
		return this;
	}

	@Override
	public SingleItemSelectorSpec resultMode(ResultMode resultMode) {
		this.resultMode = resultMode;
		return this;
	}

	@Override
	public SingleItemSelectorSpec selectItem(String name, String item) {
		this.selectItems.put(name, item);
		return this;
	}

	@Override
	public SingleItemSelectorSpec selectItems(Map<String, String> selectItems) {
		this.selectItems.putAll(selectItems);
		return this;
	}

	@Override
	public SingleItemSelectorSpec defaultSelect(String name) {
		this.defaultSelect = name;
		return this;
	}

	@Override
	public SingleItemSelectorSpec sort(Comparator<SelectorItem<String>> comparator) {
		this.comparator = comparator;
		return this;
	}

	@Override
	public SingleItemSelectorSpec renderer(Function<SingleItemSelectorContext<String, SelectorItem<String>>, List<AttributedString>> renderer) {
		this.renderer = renderer;
		return this;
	}

	@Override
	public SingleItemSelectorSpec template(String location) {
		this.templateLocation = location;
		return this;
	}

	@Override
	public SingleItemSelectorSpec max(int max) {
		this.maxItems = max;
		return this;
	}

	@Override
	public SingleItemSelectorSpec preHandler(Consumer<SingleItemSelectorContext<String, SelectorItem<String>>> handler) {
		this.preHandlers.add(handler);
		return this;
	}

	@Override
	public SingleItemSelectorSpec postHandler(Consumer<SingleItemSelectorContext<String, SelectorItem<String>>> handler) {
		this.postHandlers.add(handler);
		return this;
	}

	@Override
	public SingleItemSelectorSpec storeResult(boolean store) {
		this.storeResult = store;
		return this;
	}

	@Override
	public SingleItemSelectorSpec next(
			Function<SingleItemSelectorContext<String, SelectorItem<String>>, String> next) {
		this.next = next;
		return this;
	}

	@Override
	public Builder and() {
		getBuilder().addSingleItemSelector(this);
		return getBuilder();
	}

	@Override
	public SingleItemSelectorSpec getThis() {
		return this;
	}

	public String getName() {
		return name;
	}

	public String getResultValue() {
		return resultValue;
	}

	public ResultMode getResultMode() {
		return resultMode;
	}

	public Map<String, String> getSelectItems() {
		return selectItems;
	}

	public String getDefaultSelect() {
		return defaultSelect;
	}

	public Comparator<SelectorItem<String>> getComparator() {
		return comparator;
	}

	public Function<SingleItemSelectorContext<String, SelectorItem<String>>, List<AttributedString>> getRenderer() {
		return renderer;
	}

	public String getTemplateLocation() {
		return templateLocation;
	}

	public Integer getMaxItems() {
		return maxItems;
	}

	public List<Consumer<SingleItemSelectorContext<String, SelectorItem<String>>>> getPreHandlers() {
		return preHandlers;
	}

	public List<Consumer<SingleItemSelectorContext<String, SelectorItem<String>>>> getPostHandlers() {
		return postHandlers;
	}

	public boolean isStoreResult() {
		return storeResult;
	}

	public Function<SingleItemSelectorContext<String, SelectorItem<String>>, String> getNext() {
		return next;
	}
}