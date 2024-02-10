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
import org.springframework.shell.component.ViewComponentBuilder;
import org.springframework.shell.style.TemplateExecutor;
import org.springframework.shell.style.ThemeResolver;

/**
 * Base class helping to build shell components.
 *
 * @author Janne Valkealahti
 */
public abstract class AbstractShellComponent implements ApplicationContextAware, InitializingBean, ResourceLoaderAware {

    private ApplicationContext applicationContext;

    private ResourceLoader resourceLoader;

    private ObjectProvider<Shell> shellProvider;

    private ObjectProvider<Terminal> terminalProvider;

    private ObjectProvider<CommandCatalog> commandCatalogProvider;

    private ObjectProvider<CompletionResolver> completionResolverProvider;

    private ObjectProvider<TemplateExecutor> templateExecutorProvider;

    private ObjectProvider<ThemeResolver> themeResolverProvider;

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
