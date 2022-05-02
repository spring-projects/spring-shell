package org.springframework.shell.docs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;

import org.springframework.shell.component.ConfirmationInput;
import org.springframework.shell.component.MultiItemSelector;
import org.springframework.shell.component.PathInput;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.component.ConfirmationInput.ConfirmationInputContext;
import org.springframework.shell.component.MultiItemSelector.MultiItemSelectorContext;
import org.springframework.shell.component.PathInput.PathInputContext;
import org.springframework.shell.component.SingleItemSelector.SingleItemSelectorContext;
import org.springframework.shell.component.StringInput.StringInputContext;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;

public class UiComponentSnippets {

	// tag::snippet1[]
	class StringInputCustomRenderer implements Function<StringInputContext, List<AttributedString>> {
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
	// end::snippet1[]

	class Dump1 extends AbstractShellComponent {
		// tag::snippet2[]
		@ShellMethod(key = "component stringcustom", value = "String input", group = "Components")
		public String stringInputCustom(boolean mask) {
			StringInput component = new StringInput(getTerminal(), "Enter value", "myvalue",
					new StringInputCustomRenderer());
			component.setResourceLoader(getResourceLoader());
			component.setTemplateExecutor(getTemplateExecutor());
			if (mask) {
				component.setMaskCharater('*');
			}
			StringInputContext context = component.run(StringInputContext.empty());
			return "Got value " + context.getResultValue();
		}
		// end::snippet2[]
	}

	class Dump2 {
		// tag::snippet3[]
		@ShellComponent
		public class ComponentCommands extends AbstractShellComponent {

			@ShellMethod(key = "component string", value = "String input", group = "Components")
			public String stringInput(boolean mask) {
				StringInput component = new StringInput(getTerminal(), "Enter value", "myvalue");
				component.setResourceLoader(getResourceLoader());
				component.setTemplateExecutor(getTemplateExecutor());
				if (mask) {
					component.setMaskCharater('*');
				}
				StringInputContext context = component.run(StringInputContext.empty());
				return "Got value " + context.getResultValue();
			}
		}
		// end::snippet3[]
	}

	class Dump3 {
		// tag::snippet4[]
		@ShellComponent
		public class ComponentCommands extends AbstractShellComponent {

			@ShellMethod(key = "component path", value = "Path input", group = "Components")
			public String pathInput() {
				PathInput component = new PathInput(getTerminal(), "Enter value");
				component.setResourceLoader(getResourceLoader());
				component.setTemplateExecutor(getTemplateExecutor());
				PathInputContext context = component.run(PathInputContext.empty());
				return "Got value " + context.getResultValue();
			}
		}
		// end::snippet4[]
	}

	class Dump4 {
		// tag::snippet5[]
		@ShellComponent
		public class ComponentCommands extends AbstractShellComponent {

			@ShellMethod(key = "component confirmation", value = "Confirmation input", group = "Components")
			public String confirmationInput(boolean no) {
				ConfirmationInput component = new ConfirmationInput(getTerminal(), "Enter value", !no);
				component.setResourceLoader(getResourceLoader());
				component.setTemplateExecutor(getTemplateExecutor());
				ConfirmationInputContext context = component.run(ConfirmationInputContext.empty());
				return "Got value " + context.getResultValue();
			}
		}
		// end::snippet5[]
	}

	class Dump5 {
		// tag::snippet6[]
		@ShellComponent
		public class ComponentCommands extends AbstractShellComponent {

			@ShellMethod(key = "component single", value = "Single selector", group = "Components")
			public String singleSelector() {
				List<SelectorItem<String>> items = new ArrayList<>();
				items.add(SelectorItem.of("key1", "value1"));
				items.add(SelectorItem.of("key2", "value2"));
				SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
						items, "testSimple", null);
				component.setResourceLoader(getResourceLoader());
				component.setTemplateExecutor(getTemplateExecutor());
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
		@ShellComponent
		public class ComponentCommands extends AbstractShellComponent {

			@ShellMethod(key = "component multi", value = "Multi selector", group = "Components")
			public String multiSelector() {
				List<SelectorItem<String>> items = new ArrayList<>();
				items.add(SelectorItem.of("key1", "value1"));
				items.add(SelectorItem.of("key2", "value2", false));
				items.add(SelectorItem.of("key3", "value3"));
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
		}
		// end::snippet7[]
	}
}
