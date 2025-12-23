package org.springframework.shell.jline.tui.component;

import java.util.Map;

import org.jspecify.annotations.Nullable;

import org.springframework.shell.jline.tui.component.view.control.View;
import org.springframework.shell.jline.tui.component.view.event.EventLoop;

/**
 * @author Piotr Olaszewski
 */
public record TerminalEvent<T>(T payload, EventLoop.Type type, @Nullable View view,
		@Nullable Map<String, Object> attributes) {
	public TerminalEvent(T payload, EventLoop.Type type) {
		this(payload, type, null, null);
	}
}
