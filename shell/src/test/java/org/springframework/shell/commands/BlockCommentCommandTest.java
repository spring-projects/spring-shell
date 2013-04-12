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
package org.springframework.shell.commands;

import static org.springframework.shell.core.CommandConstants.BLOCK_COMMENT_BEGIN01_COMMAND;
import static org.springframework.shell.core.CommandConstants.BLOCK_COMMENT_BEGIN02_COMMAND;
import static org.springframework.shell.core.CommandConstants.BLOCK_COMMENT_END_COMMAND;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.AbstractDefaultShellTest;
import org.springframework.shell.CommandResult;


/**
 * Block comment command test.
 * 
 * @author David Winterfeldt
 */
public class BlockCommentCommandTest extends AbstractDefaultShellTest {
    
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testBlockComment01SingleCommand() {
        String command = BLOCK_COMMENT_BEGIN01_COMMAND + " testing 1..2..3 " + BLOCK_COMMENT_END_COMMAND; 
        
        CommandResult cr = shell.exec(command);
        
        verifyCommandStatus(cr);
    }

    @Test
    public void testBlockComment01Command() {
        shell.exec(BLOCK_COMMENT_BEGIN01_COMMAND);
        
        execComments();
        
        CommandResult cr = shell.exec(BLOCK_COMMENT_END_COMMAND);
        
        verifyCommandStatus(cr);
    }

    @Test
    public void testBlockComment02SingleCommand() {
        String command = BLOCK_COMMENT_BEGIN02_COMMAND + " testing 1..2..3 " + BLOCK_COMMENT_END_COMMAND; 
        
        CommandResult cr = shell.exec(command);
        
        verifyCommandStatus(cr);
    }

    @Test
    public void testBlockComment02Command() {
        shell.exec(BLOCK_COMMENT_BEGIN02_COMMAND);
        
        execComments();
        
        CommandResult cr = shell.exec(BLOCK_COMMENT_END_COMMAND);
        
        verifyCommandStatus(cr);
    }
    
    private void execComments() {
        shell.exec("This is a ");
        shell.exec(" test.");
        shell.exec(" 1..2..3");
    }
    
}
