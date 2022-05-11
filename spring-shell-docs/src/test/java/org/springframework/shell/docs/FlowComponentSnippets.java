package org.springframework.shell.docs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.SelectItem;

public class FlowComponentSnippets {

	// tag::snippet1[]
	static class FlowSampleComplex {

		@Autowired
		private ComponentFlow.Builder componentFlowBuilder;

		public void runFlow() {
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

	}
	// end::snippet1[]


	// tag::snippet2[]
	static class FlowSampleConditional {

		@Autowired
		private ComponentFlow.Builder componentFlowBuilder;

		public void runFlow() {
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

	}
	// end::snippet2[]

}
