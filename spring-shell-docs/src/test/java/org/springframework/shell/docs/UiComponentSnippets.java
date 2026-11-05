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
package org.springframework.shell.docs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;

import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.shell.jline.tui.component.ConfirmationInput;
import org.springframework.shell.jline.tui.component.MultiItemSelector;
import org.springframework.shell.jline.tui.component.NumberInput;
import org.springframework.shell.jline.tui.component.NumberInput.NumberInputContext;
import org.springframework.shell.jline.tui.component.PathInput;
import org.springframework.shell.jline.tui.component.PathSearch;
import org.springframework.shell.jline.tui.component.SingleItemSelector;
import org.springframework.shell.jline.tui.component.StringInput;
import org.springframework.shell.jline.tui.component.ConfirmationInput.ConfirmationInputContext;
import org.springframework.shell.jline.tui.component.MultiItemSelector.MultiItemSelectorContext;
import org.springframework.shell.jline.tui.component.PathInput.PathInputContext;
import org.springframework.shell.jline.tui.component.PathSearch.PathSearchConfig;
import org.springframework.shell.jline.tui.component.PathSearch.PathSearchContext;
import org.springframework.shell.jline.tui.component.SingleItemSelector.SingleItemSelectorContext;
import org.springframework.shell.jline.tui.component.StringInput.StringInputContext;
import org.springframework.shell.jline.tui.component.support.SelectorItem;
import org.springframework.shell.jline.tui.style.TemplateExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

public class UiComponentSnippets {

	// tag::snippet1[]
	class StringInputCustomRenderer implements Function<StringInputContext, List<AttributedString>> {

		@Override
		public List<AttributedString> apply(StringInputContext context) {
			AttributedStringBuilder builder = new AttributedStringBuilder();
			builder.append(context.getName());
			builder.append(" ");
			if (context.getMaskedResultValue() != null) {
				builder.append(context.getMaskedResultValue());
			}
			else {
				String input = context.getMaskedInput();
				if (StringUtils.hasText(input)) {
					builder.append(input);
				}
				else {
					builder.append("[Enter a value]");
				}
			}
			return Arrays.asList(builder.toAttributedString());
		}

	}
	// end::snippet1[]

	class Dump1 {

		// tag::snippet2[]
		@Component
		public class ComponentCommands {

			private final Terminal terminal;

			private final ResourceLoader resourceLoader;

			private final TemplateExecutor templateExecutor;

			public ComponentCommands(Terminal terminal, ResourceLoader resourceLoader,
					TemplateExecutor templateExecutor) {
				this.terminal = terminal;
				this.resourceLoader = resourceLoader;
				this.templateExecutor = templateExecutor;
			}

			@Command(name = "component stringcustom", description = "String input", group = "Components")
			public String stringInputCustom(@Option(defaultValue = "false") boolean mask) {
				StringInput component = new StringInput(terminal, "Enter value", "myvalue",
						new StringInputCustomRenderer());
				component.setResourceLoader(resourceLoader);
				component.setTemplateExecutor(templateExecutor);
				if (mask) {
					component.setMaskCharacter('*');
				}
				StringInputContext context = component.run(StringInputContext.empty());
				return "Got value " + context.getMaskedResultValue();
			}

		}
		// end::snippet2[]

	}

	class Dump2 {

		// tag::snippet3[]
		@Component
		public class ComponentCommands {

			private final Terminal terminal;

			private final ResourceLoader resourceLoader;

			private final TemplateExecutor templateExecutor;

			public ComponentCommands(Terminal terminal, ResourceLoader resourceLoader,
					TemplateExecutor templateExecutor) {
				this.terminal = terminal;
				this.resourceLoader = resourceLoader;
				this.templateExecutor = templateExecutor;
			}

