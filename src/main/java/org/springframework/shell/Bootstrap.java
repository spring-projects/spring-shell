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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.shell.core.ExitShellRequest;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.shell.core.Shell;
import org.springframework.shell.support.logging.HandlerUtils;
import org.springframework.util.StopWatch;

/**
 * Loads a {@link Shell} using Spring IoC container.
 * 
 * @author Ben Alex (original Roo code)
 * @author Mark Pollack 
 *
 */
public class Bootstrap {

	private static Bootstrap bootstrap;
	private AnnotationConfigApplicationContext parentApplicationContext;
	private static StopWatch sw = new StopWatch("Spring Shell");
	private static CommandLine commandLine;
		

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
		commandLine = SimpleShellCommandLineOptions.parseCommandLine(args);
	
		parentApplicationContext = new AnnotationConfigApplicationContext();
		configureParentApplicationContext(parentApplicationContext);		
		
		ConfigurableApplicationContext childPluginApplicationContext = createChildPluginApplicationContext(parentApplicationContext);			
		
		parentApplicationContext.refresh();
		childPluginApplicationContext.refresh();
		
	}
	
	public AnnotationConfigApplicationContext getParentApplicationContext() {
		return parentApplicationContext;
	}

	private void configureParentApplicationContext(AnnotationConfigApplicationContext annctx) {
		// create parent/base childPluginApplicationContext

		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.StringConverter.class);
		createAndRegisterBeanDefinition(annctx,
				org.springframework.shell.converters.AvailableCommandsConverter.class);
		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.BigDecimalConverter.class);
		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.BigIntegerConverter.class);
		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.BooleanConverter.class);
		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.CharacterConverter.class);
		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.DateConverter.class);
		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.DoubleConverter.class);
		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.EnumConverter.class);
		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.FloatConverter.class);
		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.IntegerConverter.class);
		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.LocaleConverter.class);
		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.LongConverter.class);
		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.ShortConverter.class);
		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.StaticFieldConverterImpl.class);
		createAndRegisterBeanDefinition(annctx, org.springframework.shell.core.JLineShellComponent.class, "shell");
		createAndRegisterBeanDefinition(annctx, org.springframework.shell.converters.SimpleFileConverter.class);
		
		annctx.getBeanFactory().registerSingleton("commandLine", commandLine);
		annctx.scan("org.springframework.shell.commands");
		annctx.scan("org.springframework.shell.converters");
		annctx.scan("org.springframework.shell.plugin.support");

	}

	/**
	 * Init plugin ApplicationContext
	 * 
	 * @param annctx parent ApplicationContext in core spring shell
	 * @return new ApplicationContext in the plugin with core spring shell's context as parent
	 */
	private ConfigurableApplicationContext createChildPluginApplicationContext(AnnotationConfigApplicationContext annctx) {
		return new ClassPathXmlApplicationContext(
				new String[] { "classpath*:/META-INF/spring/spring-shell-plugin.xml" }, false, annctx);
	}

	protected void createAndRegisterBeanDefinition(AnnotationConfigApplicationContext annctx, Class clazz) {
		createAndRegisterBeanDefinition(annctx, clazz, null);
	}

	protected void createAndRegisterBeanDefinition(AnnotationConfigApplicationContext annctx, Class clazz, String name) {
		RootBeanDefinition rbd = new RootBeanDefinition();
		rbd.setBeanClass(clazz);
		if (name != null) {
			annctx.registerBeanDefinition(name, rbd);
		}
		else {
			annctx.registerBeanDefinition(clazz.getSimpleName(), rbd);
		}
	}

	// seems on JDK 1.6.0_18 or higher causes the output to disappear
	private void setupLogging() {
		// Ensure all JDK log messages are deferred until a target is registered
		Logger rootLogger = Logger.getLogger("");
		HandlerUtils.wrapWithDeferredLogHandler(rootLogger, Level.SEVERE);

		// Set a suitable priority level on Spring Framework log messages
		Logger sfwLogger = Logger.getLogger("org.springframework");
		sfwLogger.setLevel(Level.WARNING);

		// Set a suitable priority level on Roo log messages
		// (see ROO-539 and HandlerUtils.getLogger(Class))
		Logger rooLogger = Logger.getLogger("org.springframework.shell");
		rooLogger.setLevel(Level.FINE);
	}


	protected ExitShellRequest run() {

		String[] commandsToExecuteAndThenQuit = commandLine.getShellCommandsToExecute();
		// The shell is used 
		JLineShellComponent shell = parentApplicationContext.getBean("shell", JLineShellComponent.class);
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
		}
		else {
			shell.start();
			shell.promptLoop();
			exitShellRequest = shell.getExitShellRequest();
			if (exitShellRequest == null) {
				// shouldn't really happen, but we'll fallback to this anyway
				exitShellRequest = ExitShellRequest.NORMAL_EXIT;
			}
			shell.waitForComplete();
		}

		parentApplicationContext.close();
		sw.stop();
		if (shell.isDevelopmentMode()) {
			System.out.println("Total execution time: " + sw.getLastTaskTimeMillis() + " ms");
		}
		return exitShellRequest;
	}
	
	JLineShellComponent getJLineShellComponent() {
		return parentApplicationContext.getBean("shell", JLineShellComponent.class);
	}
}