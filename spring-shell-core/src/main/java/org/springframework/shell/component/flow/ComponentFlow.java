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

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.component.ConfirmationInput;
import org.springframework.shell.component.ConfirmationInput.ConfirmationInputContext;
import org.springframework.shell.component.MultiItemSelector;
import org.springframework.shell.component.MultiItemSelector.MultiItemSelectorContext;
import org.springframework.shell.component.NumberInput;
import org.springframework.shell.component.NumberInput.NumberInputContext;
import org.springframework.shell.component.PathInput;
import org.springframework.shell.component.PathInput.PathInputContext;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.SingleItemSelector.SingleItemSelectorContext;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.component.StringInput.StringInputContext;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.style.TemplateExecutor;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Wizart providing an implementation which allows more polished way to ask various inputs
 * from a user using shell style components for simple text/path input, single select and
 * multi-select.
 *
 * @author Janne Valkealahti
 */
public interface ComponentFlow {

	/**
	 * Run a wizard and returns a result from it.
	 *
	 * @return the input wizard result
	 */
	ComponentFlowResult run();

	/**
	 * Gets a new instance of an input wizard builder.
	 *
	 * @return the input wizard builder
	 */
	public static Builder builder() {
		return new DefaultBuilder();
	}

	/**
	 * Results from a flow run.
	 */
	interface ComponentFlowResult {

		/**
		 * Gets a context.
		 *
		 * @return a context
		 */
		ComponentContext<?> getContext();
	}

	/**
	 * Interface for a wizard builder.
	 */
	interface Builder {

		/**
		 * Gets a builder for string input.
		 *
		 * @param id the identifier
		 * @return builder for string input
		 */
		StringInputSpec withStringInput(String id);

		/**
		 * Gets a builder for number input.
		 *
		 * @param id the identifier
		 * @return builder for number input
		 */
		NumberInputSpec withNumberInput(String id);

		/**
		 * Gets a builder for path input.
		 *
		 * @param id the identifier
		 * @return builder for text input
		 */
		PathInputSpec withPathInput(String id);

		/**
		 * Gets a builder for confirmation input.
		 *
		 * @param id the identifier
		 * @return builder for text input
		 */
		ConfirmationInputSpec withConfirmationInput(String id);

		/**
		 * Gets a builder for single item selector.
		 *
		 * @param id the identifier
		 * @return builder for single item selector
		 */
		SingleItemSelectorSpec withSingleItemSelector(String id);

		/**
		 * Gets a builder for multi item selector.
		 *
		 * @param id the identifier
		 * @return builder for multi item selector
		 */
		MultiItemSelectorSpec withMultiItemSelector(String id);

		/**
		 * Sets a {@link Terminal}.
		 *
		 * @param terminal the terminal
		 * @return a builder
		 */
		Builder terminal(Terminal terminal);

		/**
		 * Sets a {@link ResourceLoader}.
		 *
		 * @param resourceLoader the resource loader
		 * @return a builder
		 */
		Builder resourceLoader(ResourceLoader resourceLoader);

		/**
		 * Sets a {@link TemplateExecutor}.
		 *
		 * @param templateExecutor the template executor
		 * @return a builder
		 */
		Builder templateExecutor(TemplateExecutor templateExecutor);

		/**
		 * Clones existing builder.
		 *
		 * @return a builder
		 */
		Builder clone();

		/**
		 * Resets existing builder.
		 *
		 * @return a builder
		 */
		Builder reset();

		/**
		 * Builds instance of input wizard.
		 *
		 * @return instance of input wizard
		 */
		ComponentFlow build();
	}

	static abstract class BaseBuilder implements Builder {

		private final List<BaseStringInput> stringInputs = new ArrayList<>();
		private final List<BaseNumberInput> numberInputs = new ArrayList<>();
		private final List<BasePathInput> pathInputs = new ArrayList<>();
		private final List<BaseConfirmationInput> confirmationInputs = new ArrayList<>();
		private final List<BaseSingleItemSelector> singleItemSelectors = new ArrayList<>();
		private final List<BaseMultiItemSelector> multiItemSelectors = new ArrayList<>();
		private final AtomicInteger order = new AtomicInteger();
		private final HashSet<String> uniqueIds = new HashSet<>();
		private Terminal terminal;
		private ResourceLoader resourceLoader;
		private TemplateExecutor templateExecutor;

