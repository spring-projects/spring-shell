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
package org.springframework.shell.component;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;

import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.support.AbstractSelectorComponent;
import org.springframework.shell.component.support.Enableable;
import org.springframework.shell.component.support.Itemable;
import org.springframework.shell.component.support.Matchable;
import org.springframework.shell.component.support.Nameable;
import org.springframework.shell.component.support.Selectable;
import org.springframework.shell.component.support.AbstractSelectorComponent.SelectorComponentContext;
import org.springframework.shell.component.SingleItemSelector.SingleItemSelectorContext;

/**
 * Component able to pick single item.
 *
 * @author Janne Valkealahti
 */
public class SingleItemSelector<T, I extends Nameable & Matchable & Enableable & Selectable & Itemable<T>>
		extends AbstractSelectorComponent<T, SingleItemSelectorContext<T, I>, I> {

	private SingleItemSelectorContext<T, I> currentContext;

	public SingleItemSelector(Terminal terminal, List<I> items, String name, Comparator<I> comparator) {
		super(terminal, name, items, true, comparator);
		setRenderer(new DefaultRenderer());
		setTemplateLocation("classpath:org/springframework/shell/component/single-item-selector-default.stg");
	}

	@Override
	public SingleItemSelectorContext<T, I> getThisContext(ComponentContext<?> context) {
		if (context != null && currentContext == context) {
			return currentContext;
		}
		currentContext = SingleItemSelectorContext.empty(getItemMapper());
		currentContext.setName(name);
		currentContext.setTerminalWidth(getTerminal().getWidth());
		if (currentContext.getItems() == null) {
			currentContext.setItems(getItems());
		}
		if (context != null) {
			context.stream().forEach(e -> {
				currentContext.put(e.getKey(), e.getValue());
			});
		}
		return currentContext;
	}

	@Override
	protected SingleItemSelectorContext<T, I> runInternal(SingleItemSelectorContext<T, I> context) {
		super.runInternal(context);
		// if there's no tty don't try to loop as it would then cause user interaction
		if (hasTty()) {
			loop(context);
		}
		return context;
	}

	/**
	 * Context {@link SingleItemSelector}.
	 */
	public interface SingleItemSelectorContext<T, I extends Nameable & Matchable & Itemable<T>>
			extends SelectorComponentContext<T, I, SingleItemSelectorContext<T, I>> {

		/**
		 * Gets a result item.
		 *
		 * @return a result item
		 */
		Optional<I> getResultItem();

		/**
		 * Gets a value.
		 *
		 * @return a value
		 */
		Optional<String> getValue();

		/**
		 * Creates an empty {@link SingleItemSelectorContext}.
		 *
		 * @return empty context
		 */
		static <C, I extends Nameable & Matchable & Itemable<C>> SingleItemSelectorContext<C, I> empty() {
			return new DefaultSingleItemSelectorContext<>();
		}

		/**
		 * Creates a {@link SingleItemSelectorContext}.
		 *
		 * @return context
		 */
		static <C, I extends Nameable & Matchable & Itemable<C>> SingleItemSelectorContext<C, I> empty(Function<C, String> itemMapper) {
			return new DefaultSingleItemSelectorContext<>(itemMapper);
		}
	}

	private static class DefaultSingleItemSelectorContext<T, I extends Nameable & Matchable & Itemable<T>> extends
			BaseSelectorComponentContext<T, I, SingleItemSelectorContext<T, I>> implements SingleItemSelectorContext<T, I> {

		private Function<T, String> itemMapper = item -> item.toString();

		DefaultSingleItemSelectorContext() {
		}

		DefaultSingleItemSelectorContext(Function<T, String> itemMapper) {
			this.itemMapper = itemMapper;
		}

		@Override
		public Optional<I> getResultItem() {
			if (getResultItems() == null) {
				return Optional.empty();
			}
			return getResultItems().stream().findFirst();
		}

		@Override
		public Optional<String> getValue() {
			return getResultItem().map(item -> itemMapper.apply(item.getItem()));
		}

		@Override
		public Map<String, Object> toTemplateModel() {
			Map<String, Object> attributes = super.toTemplateModel();
			getValue().ifPresent(value -> {
				attributes.put("value", value);
			});
			List<Map<String, Object>> rows = getItemStateView().stream()
				.map(is -> {
					Map<String, Object> map = new HashMap<>();
					map.put("name", is.getName());
					map.put("selected", getCursorRow().intValue() == is.getIndex());
					return map;
				})
				.collect(Collectors.toList());
			attributes.put("rows", rows);
			// finally wrap it into 'model' as that's what
			// we expect in stg template.
			Map<String, Object> model = new HashMap<>();
			model.put("model", attributes);
			return model;
		}

		@Override
		public String toString() {
			return "DefaultSingleItemSelectorContext [super=" + super.toString() + "]";
		}
	}

	private class DefaultRenderer implements Function<SingleItemSelectorContext<T, I>, List<AttributedString>> {

		@Override
		public List<AttributedString> apply(SingleItemSelectorContext<T, I> context) {
			return renderTemplateResource(context.toTemplateModel());
		}
	}
}
