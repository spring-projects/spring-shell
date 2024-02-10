/*
 * Copyright 2023-2024 the original author or authors.
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
package org.springframework.shell.boot;

import org.jline.terminal.Terminal;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.shell.component.ViewComponentBuilder;
import org.springframework.shell.component.ViewComponentExecutor;
import org.springframework.shell.component.view.TerminalUI;
import org.springframework.shell.component.view.TerminalUIBuilder;
import org.springframework.shell.component.view.TerminalUICustomizer;
import org.springframework.shell.style.ThemeActive;
import org.springframework.shell.style.ThemeResolver;

@AutoConfiguration
@ConditionalOnClass(TerminalUI.class)
public class TerminalUIAutoConfiguration {

	@Bean
	@Scope("prototype")
	@ConditionalOnMissingBean
	public TerminalUIBuilder terminalUIBuilder(Terminal terminal, ThemeResolver themeResolver, ThemeActive themeActive,
			ObjectProvider<TerminalUICustomizer> customizerProvider) {
		TerminalUIBuilder builder = new TerminalUIBuilder(terminal);
		builder = builder.themeName(themeActive.get());
		builder = builder.themeResolver(themeResolver);
		builder = builder.customizers(customizerProvider.orderedStream().toList());
		return builder;
	}

	@Bean
	@Scope("prototype")
	@ConditionalOnMissingBean
	public ViewComponentBuilder viewComponentBuilder(TerminalUIBuilder terminalUIBuilder,
			ViewComponentExecutor viewComponentExecutor, Terminal terminal) {
		return new ViewComponentBuilder(terminalUIBuilder, viewComponentExecutor, terminal);
	}

	@Bean
	@ConditionalOnMissingBean
	public ViewComponentExecutor viewComponentExecutor() {
		return new ViewComponentExecutor();
	}

}
