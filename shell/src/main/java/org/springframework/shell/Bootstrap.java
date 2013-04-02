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
package org.springframework.shell;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.shell.converter.Converter;
import org.springframework.shell.core.CommandLine;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.ExitShellRequest;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.shell.core.Shell;
import org.springframework.shell.support.logging.HandlerUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StopWatch;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * Loads a {@link Shell} using Spring IoC container.
 * 
 * @author Ben Alex (original Roo code)
 * @author Mark Pollack 
 *
 */
public class Bootstrap {

    public static final String SHELL_BEAN_NAME = "shell";
    
    private final static String[] CONTEXT_PATH = { "classpath*:/META-INF/spring/shell/**/*-context.xml" };
//    private static final String CHILD_CONTEXT_PATH = "classpath*:/META-INF/spring/spring-shell-plugin.xml";
    
    private static Bootstrap bootstrap;
	private static StopWatch sw = new StopWatch("Spring Shell");
	private static CommandLine commandLine;
	
	private AbstractApplicationContext ctx;
//	private AnnotationConfigApplicationContext parentApplicationContext;
	private JLineShellComponent shell;
		

	public static void main(String[] args) throws IOException {
		sw.start();
		ExitShellRequest exitShellRequest;
		try {
			bootstrap = new Bootstrap(args);			
			exitShellRequest = bootstrap.run();
		} catch (RuntimeException t) {
			throw t;
		} finally {
			HandlerUtils.flushAllHandlers(Logger.getLogger(""));
		}

		System.exit(exitShellRequest.getExitCode());
	}

	public Bootstrap(String[] args) throws IOException {
	    configureLogging();
	    
	    commandLine = SimpleShellCommandLineOptions.parseCommandLine(args);
	    
	    ctx = new ClassPathXmlApplicationContext();
	    ctx.refresh();
//	    ctx.getBeanFactory().registerSingleton("commandLine", commandLine);
	    ((AbstractXmlApplicationContext)ctx).setConfigLocations(CONTEXT_PATH);
	    ctx.refresh();
	    
	    CommandLine cl = ctx.getBean(CommandLine.class);
	    cl.setArgs(commandLine.getArgs());
	    cl.setHistorySize(commandLine.getHistorySize());
	    cl.setShellCommandsToExecute(commandLine.getShellCommandsToExecute());
	    
	    shell = ctx.getBean(JLineShellComponent.class);
	    shell.setCommandLine(commandLine);
	    shell.init();
	}
	
//	public Bootstrap(String[] args) throws IOException {
//	    configureLogging();
//	    
//		commandLine = SimpleShellCommandLineOptions.parseCommandLine(args);
//
//		parentApplicationContext = new AnnotationConfigApplicationContext();
//		configureParentApplicationContext(parentApplicationContext);
//		
//		// FIXME: create multiple child contexts for each spring-shell-plugin.xml
//		
//		ConfigurableApplicationContext childPluginApplicationContext = createChildPluginApplicationContext(parentApplicationContext);			
//		
//		parentApplicationContext.refresh();
//		childPluginApplicationContext.refresh();
//
//		shell = parentApplicationContext.getBean(SHELL_BEAN_NAME, JLineShellComponent.class);
//		        
////	    initShell(parentApplicationContext);
//	    initShell(childPluginApplicationContext);
//	}

    private void configureLogging() {
        URL defaultShellLogbackConfig = ClassUtils.getDefaultClassLoader().getResource("default_shell_logback_config.xml");

        JoranConfigurator configurator = new JoranConfigurator();
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        configurator.setContext(loggerContext);
        loggerContext.reset();
        
        try {
            InputStream logConfigStream = defaultShellLogbackConfig.openStream();
            configurator.doConfigure(logConfigStream);
        } catch (Exception e) {
            StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
        }

        Logger rootLogger = Logger.getLogger("");
        HandlerUtils.wrapWithDeferredLogHandler(rootLogger, Level.SEVERE);
    }

