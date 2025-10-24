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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jline.terminal.impl.DumbTerminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.CommandExecution.CommandParserExceptionsException;
import org.springframework.shell.command.CommandParser;
import org.springframework.shell.command.CommandParser.CommandParserException;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.ComponentFlow.ComponentFlowResult;
import org.springframework.shell.component.flow.ResultMode;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StringUtils;

@ShellComponent
public class ComponentFlowCommands extends AbstractShellComponent {

	@Autowired
	private ComponentFlow.Builder componentFlowBuilder;

	@ShellMethod(key = "flow showcase1", value = "Showcase", group = "Flow")
	public void showcase1() {
		Map<String, String> single1SelectItems = new HashMap<>();
		single1SelectItems.put("key1", "value1");
		single1SelectItems.put("key2", "value2");
		List<SelectItem> multi1SelectItems = Arrays.asList(SelectItem.of("key1", "value1"),
				SelectItem.of("key2", "value2"), SelectItem.of("key3", "value3"));
		ComponentFlow flow = componentFlowBuilder.clone().reset()
				.withStringInput("field1")
					.name("Field1")
					.defaultValue("defaultField1Value")
					.and()
				.withStringInput("field2")
					.name("Field2")
					.and()
				.withNumberInput("number1")
					.name("Number1")
					.and()
				.withNumberInput("number2")
					.name("Number2")
					.defaultValue(20.5)
					.numberClass(Double.class)
					.and()
				.withNumberInput("number3")
					.name("Field3")
					.required()
					.and()
				.withConfirmationInput("confirmation1")
					.name("Confirmation1")
					.and()
				.withPathInput("path1")
					.name("Path1")
					.and()
				.withSingleItemSelector("single1")
					.name("Single1")
					.selectItems(single1SelectItems)
					.and()
				.withMultiItemSelector("multi1")
					.name("Multi1")
					.selectItems(multi1SelectItems)
					.and()
				.build();
		flow.run();
	}

	@ShellMethod(key = "flow showcase2", value = "Showcase with options", group = "Flow")
	public String showcase2(
		@ShellOption(help = "Field1 value", defaultValue = ShellOption.NULL) String field1,
		@ShellOption(help = "Field2 value", defaultValue = ShellOption.NULL) String field2,
		@ShellOption(help = "Number1 value", defaultValue = ShellOption.NULL) Integer number1,
		@ShellOption(help = "Number2 value", defaultValue = ShellOption.NULL) Double number2,
		@ShellOption(help = "Confirmation1 value", defaultValue = ShellOption.NULL) Boolean confirmation1,
		@ShellOption(help = "Path1 value", defaultValue = ShellOption.NULL) String path1,
		@ShellOption(help = "Single1 value", defaultValue = ShellOption.NULL) String single1,
		@ShellOption(help = "Multi1 value", defaultValue = ShellOption.NULL) List<String> multi1
	) {
		Map<String, String> single1SelectItems = new HashMap<>();
		single1SelectItems.put("key1", "value1");
		single1SelectItems.put("key2", "value2");
		List<SelectItem> multi1SelectItems = Arrays.asList(SelectItem.of("key1", "value1"),
				SelectItem.of("key2", "value2"), SelectItem.of("key3", "value3"));
		List<String> multi1ResultValues = multi1 != null ? multi1 : new ArrayList<>();
		ComponentFlow flow = componentFlowBuilder.clone().reset()
				.withStringInput("field1")
					.name("Field1")
					.defaultValue("defaultField1Value")
					.resultValue(field1)
					.resultMode(ResultMode.ACCEPT)
					.and()
				.withStringInput("field2")
					.name("Field2")
					.resultValue(field2)
					.resultMode(ResultMode.ACCEPT)
					.and()
				.withNumberInput("number1")
					.name("Number1")
					.resultValue(number1)
					.resultMode(ResultMode.ACCEPT)
					.and()
				.withNumberInput("number2")
					.name("Number2")
					.resultValue(number2)
					.numberClass(Double.class)
					.resultMode(ResultMode.ACCEPT)
					.and()
				.withConfirmationInput("confirmation1")
					.name("Confirmation1")
					.resultValue(confirmation1)
					.resultMode(ResultMode.ACCEPT)
					.and()
				.withPathInput("path1")
					.name("Path1")
					.resultValue(path1)
					.resultMode(ResultMode.ACCEPT)
					.and()
				.withSingleItemSelector("single1")
					.name("Single1")
					.selectItems(single1SelectItems)
					.resultValue(single1)
					.resultMode(ResultMode.ACCEPT)
					.and()
				.withMultiItemSelector("multi1")
					.name("Multi1")
					.selectItems(multi1SelectItems)
					.resultValues(multi1ResultValues)
					.resultMode(ResultMode.ACCEPT)
					.and()
				.build();
		ComponentFlowResult result = flow.run();
		StringBuilder buf = new StringBuilder();
		result.getContext().stream().forEach(e -> {
			buf.append(e.getKey());
			buf.append(" = ");
			buf.append(e.getValue());
			buf.append("\n");
		});
		return buf.toString();
	}

