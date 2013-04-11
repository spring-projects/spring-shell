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
package org.springframework.shell;

import static org.junit.Assert.assertNotNull;
import static org.springframework.shell.core.CommandConstants.DATE_COMMAND;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.shell.lang.ShellException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Abstract shell test.
 * 
 * @author David Winterfeldt
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:/shell-test-context.xml" })
public abstract class AbstractShellTest {

    final Logger logger = LoggerFactory.getLogger(getClass());
    
    protected final static String PATTERN = "E, MMM d, yyyy h:mm:ss a zzz";
    
    @Autowired
    private JLineShellComponent jlineShell;
    
    protected TestableShell shell;
    
    /**
     * Initializes shell if it isn't already.
     */
    @Before
    public void init() {
        if (shell == null) {
            shell = (TestableShell) jlineShell;
        }
        
        if (!shell.isRunning()) {
            shell.start();
        }
    }
    	
    /**
     * Resets shell after a test.
     */
	@After
	public void reset() {
	    shell.clear();
	}

	/**
	 * Run a command just to verify the shell is still operational after other tests.
	 */
	protected void verifyShellOperational() {
        CommandResult cr = shell.exec(DATE_COMMAND);
        
        String result = (String) cr.getCommandOutput().get(DATE_COMMAND);
        
        assertNotNull("Output for 'date' command shouldn't be null.", result);	    

        DateFormat formatter = new SimpleDateFormat(PATTERN);
        
        try {
            // date is parseable or exception would be thrown
            formatter.parse(result);
        } catch (ParseException e) {
            throw new ShellException(e.getMessage(), e);
        }
	}
	
}
