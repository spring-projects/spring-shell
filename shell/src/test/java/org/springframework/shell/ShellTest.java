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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.shell.core.CommandConstants.DATE_COMMAND;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic shell test.
 * 
 * @author David Winterfeldt
 */
public class ShellTest extends AbstractShellTest {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
	public void testShell() {
        int expectedCompletionCount = 10;
        
	    CommandResult cr = shell.exec("\t");

	    assertEquals("Expected " + expectedCompletionCount +" default completions.", expectedCompletionCount, cr.getCompletorOutput().get("").size());
	    
	    cr = shell.exec(DATE_COMMAND);
	    
	    String result = (String) cr.getCommandOutput().get("org.springframework.shell.TestableShell." + DATE_COMMAND);
	    
	    assertNotNull("Output for 'date' command shouldn't be null.", result);
    }
	
}