		BaseBuilder() {
		}

		@Override
		public ComponentFlow build() {
			return new DefaultComponentFlow(terminal, resourceLoader, templateExecutor, stringInputs, numberInputs, pathInputs,
					confirmationInputs, singleItemSelectors, multiItemSelectors);
		}

		@Override
		public StringInputSpec withStringInput(String id) {
			return new DefaultStringInputSpec(this, id);
		}

		@Override
		public NumberInputSpec withNumberInput(String id) {
			return new DefaultNumberInputSpec(this, id);
		}

		@Override
		public PathInputSpec withPathInput(String id) {
			return new DefaultPathInputSpec(this, id);
		}

		@Override
		public ConfirmationInputSpec withConfirmationInput(String id) {
			return new DefaultConfirmationInputSpec(this, id);
		}

		@Override
		public SingleItemSelectorSpec withSingleItemSelector(String id) {
			return new DefaultSingleInputSpec(this, id);
		}

		@Override
		public MultiItemSelectorSpec withMultiItemSelector(String id) {
			return new DefaultMultiInputSpec(this, id);
		}

		@Override
		public Builder terminal(Terminal terminal) {
			this.terminal = terminal;
			return this;
		}

		@Override
		public Builder resourceLoader(ResourceLoader resourceLoader) {
			this.resourceLoader = resourceLoader;
			return this;
		}

		@Override
		public Builder templateExecutor(TemplateExecutor templateExecutor) {
			this.templateExecutor = templateExecutor;
			return this;
		}

		@Override
		public Builder clone() {
			return new DefaultBuilder(this);
		}

		@Override
		public Builder reset() {
			stringInputs.clear();
			numberInputs.clear();
			pathInputs.clear();
			confirmationInputs.clear();
			singleItemSelectors.clear();
			multiItemSelectors.clear();
			order.set(0);
			uniqueIds.clear();
			return this;
		}

		void addStringInput(BaseStringInput input) {
			checkUniqueId(input.getId());
			input.setOrder(order.getAndIncrement());
			stringInputs.add(input);
		}

		void addNumberInput(BaseNumberInput input) {
			checkUniqueId(input.getId());
			input.setOrder(order.getAndIncrement());
			numberInputs.add(input);
		}

		void addPathInput(BasePathInput input) {
			checkUniqueId(input.getId());
			input.setOrder(order.getAndIncrement());
			pathInputs.add(input);
		}

		void addConfirmationInput(BaseConfirmationInput input) {
			checkUniqueId(input.getId());
			input.setOrder(order.getAndIncrement());
			confirmationInputs.add(input);
		}

		void addSingleItemSelector(BaseSingleItemSelector input) {
			checkUniqueId(input.getId());
			input.setOrder(order.getAndIncrement());
			singleItemSelectors.add(input);
		}

		void addMultiItemSelector(BaseMultiItemSelector input) {
			checkUniqueId(input.getId());
			input.setOrder(order.getAndIncrement());
			multiItemSelectors.add(input);
		}

		Terminal getTerminal() {
			return terminal;
		}

		ResourceLoader getResourceLoader() {
			return resourceLoader;
		}

		TemplateExecutor getTemplateExecutor() {
			return templateExecutor;
		}

		private void checkUniqueId(String id) {
			if (uniqueIds.contains(id)) {
				throw new IllegalArgumentException(String.format("Component with id %s is already registered", id));
			}
			uniqueIds.add(id);
		}
	}

	static class DefaultBuilder extends BaseBuilder {

		DefaultBuilder() {
			super();
		}

		DefaultBuilder(BaseBuilder other) {
			terminal(other.getTerminal());
			resourceLoader(other.getResourceLoader());
			templateExecutor(other.getTemplateExecutor());
		}
	}

	static class DefaultComponentFlowResult implements ComponentFlowResult {

		private ComponentContext<?> context;

