/*
 * Copyright 2021-2024 the original author or authors.
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
package org.springframework.shell.standard;

import java.util.stream.Stream;

import org.jline.terminal.Terminal;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.Shell;
import org.springframework.shell.command.CommandCatalog;
import org.springframework.shell.completion.CompletionResolver;
import org.springframework.shell.tui.component.ViewComponentBuilder;
import org.springframework.shell.tui.style.TemplateExecutor;
import org.springframework.shell.tui.style.ThemeResolver;

/**
 * Base class helping to build shell components.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 */
public abstract class AbstractShellComponent implements ApplicationContextAware, InitializingBean, ResourceLoaderAware {

	@SuppressWarnings("NullAway.Init")
	private ApplicationContext applicationContext;

	@SuppressWarnings("NullAway.Init")
	private ResourceLoader resourceLoader;

	@SuppressWarnings("NullAway.Init")
	private ObjectProvider<Shell> shellProvider;

	@SuppressWarnings("NullAway.Init")
	private ObjectProvider<Terminal> terminalProvider;

	@SuppressWarnings("NullAway.Init")
	private ObjectProvider<CommandCatalog> commandCatalogProvider;

	@SuppressWarnings("NullAway.Init")
	private ObjectProvider<CompletionResolver> completionResolverProvider;

	@SuppressWarnings("NullAway.Init")
	private ObjectProvider<TemplateExecutor> templateExecutorProvider;

	@SuppressWarnings("NullAway.Init")
	private ObjectProvider<ThemeResolver> themeResolverProvider;

	@SuppressWarnings("NullAway.Init")
	private ObjectProvider<ViewComponentBuilder> viewComponentBuilderProvider;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		shellProvider = applicationContext.getBeanProvider(Shell.class);
		terminalProvider = applicationContext.getBeanProvider(Terminal.class);
		commandCatalogProvider = applicationContext.getBeanProvider(CommandCatalog.class);
		completionResolverProvider = applicationContext.getBeanProvider(CompletionResolver.class);
		templateExecutorProvider = applicationContext.getBeanProvider(TemplateExecutor.class);
		themeResolverProvider = applicationContext.getBeanProvider(ThemeResolver.class);
		viewComponentBuilderProvider = applicationContext.getBeanProvider(ViewComponentBuilder.class);
	}

	protected ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	protected ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	protected Shell getShell() {
		return shellProvider.getObject();
	}

	protected Terminal getTerminal() {
		return terminalProvider.getObject();
	}

	protected CommandCatalog getCommandCatalog() {
		return commandCatalogProvider.getObject();
	}

	protected Stream<CompletionResolver> getCompletionResolver() {
		return completionResolverProvider.orderedStream();
	}

	protected TemplateExecutor getTemplateExecutor() {
		return templateExecutorProvider.getObject();
	}

	protected ThemeResolver getThemeResolver() {
		return themeResolverProvider.getObject();
	}

	protected ViewComponentBuilder getViewComponentBuilder() {
		return viewComponentBuilderProvider.getObject();
	}
}
