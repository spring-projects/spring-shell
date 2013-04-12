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
package org.springframework.shell.custom.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.shell.commands.CustomCommandConstants.REVERSE_STRING_COMMAND;

import java.util.List;

import jline.Completion;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.CommandResult;
import org.springframework.shell.custom.AbstractCustomShellTest;


/**
 * Reverse <code>String</code> command test.
 * 
 * @author David Winterfeldt
 */
public class ReverseStringCommandTest extends AbstractCustomShellTest {
    
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testReverseCommand() {
        String value ="Reversable String!";
        String command = REVERSE_STRING_COMMAND + " --value \"" + value + "\"";
        
        CommandResult cr = shell.exec(command);
        
        String result = (String) cr.getCommandOutput().get(command);
        
        assertNotNull("Result for '" + command + "' command shouldn't be null.", result);

        String reversedValue = new StringBuilder(value).reverse().toString();
        assertTrue(reversedValue.equals(result));
        
        verifySuccess(cr);
    }

    @Test
    public void testRequiredFailureCommand() {
        String command = REVERSE_STRING_COMMAND;
        
        CommandResult cr = shell.exec(command);
        
        assertTrue("Command should have error, required param missing.", cr.hasErrors());
    }

    @Test
    public void testAutocomplete() {
        String command = REVERSE_STRING_COMMAND + " --v";
        
        CommandResult cr = shell.exec(command + TAB);
        
        int expectedSize = 1;
        List<Completion> completions = cr.getCompletorOutput().get(command);
        assertEquals("Expected one completion.", expectedSize, completions.size());
        
        Completion completion = completions.get(0);

        assertNotNull("Completion shouldn't be null.", completion);
        
        verifyFailure(cr);
        
        String expectedCompletion = REVERSE_STRING_COMMAND + " --value ";
        assertEquals(expectedCompletion, completion.getFormattedValue());
        
    }

}
