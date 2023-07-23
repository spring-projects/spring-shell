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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.shell.component.view.event.KeyEvent.Key;
import org.springframework.shell.component.view.event.KeyHandler.KeyHandlerResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class AppViewTests extends AbstractViewTests {

	@Nested
	class ItemPositions {

		@Test
		void simpleSetup() {
			BoxView menu = spy(new BoxView());
			BoxView main = spy(new BoxView());
			BoxView status = spy(new BoxView());
			AppView view = new AppView(main, menu, status);

			view.setRect(0, 0, 80, 24);
			view.draw(screen24x80);

			verify(menu).setRect(0, 0, 80, 1);
			verify(main).setRect(0, 1, 80, 22);
			verify(status).setRect(0, 23, 80, 1);
		}

		@Test
		void simpleSetupWithInnerBorder() {
			BoxView menu = spy(new BoxView());
			BoxView main = spy(new BoxView());
			main.setShowBorder(true);
			BoxView status = spy(new BoxView());
			AppView view = new AppView(main, menu, status);

			view.setRect(0, 0, 80, 24);
			view.draw(screen24x80);

			verify(menu).setRect(0, 0, 80, 1);
			verify(main).setRect(0, 1, 80, 22);
			verify(status).setRect(0, 23, 80, 1);
			assertThat(forScreen(screen24x80)).hasBorder(0, 1, 80, 22);
		}

	}

	@Nested
	class Events {

		@Test
		void shouldOfferKeysToMenuIfHavingFocus() {
			BoxView smenu = spy(new BoxView());
			BoxView smain = spy(new BoxView());
			BoxView sstatus = spy(new BoxView());
			AppView sview = new AppView(smain, smenu, sstatus);

			smenu.focus(smenu, true);

			KeyHandlerResult result = handleKey(sview, Key.CursorRight);
			assertThat(result).isNotNull().satisfies(r -> {
				assertThat(r.event()).isNotNull();
				assertThat(r.consumed()).isFalse();
				assertThat(r.focus()).isNull();
				assertThat(r.capture()).isNull();
			});

			verify(smain, never()).getKeyHandler();
			verify(smenu).getKeyHandler();
		}

		@Test
		void shouldOfferKeysToMainIfMenuHaveNoFocus() {
			BoxView smenu = spy(new BoxView());
			BoxView smain = spy(new BoxView());
			BoxView sstatus = spy(new BoxView());
			AppView sview = new AppView(smain, smenu, sstatus);

			KeyHandlerResult result = handleKey(sview, Key.CursorRight);
			assertThat(result).isNotNull().satisfies(r -> {
				assertThat(r.event()).isNotNull();
				assertThat(r.consumed()).isTrue();
				assertThat(r.focus()).isNull();
				assertThat(r.capture()).isNull();
			});

			verify(smain).getKeyHandler();
			verify(smenu, never()).getKeyHandler();
		}
	}

	@Nested
	class Visibility {

		@Test
		void menuAndStatusVisible() {
			BoxView menu = spy(new BoxView());
			BoxView main = spy(new BoxView());
			BoxView status = spy(new BoxView());
			AppView view = new AppView(main, menu, status);
			view.setMenuBarVisible(true);
			view.setStatusBarVisible(true);

			view.setRect(0, 0, 80, 24);
			view.draw(screen24x80);

			verify(menu).setRect(0, 0, 80, 1);
			verify(main).setRect(0, 1, 80, 22);
			verify(status).setRect(0, 23, 80, 1);
		}

		@Test
		void menuAndStatusInvisible() {
			BoxView menu = spy(new BoxView());
			BoxView main = spy(new BoxView());
			BoxView status = spy(new BoxView());
			AppView view = new AppView(main, menu, status);
			view.setMenuBarVisible(false);
			view.setStatusBarVisible(false);

			view.setRect(0, 0, 80, 24);
			view.draw(screen24x80);

			verify(menu, never()).setRect(anyInt(), anyInt(), anyInt(), anyInt());
			verify(main).setRect(0, 0, 80, 24);
			verify(status, never()).setRect(anyInt(), anyInt(), anyInt(), anyInt());
		}

		@Test
		void onlyMenuVisible() {
			BoxView menu = spy(new BoxView());
			BoxView main = spy(new BoxView());
			BoxView status = spy(new BoxView());
			AppView view = new AppView(main, menu, status);
			view.setMenuBarVisible(true);
			view.setStatusBarVisible(false);

			view.setRect(0, 0, 80, 24);
			view.draw(screen24x80);

			verify(menu).setRect(0, 0, 80, 1);
			verify(main).setRect(0, 1, 80, 23);
			verify(status, never()).setRect(anyInt(), anyInt(), anyInt(), anyInt());
		}

		@Test
		void onlyStatusVisible() {
			BoxView menu = spy(new BoxView());
			BoxView main = spy(new BoxView());
			BoxView status = spy(new BoxView());
			AppView view = new AppView(main, menu, status);
			view.setMenuBarVisible(false);
			view.setStatusBarVisible(true);

			view.setRect(0, 0, 80, 24);
			view.draw(screen24x80);

			verify(menu, never()).setRect(anyInt(), anyInt(), anyInt(), anyInt());
			verify(main).setRect(0, 0, 80, 23);
			verify(status).setRect(0, 23, 80, 1);
		}
	}

}