			@Command(name = "component string", description = "String input", group = "Components")
			public String stringInput(@Option(defaultValue = "false") boolean mask) {
				StringInput component = new StringInput(terminal, "Enter value", "myvalue");
				component.setResourceLoader(resourceLoader);
				component.setTemplateExecutor(templateExecutor);
				if (mask) {
					component.setMaskCharacter('*');
				}
				StringInputContext context = component.run(StringInputContext.empty());
				return "Got value " + context.getMaskedResultValue();
			}

		}
		// end::snippet3[]

	}

	class Dump3 {

		// tag::snippet4[]
		@Component
		public class ComponentCommands {

			private final Terminal terminal;

			private final ResourceLoader resourceLoader;

			private final TemplateExecutor templateExecutor;

			public ComponentCommands(Terminal terminal, ResourceLoader resourceLoader,
					TemplateExecutor templateExecutor) {
				this.terminal = terminal;
				this.resourceLoader = resourceLoader;
				this.templateExecutor = templateExecutor;
			}

			@Command(name = "component path input", description = "Path input", group = "Components")
			public String pathInput() {
				PathInput component = new PathInput(terminal, "Enter value");
				component.setResourceLoader(resourceLoader);
				component.setTemplateExecutor(templateExecutor);
				PathInputContext context = component.run(PathInputContext.empty());
				return "Got value " + context.getResultValue();
			}

		}
		// end::snippet4[]

	}

	class Dump4 {

		// tag::snippet5[]
		@Component
		public class ComponentCommands {

			private final Terminal terminal;

			private final ResourceLoader resourceLoader;

			private final TemplateExecutor templateExecutor;

			public ComponentCommands(Terminal terminal, ResourceLoader resourceLoader,
					TemplateExecutor templateExecutor) {
				this.terminal = terminal;
				this.resourceLoader = resourceLoader;
				this.templateExecutor = templateExecutor;
			}

			@Command(name = "component confirmation", description = "Confirmation input", group = "Components")
			public String confirmationInput(@Option(defaultValue = "false") boolean no) {
				ConfirmationInput component = new ConfirmationInput(terminal, "Enter value", !no);
				component.setResourceLoader(resourceLoader);
				component.setTemplateExecutor(templateExecutor);
				ConfirmationInputContext context = component.run(ConfirmationInputContext.empty());
				return "Got value " + context.getResultValue();
			}

		}
		// end::snippet5[]

	}

	class Dump5 {

		// tag::snippet6[]
		@Component
		public class ComponentCommands {

			private final Terminal terminal;

			private final ResourceLoader resourceLoader;

			private final TemplateExecutor templateExecutor;

			public ComponentCommands(Terminal terminal, ResourceLoader resourceLoader,
					TemplateExecutor templateExecutor) {
				this.terminal = terminal;
				this.resourceLoader = resourceLoader;
				this.templateExecutor = templateExecutor;
			}

			@Command(name = "component single", description = "Single selector", group = "Components")
			public String singleSelector() {
				SelectorItem<String> i1 = SelectorItem.of("key1", "value1");
				SelectorItem<String> i2 = SelectorItem.of("key2", "value2");
				List<SelectorItem<String>> items = Arrays.asList(i1, i2);
				SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(terminal, items,
						"testSimple", null);
				component.setResourceLoader(resourceLoader);
				component.setTemplateExecutor(templateExecutor);
				SingleItemSelectorContext<String, SelectorItem<String>> context = component
					.run(SingleItemSelectorContext.empty());
				String result = context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get();
				return "Got value " + result;
			}

		}
		// end::snippet6[]

	}

	class Dump6 {

		// tag::snippet7[]
		@Component
		public class ComponentCommands {

			private final Terminal terminal;

			private final ResourceLoader resourceLoader;

			private final TemplateExecutor templateExecutor;

			public ComponentCommands(Terminal terminal, ResourceLoader resourceLoader,
					TemplateExecutor templateExecutor) {
				this.terminal = terminal;
				this.resourceLoader = resourceLoader;
				this.templateExecutor = templateExecutor;
			}

