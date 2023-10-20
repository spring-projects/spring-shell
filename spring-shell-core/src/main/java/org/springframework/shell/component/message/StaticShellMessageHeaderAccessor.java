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
package org.springframework.shell.component.message;

import java.util.UUID;

import reactor.util.context.Context;
import reactor.util.context.ContextView;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.shell.component.view.control.View;
import org.springframework.shell.component.view.event.EventLoop;

/**
 * Lightweight type-safe header accessor avoiding object creation just to access
 * a header.
 *
 * @author Janne Valkealahti
 *
 * @see ShellMessageHeaderAccessor
 */
public final class StaticShellMessageHeaderAccessor {

	private StaticShellMessageHeaderAccessor() {
	}

	@Nullable
	public static UUID getId(Message<?> message) {
		Object value = message.getHeaders().get(MessageHeaders.ID);
		if (value == null) {
			return null;
		}
		return (value instanceof UUID ? (UUID) value : UUID.fromString(value.toString()));
	}

	@Nullable
	public static Long getTimestamp(Message<?> message) {
		Object value = message.getHeaders().get(MessageHeaders.TIMESTAMP);
		if (value == null) {
			return null;
		}
		return (value instanceof Long ? (Long) value : Long.parseLong(value.toString()));
	}

	@Nullable
	public static Integer getPriority(Message<?> message) {
		Number priority = message.getHeaders().get(ShellMessageHeaderAccessor.PRIORITY, Number.class);
		return (priority != null ? priority.intValue() : null);
	}

	@Nullable
	public static View getView(Message<?> message) {
		View view = message.getHeaders().get(ShellMessageHeaderAccessor.VIEW, View.class);
		return view;
	}

	/**
	 * Get a {@link ContextView} header if present.
	 *
	 * @param message the message to get a header from.
	 * @return the {@link ContextView} header if present.
	 */
	public static ContextView getReactorContext(Message<?> message) {
		ContextView reactorContext = message.getHeaders()
				.get(ShellMessageHeaderAccessor.REACTOR_CONTEXT, ContextView.class);
		if (reactorContext == null) {
			reactorContext = Context.empty();
		}
		return reactorContext;
	}

	/**
	 * Get a {@link EventLoop.Type} header if present.
	 *
	 * @param message the message to get a header from.
	 * @return the {@link EventLoop.Type} header if present.
	 */
	public static EventLoop.Type getEventType(Message<?> message) {
		EventLoop.Type eventType = message.getHeaders()
				.get(ShellMessageHeaderAccessor.EVENT_TYPE, EventLoop.Type.class);
		return eventType;
	}
}
