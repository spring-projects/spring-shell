/*
 * Copyright 2023 the original author or authors.
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
package org.springframework.shell.component.view.event;

import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp.Capability;

import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.util.Assert;

import static org.jline.keymap.KeyMap.alt;
import static org.jline.keymap.KeyMap.ctrl;
import static org.jline.keymap.KeyMap.del;
import static org.jline.keymap.KeyMap.key;
import static org.jline.keymap.KeyMap.translate;

public class KeyBinder {

	private final Terminal terminal;

	public KeyBinder(Terminal terminal) {
		Assert.notNull(terminal, "terminal must be set");
		this.terminal = terminal;
	}

	public void bindAll(KeyMap<Integer> keyMap) {

		keyMap.setUnicode(Key.Unicode);

		for (char i = 32; i < KeyMap.KEYMAP_LENGTH - 1; i++) {
			keyMap.bind(KeyEvent.Key.Char, Character.toString(i));
		}

		for (char i = KeyEvent.Key.a; i <= KeyEvent.Key.z; i++) {
			keyMap.bind(i | KeyEvent.KeyMask.AltMask, alt(i));
			keyMap.bind(i | KeyEvent.KeyMask.CtrlMask, ctrl(i));
		}

		keyMap.bind(KeyEvent.Key.q | KeyEvent.KeyMask.CtrlMask, ctrl('q'));

		keyMap.bind(KeyEvent.Key.Mouse, key(terminal, Capability.key_mouse));

		keyMap.bind(KeyEvent.Key.Enter, "\r");
		keyMap.bind(KeyEvent.Key.Backspace, del());
		keyMap.bind(KeyEvent.Key.Delete, key(terminal, Capability.key_dc));
		keyMap.bind(KeyEvent.Key.Tab, "\t");
		keyMap.bind(KeyEvent.Key.Backtab, key(terminal, Capability.key_btab));


		keyMap.bind(KeyEvent.Key.CursorLeft, key(terminal, Capability.key_left));
		keyMap.bind(KeyEvent.Key.CursorRight, key(terminal, Capability.key_right));
		keyMap.bind(KeyEvent.Key.CursorUp, key(terminal, Capability.key_up));
		keyMap.bind(KeyEvent.Key.CursorDown, key(terminal, Capability.key_down));

		keyMap.bind(KeyEvent.Key.f1, key(terminal, Capability.key_f1));
		keyMap.bind(KeyEvent.Key.f2, key(terminal, Capability.key_f2));
		keyMap.bind(KeyEvent.Key.f3, key(terminal, Capability.key_f3));
		keyMap.bind(KeyEvent.Key.f4, key(terminal, Capability.key_f4));
		keyMap.bind(KeyEvent.Key.f5, key(terminal, Capability.key_f5));
		keyMap.bind(KeyEvent.Key.f6, key(terminal, Capability.key_f6));
		keyMap.bind(KeyEvent.Key.f7, key(terminal, Capability.key_f7));
		keyMap.bind(KeyEvent.Key.f8, key(terminal, Capability.key_f8));
		keyMap.bind(KeyEvent.Key.f9, key(terminal, Capability.key_f9));
		keyMap.bind(KeyEvent.Key.f10, key(terminal, Capability.key_f10));

		keyMap.bind(KeyEvent.Key.CursorLeft | KeyEvent.KeyMask.AltMask, alt(key(terminal, Capability.key_left)));
		keyMap.bind(KeyEvent.Key.CursorRight | KeyEvent.KeyMask.AltMask, alt(key(terminal, Capability.key_right)));
		keyMap.bind(KeyEvent.Key.CursorUp | KeyEvent.KeyMask.AltMask, alt(key(terminal, Capability.key_up)));
		keyMap.bind(KeyEvent.Key.CursorDown | KeyEvent.KeyMask.AltMask, alt(key(terminal, Capability.key_down)));

		keyMap.bind(KeyEvent.Key.CursorLeft | KeyEvent.KeyMask.CtrlMask, translate("^[[1;5D"));
		keyMap.bind(KeyEvent.Key.CursorRight | KeyEvent.KeyMask.CtrlMask, translate("^[[1;5C"));
		keyMap.bind(KeyEvent.Key.CursorUp | KeyEvent.KeyMask.CtrlMask, translate("^[[1;5A"));
		keyMap.bind(KeyEvent.Key.CursorDown | KeyEvent.KeyMask.CtrlMask, translate("^[[1;5B"));

		keyMap.bind(KeyEvent.Key.CursorLeft | KeyEvent.KeyMask.ShiftMask, translate("^[[1;2D"));
		keyMap.bind(KeyEvent.Key.CursorRight | KeyEvent.KeyMask.ShiftMask, translate("^[[1;2C"));
		keyMap.bind(KeyEvent.Key.CursorUp | KeyEvent.KeyMask.ShiftMask, translate("^[[1;2A"));
		keyMap.bind(KeyEvent.Key.CursorDown | KeyEvent.KeyMask.ShiftMask, translate("^[[1;2B"));

	}

}