		DefaultComponentFlowResult(ComponentContext<?> context) {
			this.context = context;
		}

		public ComponentContext<?> getContext() {
			return context;
		}
	}

	static class DefaultComponentFlow implements ComponentFlow {

		private static final Logger log = LoggerFactory.getLogger(DefaultComponentFlow.class);
		private final Terminal terminal;
		private final List<BaseStringInput> stringInputs;
		private final List<BaseNumberInput> numberInputs;
		private final List<BasePathInput> pathInputs;
		private final List<BaseConfirmationInput> confirmationInputs;
		private final List<BaseSingleItemSelector> singleInputs;
		private final List<BaseMultiItemSelector> multiInputs;
		private final ResourceLoader resourceLoader;
		private final TemplateExecutor templateExecutor;

		DefaultComponentFlow(Terminal terminal, ResourceLoader resourceLoader, TemplateExecutor templateExecutor,
				List<BaseStringInput> stringInputs, List<BaseNumberInput> numberInputs, List<BasePathInput> pathInputs,
				List<BaseConfirmationInput> confirmationInputs, List<BaseSingleItemSelector> singleInputs,
				List<BaseMultiItemSelector> multiInputs) {
			this.terminal = terminal;
			this.resourceLoader = resourceLoader;
			this.templateExecutor = templateExecutor;
			this.stringInputs = stringInputs;
			this.numberInputs = numberInputs;
			this.pathInputs = pathInputs;
			this.confirmationInputs = confirmationInputs;
			this.singleInputs = singleInputs;
			this.multiInputs = multiInputs;
		}

		@Override
		public ComponentFlowResult run() {
			return runGetResults();
		}

		private static class OrderedInputOperationList {

			private final Map<String, Node> map = new HashMap<>();
			private Node first;

			OrderedInputOperationList(List<OrderedInputOperation> values) {
				Node ref = null;
				for (OrderedInputOperation oio : values) {
					Node node = new Node(oio);
					map.put(oio.id, node);
					if (ref != null) {
						ref.next = node;
					}
					ref = node;
					if (first == null) {
						first = node;
					}
				}
			}

			Node get(String id) {
				return map.get(id);
			}

			Node getFirst() {
				return first;
			}

			static class Node {
				OrderedInputOperation data;
				Node next;
				Node(OrderedInputOperation data) {
					this.data = data;
				}
			}
		}

		private DefaultComponentFlowResult runGetResults() {
			List<OrderedInputOperation> oios = Stream
				.of(stringInputsStream(), numberInputsStream(), pathInputsStream(), confirmationInputsStream(),
						singleItemSelectorsStream(), multiItemSelectorsStream())
				.flatMap(oio -> oio)
				.sorted(OrderComparator.INSTANCE)
				.collect(Collectors.toList());
			OrderedInputOperationList oiol = new OrderedInputOperationList(oios);
			ComponentContext<?> context = ComponentContext.empty();

			OrderedInputOperationList.Node node = oiol.getFirst();
			while (node != null) {
				log.debug("Calling apply for {}", node.data.id);
				context = node.data.getOperation().apply(context);
				if (node.data.next != null) {
					Optional<String> n = node.data.next.apply(context);
					if (n == null) {
						// actual function returned null optional which is case
						// when no next function was set. this is different
						// than when null is returned for next.
						node = node.next;
					}
					else if (n.isPresent()) {
						// user returned value
						node = oiol.get(n.get());
					}
					else {
						// user returned null
						node = null;
					}
				}
				else {
					node = node.next;
				}
			}
			return new DefaultComponentFlowResult(context);
		}

