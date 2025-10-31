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
package org.springframework.shell.samples.standard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;

import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.shell.standard.AbstractCommand;
import org.springframework.shell.tui.component.ConfirmationInput;
import org.springframework.shell.tui.component.ConfirmationInput.ConfirmationInputContext;
import org.springframework.shell.tui.component.MultiItemSelector;
import org.springframework.shell.tui.component.MultiItemSelector.MultiItemSelectorContext;
import org.springframework.shell.tui.component.PathInput;
import org.springframework.shell.tui.component.PathSearch;
import org.springframework.shell.tui.component.PathInput.PathInputContext;
import org.springframework.shell.tui.component.PathSearch.PathSearchConfig;
import org.springframework.shell.tui.component.PathSearch.PathSearchContext;
import org.springframework.shell.tui.component.SingleItemSelector;
import org.springframework.shell.tui.component.SingleItemSelector.SingleItemSelectorContext;
import org.springframework.shell.tui.component.StringInput;
import org.springframework.shell.tui.component.StringInput.StringInputContext;
import org.springframework.shell.tui.component.support.SelectorItem;
import org.springframework.util.StringUtils;

public class ComponentCommands extends AbstractCommand {

	@Command(command = "component string", description = "String input", group = "Components")
	public String stringInput(boolean mask) {
		StringInput component = new StringInput(getTerminal(), "Enter value", "myvalue");
		component.setResourceLoader(getResourceLoader());
		component.setTemplateExecutor(getTemplateExecutor());
		if (mask) {
			component.setMaskCharacter('*');
		}
		StringInputContext context = component.run(StringInputContext.empty());
		return "Got value " + context.getResultValue();
	}

	@Command(command = "component path input", description = "Path input", group = "Components")
	public String pathInput() {
		PathInput component = new PathInput(getTerminal(), "Enter value");
		component.setResourceLoader(getResourceLoader());
		component.setTemplateExecutor(getTemplateExecutor());
		PathInputContext context = component.run(PathInputContext.empty());
		return "Got value " + context.getResultValue();
	}

	@Command(command = "component path search", description = "Path search", group = "Components")
	public String pathSearch(
		@Option(defaultValue = Option.NULL) Integer maxPathsShow,
		@Option(defaultValue = Option.NULL) Integer maxPathsSearch,
		@Option(defaultValue = "true") boolean searchForward,
		@Option(defaultValue = "false") boolean searchCaseSensitive,
		@Option(defaultValue = "false") boolean searchNormalize
	) {
		PathSearchConfig config = new PathSearch.PathSearchConfig();
		if (maxPathsShow != null) {
			config.setMaxPathsShow(maxPathsShow);
		}
		if (maxPathsSearch != null) {
			config.setMaxPathsSearch(maxPathsSearch);
		}
		config.setSearchForward(searchForward);
		config.setSearchCaseSensitive(searchCaseSensitive);
		config.setSearchNormalize(searchNormalize);
		PathSearch component = new PathSearch(getTerminal(), "Enter value", config);
		component.setResourceLoader(getResourceLoader());
		component.setTemplateExecutor(getTemplateExecutor());
		PathSearchContext context = component.run(PathSearchContext.empty());
		return "Got value " + context.getResultValue();
	}

	@Command(command = "component confirmation", description = "Confirmation input", group = "Components")
	public String confirmationInput(boolean no) {
		ConfirmationInput component = new ConfirmationInput(getTerminal(), "Enter value", !no);
		component.setResourceLoader(getResourceLoader());
		component.setTemplateExecutor(getTemplateExecutor());
		ConfirmationInputContext context = component.run(ConfirmationInputContext.empty());
		return "Got value " + context.getResultValue();
	}

	@Command(command = "component single", description = "Single selector", group = "Components")
	public String singleSelector(
		@Option(defaultValue = Option.NULL) Boolean longKeys
	) {
		List<SelectorItem<String>> items = new ArrayList<>();
		items.add(SelectorItem.of("key1", "value1"));
		items.add(SelectorItem.of("key2", "value2"));
		if (longKeys != null && longKeys == true) {
			items.add(SelectorItem.of("key3 long long long long long", "value3"));
			items.add(SelectorItem.of("key4 long long long long long long long long long long", "value4"));
		}
		SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
				items, "testSimple", null);
		component.setResourceLoader(getResourceLoader());
		component.setTemplateExecutor(getTemplateExecutor());
		SingleItemSelectorContext<String, SelectorItem<String>> context = component
				.run(SingleItemSelectorContext.empty());
		String result = context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get();
		return "Got value " + result;
	}

	@Command(command = "component multi", description = "Multi selector", group = "Components")
	public String multiSelector(
		@Option(defaultValue = Option.NULL) Boolean longKeys
	) {
		List<SelectorItem<String>> items = new ArrayList<>();
		items.add(SelectorItem.of("key1", "value1"));
		items.add(SelectorItem.of("key2", "value2", false, true));
		items.add(SelectorItem.of("key3", "value3"));
		if (longKeys != null && longKeys == true) {
			items.add(SelectorItem.of("key4 long long long long long", "value4", false, true));
			items.add(SelectorItem.of("key5 long long long long long long long long long long", "value5"));
		}
		MultiItemSelector<String, SelectorItem<String>> component = new MultiItemSelector<>(getTerminal(),
				items, "testSimple", null);
		component.setResourceLoader(getResourceLoader());
		component.setTemplateExecutor(getTemplateExecutor());
		MultiItemSelectorContext<String, SelectorItem<String>> context = component
				.run(MultiItemSelectorContext.empty());
		String result = context.getResultItems().stream()
				.map(si -> si.getItem())
				.collect(Collectors.joining(","));
		return "Got value " + result;
	}

	@Command(command = "component stringcustom", description = "String input", group = "Components")
	public String stringInputCustom(boolean mask) {
		StringInput component = new StringInput(getTerminal(), "Enter value", "myvalue",
				new StringInputCustomRenderer());
		component.setResourceLoader(getResourceLoader());
		component.setTemplateExecutor(getTemplateExecutor());
		if (mask) {
			component.setMaskCharacter('*');
		}
		StringInputContext context = component.run(StringInputContext.empty());
		return "Got value " + context.getResultValue();
	}

	private static class StringInputCustomRenderer implements Function<StringInputContext, List<AttributedString>> {

		@Override
		public List<AttributedString> apply(StringInputContext context) {
			AttributedStringBuilder builder = new AttributedStringBuilder();
			builder.append(context.getName());
			builder.append(" ");

			if (context.getResultValue() != null) {
				builder.append(context.getResultValue());
			}
			else  {
				String input = context.getInput();
				if (StringUtils.hasText(input)) {
					builder.append(input);
				}
				else {
					builder.append("[Default " + context.getDefaultValue() + "]");
				}
			}

			return Arrays.asList(builder.toAttributedString());
		}
	}
}
