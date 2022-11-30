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

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.shell.test.jediterm.terminal.Terminal;
import org.springframework.shell.test.jediterm.terminal.TerminalDisplay;
import org.springframework.shell.test.jediterm.terminal.TerminalStarter;
import org.springframework.shell.test.jediterm.terminal.TextStyle;
import org.springframework.shell.test.jediterm.terminal.TtyBasedArrayDataStream;
import org.springframework.shell.test.jediterm.terminal.TtyConnector;
import org.springframework.shell.test.jediterm.terminal.model.JediTerminal;
import org.springframework.shell.test.jediterm.terminal.model.LinesBuffer;
import org.springframework.shell.test.jediterm.terminal.model.StyleState;
import org.springframework.shell.test.jediterm.terminal.model.TerminalTextBuffer;

/**
 * @author jediterm authors
 */
public class JediTermWidget implements TerminalSession, TerminalWidget {

	private static final Logger log = LoggerFactory.getLogger(JediTermWidget.class);
	private final TerminalPanel terminalPanel;
	private final JediTerminal terminal;
	private final AtomicBoolean sessionRunning = new AtomicBoolean();
	private TtyConnector ttyConnector;
	private TerminalStarter terminalStarter;
	private Thread emuThread;

	public JediTermWidget() {
		this(80, 24);
	}

	public JediTermWidget(int columns, int lines) {
		StyleState styleState = createDefaultStyle();
		TerminalTextBuffer terminalTextBuffer = new TerminalTextBuffer(columns, lines, styleState,
				LinesBuffer.DEFAULT_MAX_LINES_COUNT);
		terminalPanel = createTerminalPanel(styleState, terminalTextBuffer);
		terminal = new JediTerminal(terminalPanel, terminalTextBuffer, styleState);
		terminalPanel.setCoordAccessor(terminal);
		sessionRunning.set(false);
	}

	protected StyleState createDefaultStyle() {
		StyleState styleState = new StyleState();
		styleState.setDefaultStyle(new TextStyle());
		return styleState;
	}

	protected TerminalPanel createTerminalPanel(StyleState styleState, TerminalTextBuffer terminalTextBuffer) {
		return new TerminalPanel(terminalTextBuffer, styleState);
	}

	public TerminalDisplay getTerminalDisplay() {
		return getTerminalPanel();
	}

	public TerminalPanel getTerminalPanel() {
		return terminalPanel;
	}

	public void setTtyConnector(TtyConnector ttyConnector) {
		this.ttyConnector = ttyConnector;
		terminalStarter = createTerminalStarter(terminal, ttyConnector);
		terminalPanel.setTerminalStarter(terminalStarter);
	}

	protected TerminalStarter createTerminalStarter(JediTerminal terminal, TtyConnector connector) {
		TtyBasedArrayDataStream ttyBasedArrayDataStream = new TtyBasedArrayDataStream(connector);
		return new TerminalStarter(terminal, connector, ttyBasedArrayDataStream);
	}

	@Override
	public TtyConnector getTtyConnector() {
		return ttyConnector;
	}

	@Override
	public Terminal getTerminal() {
		return terminal;
	}

	@Override
	public String getSessionName() {
		if (ttyConnector != null) {
			return ttyConnector.getName();
		} else {
			return "Session";
		}
	}

	public void start() {
		if (!sessionRunning.get()) {
			emuThread = new Thread(new EmulatorTask());
			emuThread.start();
		} else {
			log.error("Should not try to start session again at this point... ");
		}
	}

	public void stop() {
		if (sessionRunning.get() && emuThread != null) {
			emuThread.interrupt();
		}
	}

	public boolean isSessionRunning() {
		return sessionRunning.get();
	}

	@Override
	public TerminalTextBuffer getTerminalTextBuffer() {
		return terminalPanel.getTerminalTextBuffer();
	}

	public boolean canOpenSession() {
		return !isSessionRunning();
	}

	@Override
	public void setTerminalPanelListener(TerminalPanelListener terminalPanelListener) {
		terminalPanel.setTerminalPanelListener(terminalPanelListener);
	}

	@Override
	public TerminalSession getCurrentSession() {
		return this;
	}

	@Override
	public JediTermWidget createTerminalSession(TtyConnector ttyConnector) {
		setTtyConnector(ttyConnector);
		return this;
	}

	@Override
	public void close() {
		stop();
		if (terminalStarter != null) {
			terminalStarter.close();
		}
		terminalPanel.dispose();
	}

	class EmulatorTask implements Runnable {
		public void run() {
			try {
				sessionRunning.set(true);
				Thread.currentThread().setName("Connector-" + ttyConnector.getName());
				if (ttyConnector.init()) {
					terminalStarter.start();
				}
			} catch (Exception e) {
				log.error("Exception running terminal", e);
			} finally {
				try {
					ttyConnector.close();
				} catch (Exception e) {
				}
				sessionRunning.set(false);
			}
		}
	}

	@Override
	public TerminalStarter getTerminalStarter() {
		return terminalStarter;
	}
}
