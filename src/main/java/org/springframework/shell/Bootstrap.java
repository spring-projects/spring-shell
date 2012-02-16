/*
 * Copyright (C) 2011 VMware, Inc.  All rights reserved. -- VMware Confidential
 */
package org.springframework.shell;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.roo.shell.AbstractShell;
import org.springframework.roo.shell.CommandMarker;
import org.springframework.roo.shell.Converter;
import org.springframework.roo.shell.ExitShellRequest;
import org.springframework.roo.shell.Shell;
import org.springframework.roo.shell.event.ShellStatus;
import org.springframework.roo.support.logging.HandlerUtils;
import org.springframework.roo.support.util.Assert;
import org.springframework.util.StopWatch;

//import ch.qos.logback.classic.LoggerContext;
//import ch.qos.logback.classic.joran.JoranConfigurator;
//import ch.qos.logback.core.util.StatusPrinter;

/**
 * Main class, needs some cleanup
 * 
 * @author vnagaraja
 */
public class Bootstrap {

    private static Bootstrap bootstrap;
    //TODO using JLineShellComponenet to override and get access to "getSimpleParser" method on the shell - look into autowire...and move back to reference Shell interface.
    private JLineShellComponent shell;
    private ConfigurableApplicationContext ctx;
    private static StopWatch sw = new StopWatch("Spring Sehll");

    public static void main(String[] args) throws IOException {
        sw.start();        
        SimpleShellCommandLineOptions options = SimpleShellCommandLineOptions.parseCommandLine(args);

        for (Map.Entry<String, String> entry : options.extraSystemProperties.entrySet()) {
            System.setProperty(entry.getKey(), entry.getValue());
        }
        ExitShellRequest exitShellRequest;
        try {
            bootstrap = new Bootstrap(options.applicationContextLocation);
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

        Assert.hasText(applicationContextLocation, "Application context location required");

        ctx = new ClassPathXmlApplicationContext(applicationContextLocation);

        
        Map<String, JLineShellComponent> shells = ctx.getBeansOfType(JLineShellComponent.class);

        //Assert.isTrue(shells.size() == 1, "Exactly one Shell was required, but " + shells.size() + " found");
        //shell = shells.values().iterator().next();
        
        shell = new JLineShellComponent();
        


        Map<String, CommandMarker> commands = ctx.getBeansOfType(CommandMarker.class);

        for (CommandMarker command : commands.values()) {    
           System.out.println("Registgering command " + command);
           shell.getSimpleParser().add(command);
        }

        Map<String, Converter> converters = ctx.getBeansOfType(Converter.class);

        for (Converter converter : converters.values()) {
          System.out.println("Registgering converter " + converter);
          shell.getSimpleParser().add(converter);
        }  
        
        
        shell.start();
        //TODO use listener and latch..
        while(true) {
        	//System.out.println("shell status = " + shell.getShellStatus().getStatus());
        	if (shell.getShellStatus().getStatus().equals(ShellStatus.Status.USER_INPUT)) {
        		break;
        	} else {        		
    			try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }

        

    }

    protected ExitShellRequest run(String[] executeThenQuit) {
        
        ExitShellRequest exitShellRequest;
        
        if (null != executeThenQuit) {
            boolean successful = false;
            exitShellRequest = ExitShellRequest.FATAL_EXIT;

            for(String cmd : executeThenQuit) {
                successful = shell.executeCommand(cmd);
                if(!successful)
                    break;
            }

            //if all commands were successful, set the normal exit status
            if (successful) {
                exitShellRequest = ExitShellRequest.NORMAL_EXIT;
            }
        } else {
            shell.promptLoop();
            exitShellRequest = shell.getExitShellRequest();
            if (exitShellRequest == null) {
                // shouldn't really happen, but we'll fallback to this anyway
                exitShellRequest = ExitShellRequest.NORMAL_EXIT;
            }
        }

        ctx.close();
        sw.stop();
        if (shell.isDevelopmentMode()) {
            System.out.println("Total execution time: " + sw.getLastTaskTimeMillis() + " ms");
        }
        return exitShellRequest;
    }

}
