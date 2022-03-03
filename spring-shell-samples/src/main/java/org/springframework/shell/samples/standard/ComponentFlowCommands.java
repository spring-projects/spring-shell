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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.SelectItem;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class ComponentFlowCommands extends AbstractShellComponent {

	@ShellMethod(key = "flow showcase", value = "Showcase", group = "Flow")
	public void showcase() {
		Map<String, String> single1SelectItems = new HashMap<>();
		single1SelectItems.put("key1", "value1");
		single1SelectItems.put("key2", "value2");
		List<SelectItem> multi1SelectItems = Arrays.asList(SelectItem.of("key1", "value1"),
				SelectItem.of("key2", "value2"), SelectItem.of("key3", "value3"));
		ComponentFlow flow = ComponentFlow.builder(getTerminal())
				.resourceLoader(getResourceLoader())
				.templateExecutor(getTemplateExecutor())
				.withStringInput("field1")
					.name("Field1")
					.defaultValue("defaultField1Value")
					.and()
				.withStringInput("field2")
					.name("Field2")
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

	@ShellMethod(key = "flow conditional", value = "Second component based on first", group = "Flow")
	public void conditional() {
		Map<String, String> single1SelectItems = new HashMap<>();
		single1SelectItems.put("Field1", "field1");
		single1SelectItems.put("Field2", "field2");
		ComponentFlow flow = ComponentFlow.builder(getTerminal())
				.resourceLoader(getResourceLoader())
				.templateExecutor(getTemplateExecutor())
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
}
