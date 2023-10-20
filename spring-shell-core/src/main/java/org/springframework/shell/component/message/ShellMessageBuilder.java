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

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.shell.component.view.control.View;
import org.springframework.shell.component.view.event.EventLoop;
import org.springframework.shell.component.view.event.KeyEvent;
import org.springframework.shell.component.view.event.MouseEvent;
import org.springframework.util.Assert;

/**
 * Shell spesific message builder.
 *
 * @param <T> the payload type.
 *
 * @author Janne Valkealahti
 */
public final class ShellMessageBuilder<T> {

	private final T payload;
	private final ShellMessageHeaderAccessor headerAccessor;
	@Nullable
	private final Message<T> originalMessage;


	private ShellMessageBuilder(T payload, @Nullable Message<T> originalMessage) {
		Assert.notNull(payload, "payload must not be null");
		this.payload = payload;
		this.originalMessage = originalMessage;
		this.headerAccessor = new ShellMessageHeaderAccessor(originalMessage);
		// if (originalMessage != null) {
		// 		this.modified = (!this.payload.equals(originalMessage.getPayload()));
		// }
	}

	/**
	 * Create a builder for a new {@link Message} instance with the provided payload.
	 *
	 * @param <T> The type of the payload.
	 * @param payload the payload for the new message
	 * @return A ShellMessageBuilder.
	 */
	public static <T> ShellMessageBuilder<T> withPayload(T payload) {
		return new ShellMessageBuilder<>(payload, null);
	}

	/**
	 * Create a {@code redraw} message.
	 *
	 * @return a redraw message
	 */
	public static Message<String> ofRedraw() {
		return new ShellMessageBuilder<>("redraw", null)
			.setEventType(EventLoop.Type.SYSTEM)
			.setPriority(0)
			.build();
	}

	/**
	 * Create a {@code interrupt} message.
	 *
	 * @return a interrupt message
	 */
	public static Message<String> ofInterrupt() {
		return new ShellMessageBuilder<>("int", null)
			.setEventType(EventLoop.Type.SYSTEM)
			.setPriority(0)
			.build();
	}

	/**
	 * Create a {@code signal} message.
	 *
	 * @return a signal message
	 */
	public static Message<String> ofSignal(String signal) {
		return new ShellMessageBuilder<>(signal, null)
			.setEventType(EventLoop.Type.SIGNAL)
			.setPriority(0)
			.build();
	}

	/**
	 * Create a message of a {@link KeyEvent}.
	 *
	 * @param event the event type
	 * @return a message with {@link KeyEvent} as a payload
	 */
	public static Message<KeyEvent> ofKeyEvent(KeyEvent event) {
		return new ShellMessageBuilder<>(event, null)
			.setEventType(EventLoop.Type.KEY)
			.build();
	}

	/**
	 * Create a message of a {@link MouseEvent}.
	 *
	 * @param event the event type
	 * @return a message with {@link MouseEvent} as a payload
	 */
	public static Message<MouseEvent> ofMouseEvent(MouseEvent event) {
		return new ShellMessageBuilder<>(event, null)
			.setEventType(EventLoop.Type.MOUSE)
			.build();
	}

	public static Message<?> ofView(View view, Object args) {
		return new ShellMessageBuilder<>(args, null)
			.setEventType(EventLoop.Type.VIEW)
			.setView(view)
			.build();
	}

	public static Message<String> ofViewFocus(String action, View view) {
		return new ShellMessageBuilder<>(action, null)
			.setEventType(EventLoop.Type.SYSTEM)
			.setView(view)
			.build();
	}

	public ShellMessageBuilder<T> setPriority(Integer priority) {
		return setHeader(ShellMessageHeaderAccessor.PRIORITY, priority);
	}

	public ShellMessageBuilder<T> setView(View view) {
		return setHeader(ShellMessageHeaderAccessor.VIEW, view);
	}

	public ShellMessageBuilder<T> setEventType(EventLoop.Type type) {
		return setHeader(ShellMessageHeaderAccessor.EVENT_TYPE, type);
	}

	public ShellMessageBuilder<T> setHeader(String headerName, @Nullable Object headerValue) {
		this.headerAccessor.setHeader(headerName, headerValue);
		return this;
	}

	public Message<T> build() {
		return new GenericMessage<>(this.payload, this.headerAccessor.toMap());
	}

}
