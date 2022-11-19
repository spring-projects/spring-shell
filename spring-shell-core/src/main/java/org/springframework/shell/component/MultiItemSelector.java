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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.shell.component.MultiItemSelector.MultiItemSelectorContext;

/**
 * Component able to pick multiple items.
 *
 * @author Janne Valkealahti
 */
public class MultiItemSelector<T, I extends Nameable & Matchable & Enableable & Selectable & Itemable<T>>
		extends AbstractSelectorComponent<T, MultiItemSelectorContext<T, I>, I> {

	private MultiItemSelectorContext<T, I> currentContext;

	public MultiItemSelector(Terminal terminal, List<I> items, String name, Comparator<I> comparator) {
		super(terminal, name, items, false, comparator);
		setRenderer(new DefaultRenderer());
		setTemplateLocation("classpath:org/springframework/shell/component/multi-item-selector-default.stg");
	}

	@Override
	public MultiItemSelectorContext<T, I> getThisContext(ComponentContext<?> context) {
		if (context != null && currentContext == context) {
			return currentContext;
		}
		currentContext = MultiItemSelectorContext.empty(getItemMapper());
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
	protected MultiItemSelectorContext<T, I> runInternal(MultiItemSelectorContext<T, I> context) {
		super.runInternal(context);
		// if there's no tty don't try to loop as it would then cause user interaction
		if (hasTty()) {
			loop(context);
		}
		return context;
	}

	/**
	 * Context {@link MultiItemSelector}.
	 */
	public interface MultiItemSelectorContext<T, I extends Nameable & Matchable & Itemable<T>>
			extends SelectorComponentContext<T, I, MultiItemSelectorContext<T, I>> {

		/**
		 * Gets a values.
		 *
		 * @return a values
		 */
		List<String> getValues();

		/**
		 * Creates an empty {@link MultiItemSelectorContext}.
		 *
		 * @return empty context
		 */
		static <T, I extends Nameable & Matchable & Itemable<T>> MultiItemSelectorContext<T, I> empty() {
			return new DefaultMultiItemSelectorContext<>();
		}

		/**
		 * Creates an {@link MultiItemSelectorContext}.
		 *
		 * @return context
		 */
		static <T, I extends Nameable & Matchable & Itemable<T>> MultiItemSelectorContext<T, I> empty(Function<T, String> itemMapper) {
			return new DefaultMultiItemSelectorContext<>(itemMapper);
		}
	}

	private static class DefaultMultiItemSelectorContext<T, I extends Nameable & Matchable & Itemable<T>> extends
			BaseSelectorComponentContext<T, I, MultiItemSelectorContext<T, I>> implements MultiItemSelectorContext<T, I> {

		private Function<T, String> itemMapper = item -> item.toString();

		DefaultMultiItemSelectorContext() {
		}

		DefaultMultiItemSelectorContext(Function<T, String> itemMapper) {
			this.itemMapper = itemMapper;
		}

		@Override
		public List<String> getValues() {
			if (getResultItems() == null) {
				return Collections.emptyList();
			}
			return getResultItems().stream()
					.map(i -> i.getItem())
					.map(i -> itemMapper.apply(i))
					.collect(Collectors.toList());
		}

		@Override
		public Map<String, Object> toTemplateModel() {
			Map<String, Object> attributes = super.toTemplateModel();
			attributes.put("values", getValues());
			List<Map<String, Object>> rows = getItemStateView().stream()
				.map(is -> {
					Map<String, Object> map = new HashMap<>();
					map.put("name", is.getName());
					map.put("selected", is.isSelected());
					map.put("onrow", getCursorRow().intValue() == is.getIndex());
					map.put("enabled", is.isEnabled());
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
	}

	private class DefaultRenderer implements Function<MultiItemSelectorContext<T, I>, List<AttributedString>> {

		@Override
		public List<AttributedString> apply(MultiItemSelectorContext<T, I> context) {
			return renderTemplateResource(context.toTemplateModel());
		}
	}
}