		private Stream<OrderedInputOperation> stringInputsStream() {
			return stringInputs.stream().map(input -> {
				StringInput selector = new StringInput(terminal, input.getName(), input.getDefaultValue());
				Function<ComponentContext<?>, ComponentContext<?>> operation = (context) -> {
						if (input.getResultMode() == ResultMode.ACCEPT && input.isStoreResult()
								&& StringUtils.hasText(input.getResultValue())) {
							context.put(input.getId(), input.getResultValue());
							return context;
						}
						selector.setResourceLoader(resourceLoader);
						selector.setTemplateExecutor(templateExecutor);
						selector.setMaskCharacter(input.getMaskCharacter());
						if (StringUtils.hasText(input.getTemplateLocation())) {
							selector.setTemplateLocation(input.getTemplateLocation());
						}
						if (input.getRenderer() != null) {
							selector.setRenderer(input.getRenderer());
						}
						if (input.isStoreResult()) {
							if (input.getResultMode() == ResultMode.VERIFY && StringUtils.hasText(input.getResultValue())) {
								selector.addPreRunHandler(c -> {
									c.setDefaultValue(input.getResultValue());
								});
							}
							selector.addPostRunHandler(c -> {
								c.put(input.getId(), c.getResultValue());
							});
						}
						for (Consumer<StringInputContext> handler : input.getPreHandlers()) {
							selector.addPreRunHandler(handler);
						}
						for (Consumer<StringInputContext> handler : input.getPostHandlers()) {
							selector.addPostRunHandler(handler);
						}
						return selector.run(context);
				};
				Function<StringInputContext, String> f1 = input.getNext();
				Function<ComponentContext<?>, Optional<String>> f2 = context -> f1 != null
						? Optional.ofNullable(f1.apply(selector.getThisContext(context)))
						: null;
				return OrderedInputOperation.of(input.getId(), input.getOrder(), operation, f2);
			});
		}

		private Stream<OrderedInputOperation> numberInputsStream() {
			return numberInputs.stream().map(input -> {
				NumberInput selector = new NumberInput(terminal, input.getName(), input.getDefaultValue(), input.getNumberClass(), input.isRequired());
				UnaryOperator<ComponentContext<?>> operation = context -> {
					if (input.getResultMode() == ResultMode.ACCEPT && input.isStoreResult()
							&& input.getResultValue() != null) {
						context.put(input.getId(), input.getResultValue());
						return context;
					}
					selector.setResourceLoader(resourceLoader);
					selector.setTemplateExecutor(templateExecutor);
					selector.setNumberClass(input.getNumberClass());
					if (StringUtils.hasText(input.getTemplateLocation())) {
						selector.setTemplateLocation(input.getTemplateLocation());
					}
					if (input.getRenderer() != null) {
						selector.setRenderer(input.getRenderer());
					}
					if (input.isStoreResult()) {
						if (input.getResultMode() == ResultMode.VERIFY && input.getResultValue() != null) {
							selector.addPreRunHandler(c -> {
								c.setDefaultValue(input.getResultValue());
								c.setRequired(input.isRequired());
							});
						}
						selector.addPostRunHandler(c -> c.put(input.getId(), c.getResultValue()));
					}
					for (Consumer<NumberInputContext> handler : input.getPreHandlers()) {
						selector.addPreRunHandler(handler);
					}
					for (Consumer<NumberInputContext> handler : input.getPostHandlers()) {
						selector.addPostRunHandler(handler);
					}
					return selector.run(context);
				};
				Function<NumberInputContext, String> f1 = input.getNext();
				Function<ComponentContext<?>, Optional<String>> f2 = context -> f1 != null
						? Optional.ofNullable(f1.apply(selector.getThisContext(context)))
						: null;
				return OrderedInputOperation.of(input.getId(), input.getOrder(), operation, f2);
			});
		}

		private Stream<OrderedInputOperation> pathInputsStream() {
			return pathInputs.stream().map(input -> {
				PathInput selector = new PathInput(terminal, input.getName());
				Function<ComponentContext<?>, ComponentContext<?>> operation = (context) -> {
						if (input.getResultMode() == ResultMode.ACCEPT && input.isStoreResult()
								&& StringUtils.hasText(input.getResultValue())) {
							context.put(input.getId(), Paths.get(input.getResultValue()));
							return context;
						}
						selector.setResourceLoader(resourceLoader);
						selector.setTemplateExecutor(templateExecutor);
						if (StringUtils.hasText(input.getTemplateLocation())) {
							selector.setTemplateLocation(input.getTemplateLocation());
						}
						if (input.getRenderer() != null) {
							selector.setRenderer(input.getRenderer());
						}
						if (input.isStoreResult()) {
							selector.addPostRunHandler(c -> {
								c.put(input.getId(), c.getResultValue());
							});
						}
						for (Consumer<PathInputContext> handler : input.getPreHandlers()) {
							selector.addPreRunHandler(handler);
						}
						for (Consumer<PathInputContext> handler : input.getPostHandlers()) {
							selector.addPostRunHandler(handler);
						}
						return selector.run(context);
				};
				Function<PathInputContext, String> f1 = input.getNext();
				Function<ComponentContext<?>, Optional<String>> f2 = context -> f1 != null
						? Optional.ofNullable(f1.apply(selector.getThisContext(context)))
						: null;
				return OrderedInputOperation.of(input.getId(), input.getOrder(), operation, f2);
			});
		}

