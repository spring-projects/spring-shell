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

import java.io.IOException;

/**
 * Shell Event interface
 * @author tushark
 *
 */
public interface ShellEvent {
    
    public static final byte TAB = '\t';
    public static final int EOF = -1;
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String COMMAND_SEP = "";

    public ShellEvent tab() throws IOException;

    public ShellEvent addChars(String seq) throws IOException;

    public ShellEvent addCtrlZ() throws IOException;

    public ShellEvent addCtrlD() throws IOException;

    /**
     *  Signals <code>InputStream</code> to release all events.
     */
    public ShellEvent newline() throws IOException;

    public ShellEvent end();

    public void clearEvents();

    public void waitForOutput();

    public void terminate();

    public void eof();

    // TODO TO add other events

}
