/*
 * Copyright 2026-present the original author or authors.
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
package org.springframework.shell.test.autoconfigure;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.jline.tui.component.flow.ComponentFlow;

/**
 * Example {@link SpringBootApplication @SpringBootApplication} running a
 * {@link ComponentFlow} fed from the {@link CommandContext#inputReader() input reader},
 * for use with {@link ShellTest @ShellTest} tests.
 *
 * @author David Pilar
 */
@SpringBootApplication
public class ComponentFlowShellApplication {

	private final ComponentFlow.Builder builder;

	public ComponentFlowShellApplication(ComponentFlow.Builder builder) {
		this.builder = builder;
	}

	@Command(name = "example", description = "Asks for a reason and a confirmation")
	public String example(CommandContext commandContext) {
		ComponentFlow flow = builder.clone()
			.reset()
			.inputReader(commandContext.inputReader())
			.withStringInput("reason")
			.name("Reason:")
			.and()
			.withConfirmationInput("confirm")
			.name("Confirm?")
			.and()
			.build();

		ComponentFlow.ComponentFlowResult result = flow.run();
		boolean confirmed = result.getContext().get("confirm");
		return confirmed ? "Confirmed" : "Cancelled";
	}

}
