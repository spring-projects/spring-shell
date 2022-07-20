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
package org.springframework.shell.component.support;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp.Capability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.shell.component.context.BaseComponentContext;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.support.AbstractSelectorComponent.SelectorComponentContext;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import static org.jline.keymap.KeyMap.ctrl;
import static org.jline.keymap.KeyMap.del;
import static org.jline.keymap.KeyMap.key;

/**
 * Base component for selectors which provide selectable lists.
 *
 * @author Janne Valkealahti
 */
public abstract class AbstractSelectorComponent<T, C extends SelectorComponentContext<T, I, C>, I extends Nameable & Matchable & Enableable & Selectable & Itemable<T>>
		extends AbstractComponent<C> {

	private final static Logger log = LoggerFactory.getLogger(AbstractSelectorComponent.class);
	protected final String name;
	private final List<I> items;
	private Comparator<I> comparator = (o1, o2) -> 0;
	private boolean exitSelects;
	private int maxItems = 5;
	private Function<T, String> itemMapper = item -> item.toString();
	private boolean stale = false;
	private AtomicInteger start = new AtomicInteger(0);
	private AtomicInteger pos = new AtomicInteger(0);
	private I defaultExpose;
	private boolean expose = false;

	public AbstractSelectorComponent(Terminal terminal, String name, List<I> items, boolean exitSelects,
			Comparator<I> comparator) {
		super(terminal);
		this.name = name;
		this.items = items;
		this.exitSelects = exitSelects;
		if (comparator != null) {
			this.comparator = comparator;
		}
	}

	/**
	 * Set max items to show.
	 *
	 * @param maxItems max items
	 */
	public void setMaxItems(int maxItems) {
		Assert.state(maxItems > 0 || maxItems < 33, "maxItems has to be between 1 and 32");
		this.maxItems = maxItems;
	}

	/**
	 * Sets an item mapper.
	 *
	 * @param itemMapper the item mapper
	 */
	public void setItemMapper(Function<T, String> itemMapper) {
		Assert.notNull(itemMapper, "itemMapper cannot be null");
		this.itemMapper = itemMapper;
	}

	/**
	 * Gets an item mapper.
	 *
	 * @return
	 */
	public Function<T, String> getItemMapper() {
		return itemMapper;
	}

	/**
	 * Sets default expose item when component start.
	 *
	 * @param defaultExpose the default item
	 */
	public void setDefaultExpose(I defaultExpose) {
		this.defaultExpose = defaultExpose;
		if (defaultExpose != null) {
			expose = true;
		}
	}

	/**
	 * Gets items.
	 *
	 * @return a list of items
	 */
	protected List<I> getItems() {
		return items;
	}

	@Override
	protected void bindKeyMap(KeyMap<String> keyMap) {
		keyMap.bind(OPERATION_SELECT, " ");
		keyMap.bind(OPERATION_DOWN, ctrl('E'), key(getTerminal(), Capability.key_down));
		keyMap.bind(OPERATION_UP, ctrl('Y'), key(getTerminal(), Capability.key_up));
		keyMap.bind(OPERATION_EXIT, "\r");
		keyMap.bind(OPERATION_BACKSPACE, del(), key(getTerminal(), Capability.key_backspace));
		// skip 32 - SPACE, 127 - DEL
		for (char i = 33; i < KeyMap.KEYMAP_LENGTH - 1; i++) {
			keyMap.bind(OPERATION_CHAR, Character.toString(i));
		}
	}

	@Override
	protected C runInternal(C context) {
		C thisContext = getThisContext(context);
		initialExpose(thisContext);
		ItemStateViewProjection buildItemStateView = buildItemStateView(start.get(), thisContext);
		List<ItemState<I>> itemStateView = buildItemStateView.items;
		thisContext.setItemStateView(itemStateView);
		thisContext.setCursorRow(start.get() + pos.get());
		return thisContext;
	}

	@Override
	protected boolean read(BindingReader bindingReader, KeyMap<String> keyMap, C context) {

		if (stale) {
			start.set(0);
			pos.set(0);
			stale = false;
		}
		C thisContext = getThisContext(context);
		ItemStateViewProjection buildItemStateView = buildItemStateView(start.get(), thisContext);
		List<ItemState<I>> itemStateView = buildItemStateView.items;
		String operation = bindingReader.readBinding(keyMap);
		log.debug("Binding read result {}", operation);
		if (operation == null) {
			return true;
		}
		String input;
		switch (operation) {
			case OPERATION_SELECT:
				if (!exitSelects) {
					itemStateView.forEach(i -> {
						if (i.index == start.get() + pos.get() && i.enabled) {
							i.selected = !i.selected;
						}
					});
				}
				break;
			case OPERATION_DOWN:
				if (start.get() + pos.get() + 1 < itemStateView.size()) {
					pos.incrementAndGet();
				}
				else if (start.get() + pos.get() + 1 >= buildItemStateView.total) {
					start.set(0);
					pos.set(0);
				}
				else {
					start.incrementAndGet();
				}
				break;
			case OPERATION_UP:
				if (start.get() > 0 && pos.get() == 0) {
					start.decrementAndGet();
				}
				else if (start.get() + pos.get() >= itemStateView.size()) {
					pos.decrementAndGet();
				}
				else if (start.get() + pos.get() <= 0) {
					start.set(buildItemStateView.total - Math.min(maxItems, itemStateView.size()));
					pos.set(itemStateView.size() - 1);
				}
				else {
					pos.decrementAndGet();
				}
				break;
			case OPERATION_CHAR:
				String lastBinding = bindingReader.getLastBinding();
				input = thisContext.getInput();
				if (input == null) {
					input = lastBinding;
				}
				else {
					input = input + lastBinding;
				}
				thisContext.setInput(input);

				stale = true;
				break;
			case OPERATION_BACKSPACE:
				input = thisContext.getInput();
				if (StringUtils.hasLength(input)) {
					input = input.length() > 1 ? input.substring(0, input.length() - 1) : null;
				}
				thisContext.setInput(input);
				break;
			case OPERATION_EXIT:
				if (exitSelects) {
					if (itemStateView.size() == 0) {
						// filter shows nothing, prevent exit
						break;
					}
					itemStateView.forEach(i -> {
						if (i.index == start.get() + pos.get()) {
							i.selected = !i.selected;
						}
					});
				}
				List<I> values = thisContext.getItemStates().stream()
						.filter(i -> i.selected)
						.map(i -> i.item)
						.collect(Collectors.toList());
				thisContext.setResultItems(values);
				return true;
			default:
				break;
		}
		thisContext.setCursorRow(start.get() + pos.get());
		buildItemStateView = buildItemStateView(start.get(), thisContext);
		thisContext.setItemStateView(buildItemStateView.items);
		return false;
	}

	private void initialExpose(C context) {
		if (!expose) {
			return;
		}
		expose = false;
		List<ItemState<I>> itemStates = context.getItemStates();
		if (itemStates == null) {
			AtomicInteger index = new AtomicInteger(0);
			itemStates = context.getItems().stream()
					.sorted(comparator)
					.map(item -> ItemState.of(item, item.getName(), index.getAndIncrement(), item.isEnabled(), item.isSelected()))
					.collect(Collectors.toList());
		}
		for (int i = 0; i < itemStates.size(); i++) {
			if (ObjectUtils.nullSafeEquals(itemStates.get(i).getName(), defaultExpose.getName())) {
				if (i < maxItems) {
					this.pos.set(i);
				}
				else {
					this.pos.set(maxItems - 1);
					this.start.set(i - maxItems + 1);
				}
				break;
			}
		}
	}

	private ItemStateViewProjection buildItemStateView(int skip, SelectorComponentContext<T, I, ?> context) {
		List<ItemState<I>> itemStates = context.getItemStates();
		if (itemStates == null) {
			AtomicInteger index = new AtomicInteger(0);
			itemStates = context.getItems().stream()
					.sorted(comparator)
					.map(item -> ItemState.of(item, item.getName(), index.getAndIncrement(), item.isEnabled(), item.isSelected()))
					.collect(Collectors.toList());
			context.setItemStates(itemStates);
		}
		AtomicInteger reindex = new AtomicInteger(0);
		List<ItemState<I>> filtered = itemStates.stream()
			.filter(i -> {
				return i.matches(context.getInput());
			})
			.map(i -> {
				i.index = reindex.getAndIncrement();
				return i;
			})
			.collect(Collectors.toList());
		List<ItemState<I>> items = filtered.stream()
			.skip(skip)
			.limit(maxItems)
			.collect(Collectors.toList());
		return new ItemStateViewProjection(items, filtered.size());
	}

	private class ItemStateViewProjection {
		List<ItemState<I>> items;
		int total;
		ItemStateViewProjection(List<ItemState<I>> items, int total) {
			this.items = items;
			this.total = total;
		}
	}

	/**
	 * Context interface on a selector component sharing content.
	 */
	public interface SelectorComponentContext<T, I extends Nameable & Matchable & Itemable<T>, C extends SelectorComponentContext<T, I, C>>
			extends ComponentContext<C> {

		/**
		 * Gets a name.
		 *
		 * @return a name
		 */
		String getName();

		/**
		 * Sets a name
		 *
		 * @param name the name
		 */
		void setName(String name);

		/**
		 * Gets an input.
		 *
		 * @return an input
		 */
		String getInput();

		/**
		 * Sets an input.
		 *
		 * @param input the input
		 */
		void setInput(String input);

		/**
		 * Gets an item states
		 *
		 * @return an item states
		 */
		List<ItemState<I>> getItemStates();

		/**
		 * Sets an item states.
		 *
		 * @param itemStateView the input state
		 */
		void setItemStates(List<ItemState<I>> itemStateView);

		/**
		 * Gets an item state view.
		 *
		 * @return an item state view
		 */
		List<ItemState<I>> getItemStateView();

		/**
		 * Sets an item state view
		 *
		 * @param itemStateView the item state view
		 */
		void setItemStateView(List<ItemState<I>> itemStateView);

		/**
		 * Return if there is a result.
		 *
		 * @return true if context represents result
		 */
		boolean isResult();

		/**
		 * Gets a cursor row.
		 *
		 * @return a cursor row.
		 */
		Integer getCursorRow();

		/**
		 * Sets a cursor row.
		 *
		 * @param cursorRow the cursor row
		 */
		void setCursorRow(Integer cursorRow);

		/**
		 * Gets an items.
		 *
		 * @return an items
		 */
		List<I> getItems();

		/**
		 * Sets an items.
		 *
		 * @param items the items
		 */
		void setItems(List<I> items);

		/**
		 * Gets a result items.
		 *
		 * @return a result items
		 */
		List<I> getResultItems();

		/**
		 * Sets a result items.
		 *
		 * @param items the result items
		 */
		void setResultItems(List<I> items);

		/**
		 * Creates an empty {@link SelectorComponentContext}.
		 *
		 * @return empty context
		 */
		static <T, I extends Nameable & Matchable & Itemable<T>, C extends SelectorComponentContext<T, I, C>> SelectorComponentContext<T, I, C> empty() {
			return new BaseSelectorComponentContext<>();
		}
	}

	/**
	 * Base implementation of a {@link SelectorComponentContext}.
	 */
	protected static class BaseSelectorComponentContext<T, I extends Nameable & Matchable & Itemable<T>, C extends SelectorComponentContext<T, I, C>>
			extends BaseComponentContext<C> implements SelectorComponentContext<T, I, C> {

		private String name;
		private String input;
		private List<ItemState<I>> itemStates;
		private List<ItemState<I>> itemStateView;
		private Integer cursorRow;
		private List<I> items;
		private List<I> resultItems;

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String getInput() {
			return input;
		}

		@Override
		public void setInput(String input) {
			this.input = input;
		}

		@Override
		public List<ItemState<I>> getItemStates() {
			return itemStates;
		}

		@Override
		public void setItemStates(List<ItemState<I>> itemStates) {
			this.itemStates = itemStates;
		};

		@Override
		public List<ItemState<I>> getItemStateView() {
			return itemStateView;
		}

		@Override
		public void setItemStateView(List<ItemState<I>> itemStateView) {
			this.itemStateView = itemStateView;
		};

		@Override
		public boolean isResult() {
			return resultItems != null;
		}

		@Override
		public Integer getCursorRow() {
			return cursorRow;
		}

		@Override
		public java.util.Map<String,Object> toTemplateModel() {
			Map<String, Object> attributes = super.toTemplateModel();
			attributes.put("name", getName());
			attributes.put("input", getInput());
			attributes.put("itemStates", getItemStates());
			attributes.put("itemStateView", getItemStateView());
			attributes.put("isResult", isResult());
			attributes.put("cursorRow", getCursorRow());
			return attributes;
		};

		public void setCursorRow(Integer cursorRow) {
			this.cursorRow = cursorRow;
		};

		@Override
		public List<I> getItems() {
			return items;
		}

		@Override
		public void setItems(List<I> items) {
			this.items = items;
		}

		@Override
		public List<I> getResultItems() {
			return resultItems;
		}

		@Override
		public void setResultItems(List<I> resultItems) {
			this.resultItems = resultItems;
		}

		@Override
		public String toString() {
			return "DefaultSelectorComponentContext [cursorRow=" + cursorRow + "]";
		}
	}

	/**
	 * Class keeping item state.
	 */
	public static class ItemState<I extends Matchable> implements Matchable {
		I item;
		String name;
		boolean selected;
		boolean enabled;
		int index;

		ItemState(I item, String name, int index, boolean enabled, boolean selected) {
			this.item = item;
			this.name = name;
			this.index = index;
			this.enabled = enabled;
			this.selected = selected;
		}

		public boolean matches(String match) {
			return item.matches(match);
		};

		public int getIndex() {
			return index;
		}

		public String getName() {
			return name;
		}

		public boolean isSelected() {
			return selected;
		}

		public boolean isEnabled() {
			return enabled;
		}

		static <I extends Matchable> ItemState<I> of(I item, String name, int index, boolean enabled, boolean selected) {
			return new ItemState<I>(item, name, index, enabled, selected);
		}
	}

}
