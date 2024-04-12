/*
 * Copyright 2023-2024 the original author or authors.
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
package org.springframework.shell.component.view;

import java.io.IOError;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.Terminal.Signal;
import org.jline.utils.AttributedString;
import org.jline.utils.Display;
import org.jline.utils.InfoCmp.Capability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.lang.Nullable;
import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.view.control.View;
import org.springframework.shell.component.view.control.ViewService;
import org.springframework.shell.component.view.event.DefaultEventLoop;
import org.springframework.shell.component.view.event.EventLoop;
import org.springframework.shell.component.view.event.KeyBinder;
import org.springframework.shell.component.view.event.KeyEvent;
import org.springframework.shell.component.view.event.KeyHandler;
import org.springframework.shell.component.view.event.KeyHandler.KeyHandlerResult;
import org.springframework.shell.component.view.event.MouseEvent;
import org.springframework.shell.component.view.event.MouseHandler;
import org.springframework.shell.component.view.event.MouseHandler.MouseHandlerResult;
import org.springframework.shell.component.view.screen.DefaultScreen;
import org.springframework.shell.geom.Rectangle;
import org.springframework.shell.style.ThemeResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link TerminalUI} is a main component orchestrating terminal, eventloop,
 * key/mouse events and view structure to work together. In many ways it can
 * be think of being a "main application" when terminal ui is shown in
 * a screen.
 *
 * @author Janne Valkealahti
 */
public class TerminalUI implements ViewService {

	private final static Logger log = LoggerFactory.getLogger(TerminalUI.class);
	private final Terminal terminal;
	private final BindingReader bindingReader;
	private final KeyMap<Integer> keyMap = new KeyMap<>();
	private final DefaultScreen virtualDisplay = new DefaultScreen();
	private Display display;
	private Size size;
	private View rootView;
	private View modalView;
	private boolean fullScreen;
	private final KeyBinder keyBinder;
	private DefaultEventLoop eventLoop = new DefaultEventLoop();
	private View focus = null;
	private ThemeResolver themeResolver;
	private String themeName = "default";

	/**
	 * Constructs a handler with a given terminal.
	 *
	 * @param terminal the terminal
	 */
	public TerminalUI(Terminal terminal) {
		Assert.notNull(terminal, "terminal must be set");
		this.terminal = terminal;
		this.bindingReader = new BindingReader(terminal.reader());
		this.keyBinder = new KeyBinder(terminal);
	}

	@Override
	public View getModal() {
		return modalView;
	}

	@Override
	public void setModal(View view) {
		this.modalView = view;
	}

	/**
	 * Sets a root view.
	 *
	 * @param root the root view
	 * @param fullScreen if root view should request full screen
	 */
	public void setRoot(View root, boolean fullScreen) {
		setFocus(root);
		this.rootView = root;
		this.fullScreen = fullScreen;
	}

	/**
	 * Run and start execution loop. This method blocks until run loop exits.
	 */
	public void run() {
		bindKeyMap(keyMap);
		display = new Display(terminal, fullScreen);
		size = new Size();
		loop();
	}

	/**
	 * Gets an {@link EventLoop}.
	 *
	 * @return an event loop
	 */
	public EventLoop getEventLoop() {
		return eventLoop;
	}

	/**
	 * Redraw a whole screen. Essentially a message is dispatched to an event loop
	 * which is handled as soon as possible.
	 */
	public void redraw() {
		getEventLoop().dispatch(ShellMessageBuilder.ofRedraw());
	}

	/**
	 * Sets a {@link ThemeResolver}.
	 *
	 * @param themeResolver the theme resolver
	 */
	public void setThemeResolver(ThemeResolver themeResolver) {
		this.themeResolver = themeResolver;
	}

	/**
	 * Sets a {@link ThemeResolver}.
	 *
	 * @return a theme resolver
	 */
	public ThemeResolver getThemeResolver() {
		return themeResolver;
	}

	/**
	 * Sets a {@code theme name}.
	 *
	 * @param themeName the theme name
	 */
	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	/**
	 * Gets a {@code theme name}.
	 *
	 * @return a theme name
	 */
	public String getThemeName() {
		return themeName;
	}

	/**
	 * Gets a {@link ViewService}.
	 *
	 * @return a view service
	 */
	public ViewService getViewService() {
		return this;
	}

	/**
	 * Configure view for {@link EventLoop}, {@link ThemeResolver},
	 * {@code theme name} and {@link ViewService}.
	 *
	 * @param view the view to configure
	 */
	public void configure(View view) {
		view.init();
		view.setEventLoop(eventLoop);
		view.setThemeResolver(themeResolver);
		view.setThemeName(themeName);
		view.setViewService(getViewService());
	}

