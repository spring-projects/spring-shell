/*
 * Copyright 2023-present the original author or authors.
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
package org.springframework.shell.jline.tui.component.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.DumbTerminal;
import org.jline.utils.Display;
import org.junit.jupiter.api.Test;

import org.springframework.shell.jline.tui.component.view.control.BoxView;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Full screen redraw must preserve JLine frame diffing: an unchanged frame writes nothing
 * and a full rewrite happens only on resize. Frames are rendered by invoking the private
 * {@code display()} directly, inspecting the bytes written to the terminal.
 *
 * @author David Pilar
 */
class TerminalUiRedrawTests {

	private static final String MARKER = "REDRAW-MARKER";

	private static final int ROWS = 24;

	private static final int COLS = 80;

	@Test
	void fullScreenRedrawWithoutChangesDoesNotRewriteScreen() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Terminal terminal = dumbTerminal(out, COLS, ROWS);
		TerminalUI ui = fullScreenUi(terminal);

		int firstFrameBytes = renderFrame(ui, out);
		assertThat(out.toString(StandardCharsets.UTF_8)).contains(MARKER);

		// identical second frame: nothing changed
		out.reset();
		int secondFrameBytes = renderFrame(ui, out);

		assertThat(out.toString(StandardCharsets.UTF_8))
			.as("an unchanged frame must not re-emit screen content (frame diffing preserved)")
			.doesNotContain(MARKER);
		assertThat(secondFrameBytes)
			.as("an unchanged frame must write far fewer bytes than the initial draw; frame1=%d frame2=%d",
					firstFrameBytes, secondFrameBytes)
			.isLessThan(firstFrameBytes / 4);
	}

	@Test
	void fullScreenRedrawAfterResizeRewritesScreen() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Terminal terminal = dumbTerminal(out, COLS, ROWS);
		TerminalUI ui = fullScreenUi(terminal);

		renderFrame(ui, out);
		assertThat(out.toString(StandardCharsets.UTF_8)).contains(MARKER);

		// resize: next frame must redraw fully
		out.reset();
		terminal.setSize(new Size(COLS + 20, ROWS + 6));
		renderFrame(ui, out);

		assertThat(out.toString(StandardCharsets.UTF_8)).as("a frame following a resize must redraw the whole screen")
			.contains(MARKER);
	}

	private TerminalUI fullScreenUi(Terminal terminal) {
		TerminalUI ui = new TerminalUI(terminal);
		BoxView view = new BoxView();
		view.setShowBorder(true);
		view.setTitle(MARKER);
		ui.setRoot(view, true);
		// private state run()/loop() would set up
		ReflectionTestUtils.setField(ui, "display", new Display(terminal, true));
		ReflectionTestUtils.setField(ui, "size", new Size());
		return ui;
	}

	private int renderFrame(TerminalUI ui, ByteArrayOutputStream out) {
		ReflectionTestUtils.invokeMethod(ui, "display");
		return out.size();
	}

	private Terminal dumbTerminal(ByteArrayOutputStream out, int cols, int rows) throws Exception {
		Terminal terminal = new DumbTerminal("terminal", "ansi", new ByteArrayInputStream(new byte[0]), out,
				StandardCharsets.UTF_8);
		terminal.setSize(new Size(cols, rows));
		return terminal;
	}

}
