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
package org.springframework.shell.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.shell.commands.support.DefaultCommentDefinitionImpl;
import org.springframework.shell.event.ParseResult;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import org.mockito.Mock;

/**
 *
 * A meaningful test for AbstractShell (not an abstract test)
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractShellTest {

    @Mock
    private ExecutionStrategy executionStrategy;

    @Mock
    private Parser parser;

    @Mock
    private ParseResult parseResult;

    private AbstractShell shell;

    @Before
    public void setup() throws Exception {
        shell = mock(AbstractShell.class);
        when(shell.executeCommand(anyString())).thenCallRealMethod();
        when(shell.getExecutionStrategy()).thenReturn(executionStrategy);
        when(executionStrategy.isReadyForCommands()).thenReturn(Boolean.TRUE);
        when(shell.areCommentsSupported()).thenReturn(Boolean.TRUE);
        when(shell.getEndCommentBlock()).thenReturn(DefaultCommentDefinitionImpl.BLOCK_END);
        when(shell.getStartCommentBlock()).thenReturn(DefaultCommentDefinitionImpl.BLOCK_START);
        when(shell.getLineCommentStarters()).thenReturn(Arrays.asList(DefaultCommentDefinitionImpl.DOUBLE_SLASH));
        when(shell.getParser()).thenReturn(parser);
        when(parser.parse(anyString())).thenReturn(parseResult);
    }

    @Test
    public void testExecuteCommand() {

        assertTrue(shell.executeCommand("test command"));
        verify(executionStrategy).execute(eq(parseResult));

    }

    @Test
    public void testExecuteCommand_inlineComment() {
        verify(executionStrategy, times(0)).execute(eq(parseResult));

        assertTrue(shell.executeCommand("/* test command */"));
        verify(executionStrategy, times(0)).execute(eq(parseResult));

    }

    @Test
    public void testExecuteCommand_fullLineComment() {

        assertTrue(shell.executeCommand("// inline comment"));
        verify(executionStrategy, times(0)).execute(eq(parseResult));

    }

    @Test
    public void testExecuteCommand_finishComment() {

        when(shell.isInBlockComment()).thenReturn(Boolean.TRUE);
        assertTrue(shell.executeCommand("end comment */"));
        verify(shell).blockCommentFinish();

    }
}