	@Override
	public void setFocus(@Nullable View view) {
		if (focus != null) {
			focus.focus(focus, false);
		}
		focus = view;
		if (focus != null) {
			focus.focus(focus, true);
		}
	}

	private void render(Rectangle rect) {
		if (rootView != null) {
			rootView.setRect(rect.x(), rect.y(), rect.width(), rect.height());
			rootView.draw(virtualDisplay);
		}
		if (modalView != null) {
			modalView.setLayer(1);
			modalView.setRect(rect.x(), rect.y(), rect.width(), rect.height());
			modalView.draw(virtualDisplay);
		}
	}

	private BiFunction<Terminal, View, Rectangle> fullScreenViewRect = (terminal, view) -> {
		Size s = terminal.getSize();
		return new Rectangle(0, 0, s.getColumns(), s.getRows());
	};

	private BiFunction<Terminal, View, Rectangle> nonfullScreenViewRect = (terminal, view) -> {
		Size s = terminal.getSize();
		Rectangle rect = view.getRect();
		if (!rect.isEmpty()) {
			return rect;
		}
		return new Rectangle(0, 0, s.getColumns(), 5);
	};

	/**
	 * Sets a view rect function for full screen mode. Default behaviour uses {@link Rectangle}
	 * area equal to terminal size.
	 *
	 * @param fullScreenViewRect the view rect function
	 */
	public void setFullScreenViewRect(BiFunction<Terminal, View, Rectangle> fullScreenViewRect) {
		Assert.notNull(fullScreenViewRect, "view rect function must be set");
		this.fullScreenViewRect = fullScreenViewRect;
	}

	/**
	 * Sets a view rect function for full screen mode. Default behaviour uses {@link Rectangle}
	 * from {@code rootView} if it's not empty, otherwise uses zero based area with
	 * width matching terminal columns and height matching 5.
	 *
	 * @param nonfullScreenViewRect the view rect function
	 */
	public void setNonfullScreenViewRect(BiFunction<Terminal, View, Rectangle> nonfullScreenViewRect) {
		Assert.notNull(nonfullScreenViewRect, "view rect function must be set");
		this.nonfullScreenViewRect = nonfullScreenViewRect;
	}

	private synchronized void display() {
		log.trace("display() start");
		size.copy(terminal.getSize());
		if (fullScreen) {
			display.clear();
			display.reset();
			display.resize(size.getRows(), size.getColumns());
			Rectangle rect = fullScreenViewRect.apply(terminal, rootView);
			rootView.setRect(rect.x(), rect.y(), rect.width(), rect.height());
			virtualDisplay.resize(size.getRows(), size.getColumns());
			virtualDisplay.setShowCursor(false);
			render(rect);
		}
		else {
			Rectangle rect = nonfullScreenViewRect.apply(terminal, rootView);
			display.reset();
			display.resize(size.getRows(), size.getColumns());
			virtualDisplay.resize(rect.height(), rect.width());
			virtualDisplay.setShowCursor(false);
			render(rect);
		}

		List<AttributedString> newLines = virtualDisplay.getScreenLines();

		int targetCursorPos = 0;
		if (virtualDisplay.isShowCursor()) {
			terminal.puts(Capability.cursor_normal);
			targetCursorPos = size.cursorPos(virtualDisplay.getCursorPosition().y(), virtualDisplay.getCursorPosition().x());
			log.debug("Display targetCursorPos {}", targetCursorPos);
		}
		else {
			terminal.puts(Capability.cursor_invisible);
		}
		display.update(newLines, targetCursorPos);
		log.trace("display() end");
	}

	private void dispatchWinch() {
		eventLoop.dispatch(ShellMessageBuilder.ofSignal("WINCH"));
	}

	private void registerEventHandling() {
		eventLoop.onDestroy(eventLoop.signalEvents()
			.subscribe(event -> {
				display();
			}));

		eventLoop.onDestroy(eventLoop.systemEvents()
			.subscribe(event -> {
				if ("redraw".equals(event)) {
					display();
				}
				else if ("int".equals(event)) {
					this.terminal.raise(Signal.INT);
				}
			}));

		eventLoop.onDestroy(eventLoop.keyEvents()
			.doOnNext(m -> {
				handleKeyEvent(m);
			})
			.subscribe());

		eventLoop.onDestroy(eventLoop.mouseEvents()
			.doOnNext(m -> {
				handleMouseEvent(m);
			})
			.subscribe());
	}

