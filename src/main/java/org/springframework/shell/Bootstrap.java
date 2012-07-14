/*
 * Copyright (C) 2011 VMware, Inc.  All rights reserved. -- VMware Confidential
 */
package org.springframework.shell;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.ExitShellRequest;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.shell.core.Shell;
import org.springframework.shell.support.logging.HandlerUtils;
import org.springframework.util.StopWatch;

/**
 * Loads a {@link Shell} using Spring IoC container.
 * 
 * @author Ben Alex
 * @since 1.0
 *
 */
public class Bootstrap {

	private static Bootstrap bootstrap;
	private JLineShellComponent shell;
	private ConfigurableApplicationContext ctx;
	private static StopWatch sw = new StopWatch("Spring Shell");
	private static SimpleShellCommandLineOptions options;

	public static void main(String[] args) throws IOException {
		sw.start();
		options = SimpleShellCommandLineOptions.parseCommandLine(args);

		for (Map.Entry<String, String> entry : options.extraSystemProperties.entrySet()) {
			System.setProperty(entry.getKey(), entry.getValue());
		}
		ExitShellRequest exitShellRequest;
		try {
			bootstrap = new Bootstrap(null);
			exitShellRequest = bootstrap.run(options.executeThenQuit);
		} catch (RuntimeException t) {
			throw t;
		} finally {
			HandlerUtils.flushAllHandlers(Logger.getLogger(""));
		}

		System.exit(exitShellRequest.getExitCode());
	}

	public Bootstrap(String applicationContextLocation) throws IOException {
		//setupLogging();
		createApplicationContext();

		shell = ctx.getBean("shell", JLineShellComponent.class);
		shell.setApplicationContext(ctx);
		shell.setHistorySize(options.historySize);
		if (options.executeThenQuit != null) {
			shell.setPrintBanner(false);
		}

		Map<String, CommandMarker> commands = BeanFactoryUtils.beansOfTypeIncludingAncestors(ctx, CommandMarker.class);
		for (CommandMarker command : commands.values()) {
			shell.getSimpleParser().add(command);
		}

		Map<String, Converter> converters = BeanFactoryUtils.beansOfTypeIncludingAncestors(ctx, Converter.class);
		for (Converter converter : converters.values()) {
			shell.getSimpleParser().add(converter);
		}

		shell.start();
	}

	private void createApplicationContext() {
		// create parent/base ctx
		AnnotationConfigApplicationContext annctx = new AnnotationConfigApplicationContext();
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

		annctx.scan("org.springframework.shell.commands");
		annctx.scan("org.springframework.shell.converters");
		annctx.scan("org.springframework.shell.plugin.support");
		annctx.refresh();

		ctx = initPluginApplicationContext(annctx);
		ctx.refresh();
	}

	/**
	 * Init plugin ApplicationContext
	 * 
	 * @param annctx parent ApplicationContext in core spring shell
	 * @return new ApplicationContext in the plugin with core spring shell's context as parent
	 */
	private ConfigurableApplicationContext initPluginApplicationContext(AnnotationConfigApplicationContext annctx) {
		return new ClassPathXmlApplicationContext(
				new String[] { "classpath*:/META-INF/spring/spring-shell-plugin.xml" }, true, annctx);
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


	protected ExitShellRequest run(String[] executeThenQuit) {

		ExitShellRequest exitShellRequest;

		if (null != executeThenQuit) {
			boolean successful = false;
			exitShellRequest = ExitShellRequest.FATAL_EXIT;

			for (String cmd : executeThenQuit) {
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
}