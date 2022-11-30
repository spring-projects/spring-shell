/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.test.jediterm.terminal.ui;

import org.springframework.shell.test.jediterm.terminal.Terminal;
import org.springframework.shell.test.jediterm.terminal.TerminalStarter;
import org.springframework.shell.test.jediterm.terminal.TtyConnector;
import org.springframework.shell.test.jediterm.terminal.model.TerminalTextBuffer;

/**
 * @author jediterm authors
 */
public interface TerminalSession {

	void start();

	TerminalTextBuffer getTerminalTextBuffer();

	TerminalStarter getTerminalStarter();

	Terminal getTerminal();

	TtyConnector getTtyConnector();

	String getSessionName();

	void close();
}
