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
package org.springframework.shell.component.view.control;

import java.util.Arrays;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.shell.component.view.control.cell.ListCell;
import org.springframework.shell.component.view.event.KeyEvent;
import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.view.event.KeyHandler;
import org.springframework.shell.component.view.event.KeyHandler.KeyHandlerResult;
import org.springframework.shell.component.view.event.MouseEvent;
import org.springframework.shell.component.view.event.MouseHandler;
import org.springframework.shell.component.view.event.MouseHandler.MouseHandlerResult;
import org.springframework.shell.component.view.geom.Rectangle;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.component.view.screen.Screen.Writer;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class ListViewTests extends AbstractViewTests {

	@Test
	void hasBorder() {
		ListView<?> view = new ListView<>();
		view.setShowBorder(true);
		view.setRect(0, 0, 80, 24);
		view.draw(screen24x80);
		assertThat(forScreen(screen24x80)).hasBorder(0, 0, 80, 24);
	}

	@Test
	void arrowKeysMoveSelection() {
		ListView<String> view = new ListView<>();
		configure(view);
		view.setRect(0, 0, 80, 24);
		view.setItems(Arrays.asList("item1", "item2"));
		assertThat(ReflectionTestUtils.getField(view, "selected")).isEqualTo(-1);

		KeyEvent eventDown = KeyEvent.of(Key.CursorDown);
		KeyEvent eventUp = KeyEvent.of(Key.CursorUp);
		KeyHandlerResult result = view.getKeyHandler().handle(KeyHandler.argsOf(eventDown));
		assertThat(result).isNotNull().satisfies(r -> {
			assertThat(r.event()).isEqualTo(eventDown);
			assertThat(r.consumed()).isTrue();
			// assertThat(r.focus()).isEqualTo(view);
			// assertThat(r.capture()).isEqualTo(view);
		});
		assertThat(selected(view)).isEqualTo(0);
		result = view.getKeyHandler().handle(KeyHandler.argsOf(eventDown));
		assertThat(selected(view)).isEqualTo(1);
		result = view.getKeyHandler().handle(KeyHandler.argsOf(eventUp));
		assertThat(selected(view)).isEqualTo(0);
	}

	@Test
	void mouseWheelMoveSelection() {
		ListView<String> view = new ListView<>();
		configure(view);
		view.setRect(0, 0, 80, 24);
		view.setItems(Arrays.asList("item1", "item2"));
		assertThat(selected(view)).isEqualTo(-1);

		MouseEvent eventDown = mouseWheelDown(0, 0);
		MouseEvent eventUp = mouseWheelUp(0, 0);
		MouseHandlerResult result = view.getMouseHandler().handle(MouseHandler.argsOf(eventDown));
		assertThat(result).isNotNull().satisfies(r -> {
			assertThat(r.event()).isEqualTo(eventDown);
			// assertThat(r.consumed()).isFalse();
			// assertThat(r.focus()).isNull();
			assertThat(r.capture()).isEqualTo(view);
		});
		assertThat(selected(view)).isEqualTo(0);
		result = view.getMouseHandler().handle(MouseHandler.argsOf(eventDown));
		assertThat(selected(view)).isEqualTo(1);
		result = view.getMouseHandler().handle(MouseHandler.argsOf(eventUp));
		assertThat(selected(view)).isEqualTo(0);
	}

	static int selected(ListView<?> view) {
		return (int) ReflectionTestUtils.getField(view, "selected");
	}

	@Nested
	class Visual {

		@Test
		void customCellFactory() {
			ListView<String> view = new ListView<>();
			view.setShowBorder(true);
			view.setCellFactory(list -> new TestListCell());
			view.setRect(0, 0, 80, 24);
			view.setItems(Arrays.asList("item1"));
			view.draw(screen24x80);
			assertThat(forScreen(screen24x80)).hasHorizontalText("pre-item1-post", 0, 1, 16);
		}

		static class TestListCell extends ListCell<String> {

			@Override
			public void draw(Screen screen) {
				Rectangle rect = getRect();
				Writer writer = screen.writerBuilder().build();
				writer.text(String.format("pre-%s-post", getItem()), rect.x(), rect.y());
				writer.background(rect, getBackgroundColor());
			}
		}

	}

}
