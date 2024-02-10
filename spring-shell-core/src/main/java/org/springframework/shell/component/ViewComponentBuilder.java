/*
 * Copyright 2024 the original author or authors.
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
package org.springframework.shell.component;

import org.jline.terminal.Terminal;

import org.springframework.shell.component.view.TerminalUIBuilder;
import org.springframework.shell.component.view.control.View;

/**
 * Builder that can be used to configure and create a {@link ViewComponent}.
 *
 * @author Janne Valkealahti
 */
public class ViewComponentBuilder {

	private final TerminalUIBuilder terminalUIBuilder;
	private final ViewComponentExecutor viewComponentExecutor;
	private final Terminal terminal;

	public ViewComponentBuilder(TerminalUIBuilder terminalUIBuilder, ViewComponentExecutor viewComponentExecutor,
			Terminal terminal) {
		this.terminalUIBuilder = terminalUIBuilder;
		this.viewComponentExecutor = viewComponentExecutor;
		this.terminal = terminal;
	}

	/**
	 * Build a new {@link ViewComponent} instance and configure it using this builder.
	 *
	 * @param view the view to use with view component
	 * @return a configured {@link ViewComponent} instance.
	 */
	public ViewComponent build(View view) {
		return new ViewComponent(terminalUIBuilder.build(), terminal, viewComponentExecutor, view);
	}

}
