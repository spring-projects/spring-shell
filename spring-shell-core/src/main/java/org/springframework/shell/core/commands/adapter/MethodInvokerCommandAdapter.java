/*
 * Copyright 2025-present the original author or authors.
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
package org.springframework.shell.core.commands.adapter;

import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.commands.AbstractCommand;
import org.springframework.util.MethodInvoker;

/**
 * @author Mahmoud Ben Hassine
 * @author Piotr Olaszewski
 */
public class MethodInvokerCommandAdapter extends AbstractCommand {

	MethodInvoker methodInvoker;

	public MethodInvokerCommandAdapter(String name, String description, String help, String group,
			MethodInvoker methodInvoker) {
		super(name, description, help, group);
		this.methodInvoker = methodInvoker;
	}

	@Override
	public void execute(CommandContext commandContext) throws Exception {
		this.methodInvoker.setArguments(commandContext);
		this.methodInvoker.prepare();
		this.methodInvoker.invoke();
		commandContext.terminal().flush();
	}

}