		private Stream<OrderedInputOperation> confirmationInputsStream() {
			return confirmationInputs.stream().map(input -> {
				ConfirmationInput selector = new ConfirmationInput(terminal, input.getName(), input.getDefaultValue());
				Function<ComponentContext<?>, ComponentContext<?>> operation = (context) -> {
						if (input.getResultMode() == ResultMode.ACCEPT && input.isStoreResult()
								&& input.getResultValue() != null) {
							context.put(input.getId(), input.getResultValue());
							return context;
						}
						selector.setResourceLoader(resourceLoader);
						selector.setTemplateExecutor(templateExecutor);
						if (StringUtils.hasText(input.getTemplateLocation())) {
							selector.setTemplateLocation(input.getTemplateLocation());
						}
						if (input.getRenderer() != null) {
							selector.setRenderer(input.getRenderer());
						}
						if (input.isStoreResult()) {
							selector.addPostRunHandler(c -> {
								c.put(input.getId(), c.getResultValue());
							});
						}
						for (Consumer<ConfirmationInputContext> handler : input.getPreHandlers()) {
							selector.addPreRunHandler(handler);
						}
						for (Consumer<ConfirmationInputContext> handler : input.getPostHandlers()) {
							selector.addPostRunHandler(handler);
						}
						return selector.run(context);
				};
				Function<ConfirmationInputContext, String> f1 = input.getNext();
				Function<ComponentContext<?>, Optional<String>> f2 = context -> f1 != null
						? Optional.ofNullable(f1.apply(selector.getThisContext(context)))
						: null;
				return OrderedInputOperation.of(input.getId(), input.getOrder(), operation, f2);
			});
		}

		private Stream<OrderedInputOperation> singleItemSelectorsStream() {
			return singleInputs.stream().map(input -> {
				List<SelectorItem<String>> selectorItems = input.getSelectItems().entrySet().stream()
					.map(e -> SelectorItem.of(e.getKey(), e.getValue()))
					.collect(Collectors.toList());

				// setup possible item for initial expose
				String defaultSelect = input.getDefaultSelect();
				Stream<SelectorItem<String>> defaultCheckStream = StringUtils.hasText(defaultSelect)
						? selectorItems.stream()
						: Stream.empty();
				SelectorItem<String> defaultExpose = defaultCheckStream
					.filter(si -> ObjectUtils.nullSafeEquals(defaultSelect, si.getName()))
					.findFirst()
					.orElse(null);

				SingleItemSelector<String, SelectorItem<String>> selector = new SingleItemSelector<>(terminal,
						selectorItems, input.getName(), input.getComparator());
				selector.setDefaultExpose(defaultExpose);
				Function<ComponentContext<?>, ComponentContext<?>> operation = (context) -> {
					if (input.getResultMode() == ResultMode.ACCEPT && input.isStoreResult()
							&& StringUtils.hasText(input.getResultValue())) {
						context.put(input.getId(), input.getResultValue());
						return context;
					}
					selector.setResourceLoader(resourceLoader);
					selector.setTemplateExecutor(templateExecutor);
					if (StringUtils.hasText(input.getTemplateLocation())) {
						selector.setTemplateLocation(input.getTemplateLocation());
					}
					if (input.getRenderer() != null) {
						selector.setRenderer(input.getRenderer());
					}
					if (input.getMaxItems() != null) {
						selector.setMaxItems(input.getMaxItems());
					}
					if (input.isStoreResult()) {
						selector.addPostRunHandler(c -> {
							c.getValue().ifPresent(v -> {
								c.put(input.getId(), v);
							});
						});
					}
					for (Consumer<SingleItemSelectorContext<String, SelectorItem<String>>> handler : input.getPreHandlers()) {
						selector.addPreRunHandler(handler);
					}
					for (Consumer<SingleItemSelectorContext<String, SelectorItem<String>>> handler : input.getPostHandlers()) {
						selector.addPostRunHandler(handler);
					}
					return selector.run(context);
				};
				Function<SingleItemSelectorContext<String, SelectorItem<String>>, String> f1 = input.getNext();
				Function<ComponentContext<?>, Optional<String>> f2 = context -> f1 != null
						? Optional.ofNullable(f1.apply(selector.getThisContext(context)))
						: null;
				return OrderedInputOperation.of(input.getId(), input.getOrder(), operation, f2);
			});
		}

