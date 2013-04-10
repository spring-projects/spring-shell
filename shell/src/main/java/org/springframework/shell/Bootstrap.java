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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.shell.core.CommandLine;
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

//    public static final String SHELL_BEAN_NAME = "shell";
    
    private final static String[] CONTEXT_PATH = { "classpath*:/META-INF/spring/shell/**/*-context.xml" };
    
    private static Bootstrap bootstrap;
	private static StopWatch sw = new StopWatch("Spring Shell");
	private static CommandLine commandLine;
	
	private AbstractApplicationContext ctx;
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
	    this(args, CONTEXT_PATH, true);
	}
	
	public Bootstrap(String[] contextPath, boolean configureLogging) {
	    this(null, contextPath, configureLogging);
	}
	
	public Bootstrap(String[] args, String[] contextPath, boolean configureLogging) {
	    if (configureLogging) {
	        configureLogging();
	    }
	    
	    try {
            commandLine = SimpleShellCommandLineOptions.parseCommandLine(args);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
	    
	    ctx = new ClassPathXmlApplicationContext();
	    ctx.registerShutdownHook();
	    ctx.refresh();
	    ((AbstractXmlApplicationContext)ctx).setConfigLocations(contextPath);
	    ctx.refresh();
	    
	    CommandLine cl = ctx.getBean(CommandLine.class);
	    cl.setArgs(commandLine.getArgs());
	    cl.setHistorySize(commandLine.getHistorySize());
	    cl.setShellCommandsToExecute(commandLine.getShellCommandsToExecute());
	    
	    shell = ctx.getBean(JLineShellComponent.class);
	    shell.setCommandLine(commandLine);
	    shell.init();
	}

	/**
	 * Suppress initial logging on startup until main logging config loads.
	 */
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