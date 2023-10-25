/*
 * Copyright 2023 the original author or authors.
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
package org.springframework.shell.samples.catalog;

import java.util.List;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.component.view.TerminalUIBuilder;
import org.springframework.shell.samples.catalog.scenario.Scenario;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.style.ThemeResolver;

/**
 * Main command access point to view showcase catalog.
 *
 * @author Janne Valkealahti
 */
@Command
public class CatalogCommand extends AbstractShellComponent {

	private final List<Scenario> scenarios;
	private final TerminalUIBuilder terminalUIBuilder;
	private final ThemeResolver themeResolver;

	public CatalogCommand(List<Scenario> scenarios, TerminalUIBuilder terminalUIBuilder, ThemeResolver themeResolver) {
		this.scenarios = scenarios;
		this.terminalUIBuilder = terminalUIBuilder;
		this.themeResolver = themeResolver;
	}

	@Command(command = "catalog")
	public void catalog() {
		Catalog catalog = new Catalog(terminalUIBuilder, themeResolver, scenarios);
		catalog.run();
	}
}
