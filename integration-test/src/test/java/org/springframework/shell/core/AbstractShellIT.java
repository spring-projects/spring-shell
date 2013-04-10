/*
 * Copyright 2011-2013 the original author or authors.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Start shell jar test.
 */
public abstract class AbstractShellIT {
    
    final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final static String SHELL_JAR_PATH = "../shell/target/spring-shell.jar";
    
    private final String shellJarPath;
    
    public AbstractShellIT() {
        File shellJar = new File(SHELL_JAR_PATH);
        shellJarPath = shellJar.getAbsolutePath();

        assertTrue("Shell jar does not exist at '" + shellJarPath + "'.", shellJar.exists());
        
        logger.debug("Running Shell jar from '{}'.", shellJarPath);        
    }

    /**
     * Executes the shell.
     */
    protected String exec() throws IOException {
        return exec(new String[]{});
    }
    
    /**
     * Executes the shell with command.
     */
    protected String exec(String command) throws IOException {
        return exec(new String[] { command });
    }
    
    /**
     * Executes the shell with command.
     */
    protected String exec(String[] commands) throws IOException {
        String result = null;
        
        // doesn't exit right on windows
        if (!SystemUtils.IS_OS_WINDOWS) {
            Executor exec = new DefaultExecutor();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(os);
            
            // 15 second timeout
            ExecuteWatchdog watchdog = new ExecuteWatchdog(15 * 1000);
            exec.setStreamHandler(streamHandler);
            exec.setWatchdog(watchdog);
            
            CommandLine cl = CommandLine.parse("java -jar " + shellJarPath);

            if (commands != null) {
                for (String command : commands) {
                    if (StringUtils.isNotBlank(command)) {
                        cl.addArgument(command);
                    }
                }
            }
            
            int exitValue = -1;
            
            try {
                exitValue = exec.execute(cl);
                
                result = os.toString();
            } catch (ExecuteException e) {
                exitValue = e.getExitValue();

                throw e;
            } catch (IOException e) {
                logger.error(e.getMessage());
                
                throw e;
            }
            
            logger.debug("Shell jar start. exitCode={}", exitValue);
            
            int expectedExitValue = 0;

            assertEquals(expectedExitValue, exitValue);
            
        }

        if (result.indexOf("\n") != -1) {
            result = StringUtils.substringBeforeLast(result, "\n");
            result = StringUtils.substringAfterLast(result, "\n");
        }
        
        return result;
    }
    
}