			@Command(name = "component multi", description = "Multi selector", group = "Components")
			public String multiSelector() {
				List<SelectorItem<String>> items = new ArrayList<>();
				items.add(SelectorItem.of("key1", "value1"));
				items.add(SelectorItem.of("key2", "value2", false, true));
				items.add(SelectorItem.of("key3", "value3"));
				MultiItemSelector<String, SelectorItem<String>> component = new MultiItemSelector<>(terminal, items,
						"testSimple", null);
				component.setResourceLoader(resourceLoader);
				component.setTemplateExecutor(templateExecutor);
				MultiItemSelectorContext<String, SelectorItem<String>> context = component
					.run(MultiItemSelectorContext.empty());
				String result = context.getResultItems()
					.stream()
					.map(si -> si.getItem())
					.collect(Collectors.joining(","));
				return "Got value " + result;
			}

		}
		// end::snippet7[]

	}

	class Dump7 {

		@Component
		public class ComponentCommands {

			private final Terminal terminal;

			private final ResourceLoader resourceLoader;

			private final TemplateExecutor templateExecutor;

			public ComponentCommands(Terminal terminal, ResourceLoader resourceLoader,
					TemplateExecutor templateExecutor) {
				this.terminal = terminal;
				this.resourceLoader = resourceLoader;
				this.templateExecutor = templateExecutor;
			}

			@Command(name = "component single", description = "Single selector", group = "Components")
			public String singleSelector() {
				// tag::snippet8[]
				SelectorItem<String> i1 = SelectorItem.of("key1", "value1");
				SelectorItem<String> i2 = SelectorItem.of("key2", "value2");
				List<SelectorItem<String>> items = Arrays.asList(i1, i2);
				SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(terminal, items,
						"testSimple", null);
				component.setDefaultExpose(i2);
				// end::snippet8[]

				component.setResourceLoader(resourceLoader);
				component.setTemplateExecutor(templateExecutor);
				SingleItemSelectorContext<String, SelectorItem<String>> context = component
					.run(SingleItemSelectorContext.empty());
				String result = context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get();
				return "Got value " + result;
			}

		}

	}

	class Dump8 {

		// tag::snippet9[]
		@Component
		public class ComponentCommands {

			private final Terminal terminal;

			private final ResourceLoader resourceLoader;

			private final TemplateExecutor templateExecutor;

			public ComponentCommands(Terminal terminal, ResourceLoader resourceLoader,
					TemplateExecutor templateExecutor) {
				this.terminal = terminal;
				this.resourceLoader = resourceLoader;
				this.templateExecutor = templateExecutor;
			}

			@Command(name = "component path search", description = "Path search", group = "Components")
			public String pathSearch() {
				PathSearchConfig config = new PathSearch.PathSearchConfig();
				config.setMaxPathsShow(5);
				config.setMaxPathsSearch(100);
				config.setSearchForward(true);
				config.setSearchCaseSensitive(false);
				config.setSearchNormalize(false);

				PathSearch component = new PathSearch(terminal, "Enter value", config);
				component.setResourceLoader(resourceLoader);
				component.setTemplateExecutor(templateExecutor);

				PathSearchContext context = component.run(PathSearchContext.empty());
				return "Got value " + context.getResultValue();
			}

		}

		// end::snippet9[]

	}

	class Dump9 {

		// tag::snippet10[]
		@Component
		public class ComponentCommands {

			private final Terminal terminal;

			private final ResourceLoader resourceLoader;

			private final TemplateExecutor templateExecutor;

			public ComponentCommands(Terminal terminal, ResourceLoader resourceLoader,
					TemplateExecutor templateExecutor) {
				this.terminal = terminal;
				this.resourceLoader = resourceLoader;
				this.templateExecutor = templateExecutor;
			}

			@Command(name = "component number", description = "Number input", group = "Components")
			public String numberInput() {
				NumberInput component = new NumberInput(terminal, "Enter value", 99.9, Double.class);
				component.setResourceLoader(resourceLoader);
				component.setTemplateExecutor(templateExecutor);

				NumberInputContext context = component.run(NumberInputContext.empty());
				return "Got value " + context.getResultValue();
			}

		}

		// end::snippet10[]

	}

}