	public ApplicationContext getApplicationContext() {
		return ctx;
	}

//	private void configureParentApplicationContext(AnnotationConfigApplicationContext annctx) {
//		// create parent/base childPluginApplicationContext
//
//		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.StringConverter.class);
//		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.AvailableCommandsConverter.class);
//		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.BigDecimalConverter.class);
//		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.BigIntegerConverter.class);
//		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.BooleanConverter.class);
//		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.CharacterConverter.class);
//		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.DateConverter.class);
//		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.DoubleConverter.class);
//		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.EnumConverter.class);
//		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.FloatConverter.class);
//		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.IntegerConverter.class);
//		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.LocaleConverter.class);
//		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.LongConverter.class);
//		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.ShortConverter.class);
//		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.StaticFieldConverterImpl.class);
//		createAndRegisterBeanDefinition(annctx, org.springframework.shell.core.JLineShellComponent.class, SHELL_BEAN_NAME);
//		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.SimpleFileConverter.class);
//		
//		annctx.getBeanFactory().registerSingleton("commandLine", commandLine);
//		annctx.scan("org.springframework.shell.commands");
//		annctx.scan("org.springframework.shell.converters");
//		annctx.scan("org.springframework.shell.plugin.support");
//	}

	/**
	 * Init plugin ApplicationContext
	 * 
	 * @param annctx parent ApplicationContext in core spring shell
	 * @return new ApplicationContext in the plugin with core spring shell's context as parent
	 */
//	private ConfigurableApplicationContext createChildPluginApplicationContext(AnnotationConfigApplicationContext annctx) {
//		return new ClassPathXmlApplicationContext(new String[] { CHILD_CONTEXT_PATH }, false, annctx);
//	}
//
//	protected void createAndRegisterBeanDefinition(AnnotationConfigApplicationContext annctx, Class clazz) {
//		createAndRegisterBeanDefinition(annctx, clazz, null);
//	}
//
//	protected void createAndRegisterBeanDefinition(AnnotationConfigApplicationContext annctx, Class clazz, String name) {
//		RootBeanDefinition rbd = new RootBeanDefinition();
//		rbd.setBeanClass(clazz);
//		if (name != null) {
//			annctx.registerBeanDefinition(name, rbd);
//		} else {
//			annctx.registerBeanDefinition(clazz.getSimpleName(), rbd);
//		}
//	}

	// seems on JDK 1.6.0_18 or higher causes the output to disappear
//	private void setupLogging() {
//		// Ensure all JDK log messages are deferred until a target is registered
//		Logger rootLogger = Logger.getLogger("");
//		HandlerUtils.wrapWithDeferredLogHandler(rootLogger, Level.SEVERE);
//
//		// Set a suitable priority level on Spring Framework log messages
//		Logger sfwLogger = Logger.getLogger("org.springframework");
//		sfwLogger.setLevel(Level.WARNING);
//
//		// Set a suitable priority level on Roo log messages
//		// (see ROO-539 and HandlerUtils.getLogger(Class))
//		Logger rooLogger = Logger.getLogger("org.springframework.shell");
//		rooLogger.setLevel(Level.FINE);
//	}


	protected ExitShellRequest run() {
		String[] commandsToExecuteAndThenQuit = commandLine.getShellCommandsToExecute();
		ExitShellRequest exitShellRequest;

		if (null != commandsToExecuteAndThenQuit) {
			boolean successful = false;
			exitShellRequest = ExitShellRequest.FATAL_EXIT;

			for (String cmd : commandsToExecuteAndThenQuit) {
				successful = shell.executeCommand(cmd);
				if (!successful)
					break;
			}

			//if all commands were successful, set the normal exit status
			if (successful) {
				exitShellRequest = ExitShellRequest.NORMAL_EXIT;
			}
		} else {
			shell.start();
			shell.promptLoop();
			exitShellRequest = shell.getExitShellRequest();
			if (exitShellRequest == null) {
				// shouldn't really happen, but we'll fallback to this anyway
				exitShellRequest = ExitShellRequest.NORMAL_EXIT;
			}
			shell.waitForComplete();
		}

		ctx.close();
		sw.stop();
		if (shell.isDevelopmentMode()) {
			System.out.println("Total execution time: " + sw.getLastTaskTimeMillis() + " ms");
		}
		return exitShellRequest;
	}
	
	JLineShellComponent getJLineShellComponent() {
		return shell;
	}
	
}