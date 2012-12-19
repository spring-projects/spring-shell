/*
 * Copyright 2011-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.core;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.Lifecycle;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.Resource;
import org.springframework.shell.CommandLine;
import org.springframework.shell.plugin.BannerProvider;
import org.springframework.shell.plugin.HistoryFileNameProvider;
import org.springframework.shell.plugin.PluginProvider;
import org.springframework.shell.plugin.PromptProvider;

/**
 * Launcher for {@link JLineShell}.
 *
 * @author Ben Alex
 * @since 1.1
 */
public class JLineShellComponent extends JLineShell implements SmartLifecycle, ApplicationContextAware, InitializingBean {

	@Autowired
	private CommandLine commandLine;
	
	private volatile boolean running = false;
	private Thread shellThread;

	private ApplicationContext applicationContext;
	private boolean printBanner = true;

	private static AnnotationAwareOrderComparator annotationOrderComparator = new AnnotationAwareOrderComparator();

	private String historyFileName;
	private String promptText;
	private String productName;
	private String banner;
	private String version;
	private String welcomeMessage;

	private ExecutionStrategy executionStrategy = new SimpleExecutionStrategy();
	private SimpleParser parser = new SimpleParser();

	public SimpleParser getSimpleParser() {
		return parser;
	}

	public boolean isAutoStartup() {
		return false;
	}
	
	public void stop(Runnable callback) {
		stop();
		callback.run();
	}
	
	public int getPhase() {
		return 1;
	}
	
	public void start() {
		//customizePlug must run before start thread to take plugin's configuration into effect
		customizePlugin();
		shellThread = new Thread(this, "Spring Shell");
		shellThread.start();
		running = true;
	}


	public void stop() {
		if (running) {
			closeShell();
			running = false;
		}
	}

	public boolean isRunning() {
		return running;
	}
	
	public void afterPropertiesSet() {

		Map<String, CommandMarker> commands = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, CommandMarker.class);
		for (CommandMarker command : commands.values()) {
			getSimpleParser().add(command);
		}

		Map<String, Converter> converters = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, Converter.class);
		for (Converter converter : converters.values()) {
			getSimpleParser().add(converter);
		}
		
		setHistorySize(commandLine.getHistorySize());
		if (commandLine.getShellCommandsToExecute() != null) {
			setPrintBanner(false);
		}
	}

	/**
	 * wait the shell command to complete by typing "quit" or "exit" 
	 * 
	 */
	public void waitForComplete() {
		try {
			shellThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Collection<URL> findResources(final String path) {
		try {
			Resource[] resources = applicationContext.getResources(path);
			Collection<URL> list = new ArrayList<URL>(resources.length);
			for (Resource resource : resources) {
				list.add(resource.getURL());
			}
			return list;
		} catch (IOException ex) {
			logger.fine("Cannot find path " + path);
			// return Collections.emptyList();
			throw new RuntimeException(ex);
		}
	}

	@Override
	protected ExecutionStrategy getExecutionStrategy() {
		return executionStrategy;
	}

	@Override
	protected Parser getParser() {
		return parser;
	}

	@Override
	public String getStartupNotifications() {
		return null;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void customizePlugin() {
		this.historyFileName = getHistoryFileName();
		this.promptText = getPromptText();
		String[] banner = getBannerText();
		this.banner = banner[0];
		this.welcomeMessage = banner[1];
		this.version = banner[2];
		this.productName = banner[3];
	}

	/**
	 * get history file name from provider. The provider has highest order 
	 * <link>org.springframework.core.Ordered.getOder</link> will win. 
	 * 
	 * @return history file name 
	 */
	protected String getHistoryFileName() {
		String providerHistoryFileName = getHighestPriorityProvider(HistoryFileNameProvider.class).getHistoryFileName();
		if (providerHistoryFileName != null) {
			return providerHistoryFileName;
		} else {
			return historyFileName;
		}
	}

	/**
	 * get prompt text from provider. The provider has highest order 
	 * <link>org.springframework.core.Ordered.getOder</link> will win. 
	 * 
	 * @return prompt text
	 */
	protected String getPromptText() {
		String providerPromptText = getHighestPriorityProvider(PromptProvider.class).getPrompt();
		if (providerPromptText != null) {
			return providerPromptText;
		} else {
			return promptText;
		}
	}

	/**
	 * Get Banner and Welcome Message from provider. The provider has highest order 
	 * <link>org.springframework.core.Ordered.getOder</link> will win. 
	 * @return BannerText[0]: Banner
	 *         BannerText[1]: Welcome Message
	 *         BannerText[2]: Version
	 *         BannerText[3]: Product Name
	 */
	private String[] getBannerText() {
		String[] bannerText = new String[4];
		BannerProvider provider = getHighestPriorityProvider(BannerProvider.class);
		bannerText[0] = provider.getBanner();
		bannerText[1] = provider.getWelcomeMessage();
		bannerText[2] = provider.getVersion();
		bannerText[3] = provider.name();
		return bannerText;
	}


	private <T extends PluginProvider> T getHighestPriorityProvider(Class<T> t) {
		Map<String, T> providers = BeanFactoryUtils.beansOfTypeIncludingAncestors(this.applicationContext, t);
		List<T> sortedProviders = new ArrayList<T>(providers.values());
		Collections.sort(sortedProviders, annotationOrderComparator);
		T highestPriorityProvider = sortedProviders.get(0);
		return highestPriorityProvider;
	}
	
	public void printBannerAndWelcome() {
	    if (printBanner) {
			logger.info(this.banner);
			logger.info(getWelcomeMessage());
		}
	}


	/**
	 * get the welcome message at start.
	 * 
	 * @return welcome message
	 */
	public String getWelcomeMessage() {
		return this.welcomeMessage;
	}


	/**
	 * @param printBanner the printBanner to set
	 */
	public void setPrintBanner(boolean printBanner) {
		this.printBanner = printBanner;
	}
	
	protected String getProductName() {
		return productName;
	}
	
	protected String getVersion() {
		return version;
	}
}