		private Stream<OrderedInputOperation> multiItemSelectorsStream() {
			return multiInputs.stream().map(input -> {
				List<SelectorItem<String>> selectorItems = input.getSelectItems().stream()
						.map(si -> SelectorItem.of(si.name(), si.item(), si.enabled(), si.selected()))
						.collect(Collectors.toList());
				MultiItemSelector<String, SelectorItem<String>> selector = new MultiItemSelector<>(terminal,
						selectorItems, input.getName(), input.getComparator());
				Function<ComponentContext<?>, ComponentContext<?>> operation = (context) -> {
					if (input.getResultMode() == ResultMode.ACCEPT && input.isStoreResult()
							&& !ObjectUtils.isEmpty(input.getResultValues())) {
						context.put(input.getId(), input.getResultValues());
						return context;
					}
					selector.setResourceLoader(resourceLoader);
					selector.setTemplateExecutor(templateExecutor);
					if (StringUtils.hasText(input.getTemplateLocation())) {
						selector.setTemplateLocation(input.getTemplateLocation());
					}
					if (input.getRenderer() != null) {
						selector.setRenderer(input.getRenderer());
					}
					if (input.getMaxItems() != null) {
						selector.setMaxItems(input.getMaxItems());
					}
					if (input.isStoreResult()) {
						selector.addPostRunHandler(c -> {
							c.put(input.getId(), c.getValues());
						});
					}
					for (Consumer<MultiItemSelectorContext<String, SelectorItem<String>>> handler : input.getPreHandlers()) {
						selector.addPreRunHandler(handler);
					}
					for (Consumer<MultiItemSelectorContext<String, SelectorItem<String>>> handler : input.getPostHandlers()) {
						selector.addPostRunHandler(handler);
					}
					return selector.run(context);
				};
				Function<MultiItemSelectorContext<String, SelectorItem<String>>, String> f1 = input.getNext();
				Function<ComponentContext<?>, Optional<String>> f2 = context -> f1 != null
						? Optional.ofNullable(f1.apply(selector.getThisContext(context)))
						: null;
				return OrderedInputOperation.of(input.getId(), input.getOrder(), operation, f2);
			});
		}
	}

	static class OrderedInputOperation implements Ordered {

		private String id;
		private int order;
		private Function<ComponentContext<?>, ComponentContext<?>> operation;
		private Function<ComponentContext<?>, Optional<String>> next;

		@Override
		public int getOrder() {
			return order;
		}

		public String getId() {
			return id;
		}

		public Function<ComponentContext<?>, ComponentContext<?>> getOperation() {
			return operation;
		}

		public Function<ComponentContext<?>, Optional<String>> getNext() {
			return next;
		}

		static OrderedInputOperation of(String id, int order,
				Function<ComponentContext<?>, ComponentContext<?>> operation,
				Function<ComponentContext<?>, Optional<String>> next) {
			OrderedInputOperation oio = new OrderedInputOperation();
			oio.id = id;
			oio.order = order;
			oio.operation = operation;
			oio.next = next;
			return oio;
		}
	}
}