	private void handleKeyEvent(KeyEvent event) {
		log.trace("handleKeyEvent {}", event);

		if (rootView != null) {
			// if hotkeys consume, we're done
			KeyHandler handler = rootView.getHotKeyHandler();
			if (handler != null) {
				KeyHandlerResult result = handler.handle(KeyHandler.argsOf(event));
				if (result.consumed()) {
					if (result.focus() != null) {
						setFocus(result.focus());
					}
					return;
				}
			}
			// continue with one having focus
			if (rootView.hasFocus()) {
				handler = rootView.getKeyHandler();
				if (handler != null) {
					KeyHandlerResult result = handler.handle(KeyHandler.argsOf(event));
					if (result.focus() != null) {
						setFocus(result.focus());
					}
				}
			}
		}
	}

	private void handleMouseEvent(MouseEvent event) {
		log.trace("handleMouseEvent {}", event);
		View view = modalView != null ? modalView : rootView;
		if (view != null) {
			MouseHandler handler = view.getMouseHandler();
			if (handler != null) {
				MouseHandlerResult result = handler.handle(MouseHandler.argsOf(event));
				if (result.focus() != null) {
					setFocus(result.focus());
				}
			}
		}
	}

	private void loop() {
		Attributes attr = terminal.enterRawMode();
		registerEventHandling();

		terminal.handle(Signal.WINCH, signal -> {
			log.debug("Handling signal {}", signal);
			dispatchWinch();
		});

		try {
			if (fullScreen) {
				terminal.puts(Capability.enter_ca_mode);
			}
			terminal.puts(Capability.keypad_xmit);
			terminal.puts(Capability.cursor_invisible);
			terminal.trackMouse(Terminal.MouseTracking.Normal);
			terminal.writer().flush();
			size.copy(terminal.getSize());
			display.clear();
			display.reset();

			while (true) {
				display();
				boolean exit = read(bindingReader, keyMap);
				if (exit) {
					break;
				}
			}
		}
		finally {
			eventLoop.destroy();
			terminal.setAttributes(attr);
			log.debug("Setting cursor visible");
			terminal.puts(Capability.cursor_normal);
			if (fullScreen) {
				display.update(Collections.emptyList(), 0);
			}
			terminal.trackMouse(Terminal.MouseTracking.Off);
			if (fullScreen) {
				terminal.puts(Capability.exit_ca_mode);
			}
			terminal.puts(Capability.keypad_local);
			if (!fullScreen) {
				display.update(Collections.emptyList(), 0);
			}
		}
	}

	private void bindKeyMap(KeyMap<Integer> keyMap) {
		keyBinder.bindAll(keyMap);
	}

	private boolean read(BindingReader bindingReader, KeyMap<Integer> keyMap) {
        Thread readThread = Thread.currentThread();
		terminal.handle(Signal.INT, signal -> {
			log.debug("Handling signal {}", signal);
			readThread.interrupt();
		});

		Integer operation = null;
		try {
			operation = bindingReader.readBinding(keyMap);
			log.debug("Read got operation {}", operation);
        } catch (IOError e) {
            // Ignore Ctrl+C interrupts and just exit the loop
			log.trace("Read binding error {}", e);
		}
		if (operation == null) {
			return true;
		}

		if (operation == KeyEvent.Key.Char) {
			String lastBinding = bindingReader.getLastBinding();
			if (StringUtils.hasLength(lastBinding)) {
				dispatchKeyEvent(KeyEvent.of(lastBinding.charAt(0)));
			}
		}
		else if (operation == KeyEvent.Key.Unicode) {
			String lastBinding = bindingReader.getLastBinding();
			if (StringUtils.hasLength(lastBinding)) {
				dispatchKeyEvent(KeyEvent.of(lastBinding));
			}
		}
		else if (operation == KeyEvent.Key.Mouse) {
			mouseEvent();
		}
		else {
			dispatchKeyEvent(KeyEvent.of(operation));
		}

		return false;
	}

	private void dispatchKeyEvent(KeyEvent event) {
		log.debug("Dispatch key event: {}", event);
		eventLoop.dispatch(ShellMessageBuilder.ofKeyEvent(event));
	}

	private void dispatchMouse(MouseEvent event) {
		log.debug("Dispatch mouse event: {}", event);
		eventLoop.dispatch(ShellMessageBuilder.ofMouseEvent(event));
	}

    private void mouseEvent() {
		dispatchMouse(MouseEvent.of(terminal.readMouseEvent()));
    }
}
