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
package org.springframework.shell.test.jediterm.terminal;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jediterm authors
 */
public enum TerminalMode {
	Null,
	CursorKey {
		@Override
		public void setEnabled(Terminal terminal, boolean enabled) {
			terminal.setApplicationArrowKeys(enabled);
		}
	},
	ANSI,
	WideColumn {
		@Override
		public void setEnabled(Terminal terminal, boolean enabled) {
			// Skip resizing as it would require to resize parent container.
			// Other terminal emulators (iTerm2, Terminal.app, GNOME Terminal) ignore it too.
			terminal.clearScreen();
			terminal.resetScrollRegions();
		}
	},
	CursorVisible {
		@Override
		public void setEnabled(Terminal terminal, boolean enabled) {
			terminal.setCursorVisible(enabled);
		}
	},
	AlternateBuffer {
		@Override
		public void setEnabled(Terminal terminal, boolean enabled) {
			terminal.useAlternateBuffer(enabled);
		}
	},
	SmoothScroll,
	ReverseVideo,
	OriginMode {
		@Override
		public void setEnabled(Terminal terminal, boolean enabled) {
		}
	},
	AutoWrap {
		@Override
		public void setEnabled(Terminal terminal, boolean enabled) {
			//we do nothing just switching the mode
		}
	},
	AutoRepeatKeys,
	Interlace,
	Keypad {
		@Override
		public void setEnabled(Terminal terminal, boolean enabled) {
			terminal.setApplicationKeypad(enabled);
		}
	},
	StoreCursor {
		@Override
		public void setEnabled(Terminal terminal, boolean enabled) {
			if (enabled) {
				terminal.saveCursor();
			}
			else {
				terminal.restoreCursor();
			}
		}
	},
	CursorBlinking {
		@Override
		public void setEnabled(Terminal terminal, boolean enabled) {
			terminal.setBlinkingCursor(enabled);
		}
	},
	AllowWideColumn,
	ReverseWrapAround,
	AutoNewLine {
		@Override
		public void setEnabled(Terminal terminal, boolean enabled) {
			terminal.setAutoNewLine(enabled);
		}
	},
	KeyboardAction,
	InsertMode,
	SendReceive,
	EightBitInput, //Interpret "meta" key, sets eighth bit. (enables the eightBitInput resource).
							 // http://www.leonerd.org.uk/hacks/hints/xterm-8bit.html

	AltSendsEscape //See section Alt and Meta Keys in http://invisible-island.net/xterm/ctlseqs/ctlseqs.html
					{
						@Override
						public void setEnabled(Terminal terminal, boolean enabled) {
							terminal.setAltSendsEscape(enabled);
						}
					},

	// https://cirw.in/blog/bracketed-paste
	// http://www.xfree86.org/current/ctlseqs.html#Bracketed%20Paste%20Mode
	BracketedPasteMode {
		@Override
		public void setEnabled(Terminal terminal, boolean enabled) {
			terminal.setBracketedPasteMode(enabled);
		}
	}
	;

	private static final Logger LOG = LoggerFactory.getLogger(TerminalMode.class);

	public void setEnabled(Terminal terminal, boolean enabled) {
		LOG.warn("Mode " + name() + " is not implemented, setting to " + enabled);
	}
}