	@Bean
	public CommandRegistration showcaseRegistration() {
		return CommandRegistration.builder()
			.command("flow", "showcase3")
			.description("Showcase")
			.withOption()
				.longNames("field1")
				.and()
			.withOption()
				.longNames("field2")
				.and()
			.withOption()
				.longNames("number1")
				.and()
			.withOption()
				.longNames("confirmation1")
				.type(Boolean.class)
				.and()
			.withOption()
				.longNames("path1")
				.and()
			.withOption()
				.longNames("single1")
				.and()
			.withOption()
				.longNames("multi1")
				.and()
			.withTarget()
				.consumer(ctx -> {

					String field1 = ctx.getOptionValue("field1");
					String field2 = ctx.getOptionValue("field2");
					Integer number1 = ctx.getOptionValue("number1");
					Boolean confirmation1 = ctx.getOptionValue("confirmation1");
					String path1 = ctx.getOptionValue("path1");
					String single1 = ctx.getOptionValue("single1");
					String asdf = ctx.getOptionValue("multi1");
					List<String> multi1 = new ArrayList<>();
					if (StringUtils.hasText(asdf)) {
						multi1.add(asdf);
					}

					Map<String, String> single1SelectItems = new HashMap<>();
					single1SelectItems.put("key1", "value1");
					single1SelectItems.put("key2", "value2");
					List<SelectItem> multi1SelectItems = Arrays.asList(SelectItem.of("key1", "value1"),
							SelectItem.of("key2", "value2"), SelectItem.of("key3", "value3"));
					ComponentFlow flow = componentFlowBuilder.clone().reset()
							.withStringInput("field1")
								.name("Field1")
								.defaultValue("defaultField1Value")
								.resultValue(field1)
								.resultMode(ResultMode.ACCEPT)
								.and()
							.withStringInput("field2")
								.name("Field2")
								.resultValue(field2)
								.resultMode(ResultMode.ACCEPT)
								.and()
							.withNumberInput("number1")
								.name("Number1")
								.resultValue(number1)
								.resultMode(ResultMode.ACCEPT)
								.and()
							.withConfirmationInput("confirmation1")
								.name("Confirmation1")
								.resultValue(confirmation1)
								.resultMode(ResultMode.ACCEPT)
								.and()
							.withPathInput("path1")
								.name("Path1")
								.resultValue(path1)
								.resultMode(ResultMode.ACCEPT)
								.and()
							.withSingleItemSelector("single1")
								.name("Single1")
								.selectItems(single1SelectItems)
								.resultValue(single1)
								.resultMode(ResultMode.ACCEPT)
								.and()
							.withMultiItemSelector("multi1")
								.name("Multi1")
								.selectItems(multi1SelectItems)
								.resultValues(multi1)
								.resultMode(ResultMode.ACCEPT)
								.and()
							.build();
					ComponentFlowResult result = flow.run();

					boolean hasTty = !((ctx.getTerminal() instanceof DumbTerminal) && ctx.getTerminal().getSize().getRows() == 0);
					if (hasTty) {
						StringBuilder buf = new StringBuilder();
						result.getContext().stream().forEach(e -> {
							buf.append(e.getKey());
							buf.append(" = ");
							buf.append(e.getValue());
							buf.append("\n");
						});
						ctx.getTerminal().writer().print(buf.toString());
						ctx.getTerminal().writer().flush();
					}
					else {
						List<CommandParser.CommandParserException> errors = new ArrayList<>();
						result.getContext().stream().forEach(e -> {
							if (e.getValue() == null) {
								errors.add(CommandParserException.of(String.format("Missing option, longnames='%s'", e.getKey())));
							}
						});
						if (!result.getContext().containsKey("single1")) {
							errors.add(CommandParserException.of("Missing option, longnames='single'"));
						}
						if (!errors.isEmpty()) {
							throw CommandParserExceptionsException.of("Missing options", errors);
						}
					}
				})
				.and()
			.build();
	}

	@ShellMethod(key = "flow conditional", value = "Second component based on first", group = "Flow")
	public void conditional() {
		Map<String, String> single1SelectItems = new HashMap<>();
		single1SelectItems.put("Field1", "field1");
		single1SelectItems.put("Field2", "field2");
		ComponentFlow flow = componentFlowBuilder.clone().reset()
				.withSingleItemSelector("single1")
					.name("Single1")
					.selectItems(single1SelectItems)
					.next(ctx -> ctx.getResultItem().get().getItem())
					.and()
				.withStringInput("field1")
					.name("Field1")
					.defaultValue("defaultField1Value")
					.next(ctx -> null)
					.and()
				.withStringInput("field2")
					.name("Field2")
					.defaultValue("defaultField2Value")
					.next(ctx -> null)
					.and()
				.build();
		flow.run();
	}

	@ShellMethod(key = "flow autoselect", value = "Autoselect item", group = "Flow")
	public void autoselect(
			@ShellOption(defaultValue = "Field3") String defaultValue
		) {
		Map<String, String> single1SelectItems = IntStream.range(1, 10)
			.boxed()
			.collect(Collectors.toMap(i -> "Field" + i, i -> "field" + i));

		ComponentFlow flow = componentFlowBuilder.clone().reset()
				.withSingleItemSelector("single1")
					.name("Single1")
					.selectItems(single1SelectItems)
					.defaultSelect(defaultValue)
					.sort((o1, o2) -> o1.getName().compareTo(o2.getName()))
					.and()
				.build();
		flow.run();
	}
}
