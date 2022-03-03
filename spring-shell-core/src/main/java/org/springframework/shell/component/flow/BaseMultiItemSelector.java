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
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jline.utils.AttributedString;

import org.springframework.shell.component.MultiItemSelector.MultiItemSelectorContext;
import org.springframework.shell.component.flow.ComponentFlow.BaseBuilder;
import org.springframework.shell.component.flow.ComponentFlow.Builder;
import org.springframework.shell.component.support.SelectorItem;

/**
 * Base impl for {@link MultiItemSelectorSpec}.
 *
 * @author Janne Valkealahti
 */
public abstract class BaseMultiItemSelector extends BaseInput<MultiItemSelectorSpec> implements MultiItemSelectorSpec {

	private String name;
	private List<String> resultValues = new ArrayList<>();
	private ResultMode resultMode;
	private List<SelectItem> selectItems = new ArrayList<>();
	private Comparator<SelectorItem<String>> comparator;
	private Function<MultiItemSelectorContext<String, SelectorItem<String>>, List<AttributedString>> renderer;
	private Integer maxItems;
	private List<Consumer<MultiItemSelectorContext<String, SelectorItem<String>>>> preHandlers = new ArrayList<>();
	private List<Consumer<MultiItemSelectorContext<String, SelectorItem<String>>>> postHandlers = new ArrayList<>();
	private boolean storeResult = true;
	private String templateLocation;
	private Function<MultiItemSelectorContext<String, SelectorItem<String>>, String> next;

	public BaseMultiItemSelector(BaseBuilder builder, String id) {
		super(builder, id);
	}

	@Override
	public MultiItemSelectorSpec name(String name) {
		this.name = name;
		return this;
	}

	@Override
	public MultiItemSelectorSpec resultValues(List<String> resultValues) {
		this.resultValues.addAll(resultValues);
		return this;
	}

	@Override
	public MultiItemSelectorSpec resultMode(ResultMode resultMode) {
		this.resultMode = resultMode;
		return this;
	}

	@Override
	public MultiItemSelectorSpec selectItems(List<SelectItem> selectItems) {
		this.selectItems = selectItems;
		return this;
	}

	@Override
	public MultiItemSelectorSpec sort(Comparator<SelectorItem<String>> comparator) {
		this.comparator = comparator;
		return this;
	}

	@Override
	public MultiItemSelectorSpec renderer(Function<MultiItemSelectorContext<String, SelectorItem<String>>, List<AttributedString>> renderer) {
		this.renderer = renderer;
		return this;
	}

	@Override
	public MultiItemSelectorSpec template(String location) {
		this.templateLocation = location;
		return this;
	}

	@Override
	public MultiItemSelectorSpec max(int max) {
		this.maxItems = max;
		return this;
	}

	@Override
	public MultiItemSelectorSpec preHandler(Consumer<MultiItemSelectorContext<String, SelectorItem<String>>> handler) {
		this.preHandlers.add(handler);
		return this;
	}

	@Override
	public MultiItemSelectorSpec postHandler(Consumer<MultiItemSelectorContext<String, SelectorItem<String>>> handler) {
		this.postHandlers.add(handler);
		return this;
	}

	@Override
	public MultiItemSelectorSpec storeResult(boolean store) {
		this.storeResult = store;
		return this;
	}

	@Override
	public MultiItemSelectorSpec next(
			Function<MultiItemSelectorContext<String, SelectorItem<String>>, String> next) {
		this.next = next;
		return this;
	}

	@Override
	public Builder and() {
		getBuilder().addMultiItemSelector(this);
		return getBuilder();
	}

	@Override
	public MultiItemSelectorSpec getThis() {
		return this;
	}

	public String getName() {
		return name;
	}

	public List<String> getResultValues() {
		return resultValues;
	}

	public ResultMode getResultMode() {
		return resultMode;
	}

	public List<SelectItem> getSelectItems() {
		return selectItems;
	}

	public Comparator<SelectorItem<String>> getComparator() {
		return comparator;
	}

	public Function<MultiItemSelectorContext<String, SelectorItem<String>>, List<AttributedString>> getRenderer() {
		return renderer;
	}

	public String getTemplateLocation() {
		return templateLocation;
	}

	public Integer getMaxItems() {
		return maxItems;
	}

	public List<Consumer<MultiItemSelectorContext<String, SelectorItem<String>>>> getPreHandlers() {
		return preHandlers;
	}

	public List<Consumer<MultiItemSelectorContext<String, SelectorItem<String>>>> getPostHandlers() {
		return postHandlers;
	}

	public boolean isStoreResult() {
		return storeResult;
	}

	public Function<MultiItemSelectorContext<String, SelectorItem<String>>, String> getNext() {
		return next;
	}
}