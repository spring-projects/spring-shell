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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import reactor.util.context.ContextView;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.shell.component.view.control.View;
import org.springframework.shell.component.view.event.EventLoop;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Adds standard shell Headers.
 *
 * @author Janne Valkealahti
 */
public class ShellMessageHeaderAccessor extends MessageHeaderAccessor {

	public static final String PRIORITY = "priority";

	public static final String VIEW = "view";

	/**
	 * Raw source message.
	 */
	public static final String REACTOR_CONTEXT = "reactorContext";

	/**
	 * Raw source message.
	 */
	public static final String EVENT_TYPE = "eventType";

	private static final BiFunction<String, String, String> TYPE_VERIFY_MESSAGE_FUNCTION =
			(name, trailer) -> "The '" + name + trailer;

	private Set<String> readOnlyHeaders = new HashSet<>();

	public ShellMessageHeaderAccessor(@Nullable Message<?> message) {
		super(message);
	}

	/**
	 * Specify a list of headers which should be considered as read only and prohibited
	 * from being populated in the message.
	 *
	 * @param readOnlyHeaders the list of headers for {@code readOnly} mode. Defaults to
	 * {@link org.springframework.messaging.MessageHeaders#ID} and
	 * {@link org.springframework.messaging.MessageHeaders#TIMESTAMP}.
	 * @see #isReadOnly(String)
	 */
	public void setReadOnlyHeaders(String... readOnlyHeaders) {
		Assert.noNullElements(readOnlyHeaders, "'readOnlyHeaders' must not be contain null items.");
		if (!ObjectUtils.isEmpty(readOnlyHeaders)) {
			this.readOnlyHeaders = new HashSet<>(Arrays.asList(readOnlyHeaders));
		}
	}

	@Nullable
	public Integer getPriority() {
		Number priority = getHeader(PRIORITY, Number.class);
		return (priority != null ? priority.intValue() : null);
	}

	@Nullable
	public View getView() {
		View view = getHeader(VIEW, View.class);
		return view;
	}

	/**
	 * Get a {@link ContextView} header if present.
	 *
	 * @return the {@link ContextView} header if present.
	 */
	@Nullable
	public ContextView getReactorContext() {
		return getHeader(REACTOR_CONTEXT, ContextView.class);
	}

	/**
	 * Get a {@link EventLoop.Type} header if present.
	 *
	 * @return the {@link EventLoop.Type} header if present.
	 */
	@Nullable
	public EventLoop.Type getEventType() {
		return getHeader(EVENT_TYPE, EventLoop.Type.class);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public <T> T getHeader(String key, Class<T> type) {
		Object value = getHeader(key);
		if (value == null) {
			return null;
		}
		if (!type.isAssignableFrom(value.getClass())) {
			throw new IllegalArgumentException("Incorrect type specified for header '" + key + "'. Expected [" + type
					+ "] but actual type is [" + value.getClass() + "]");
		}
		return (T) value;
	}

	@Override
	protected void verifyType(String headerName, Object headerValue) {
		if (headerName != null && headerValue != null) {
			super.verifyType(headerName, headerValue);
			if (ShellMessageHeaderAccessor.PRIORITY.equals(headerName)) {
				Assert.isTrue(Number.class.isAssignableFrom(headerValue.getClass()),
						TYPE_VERIFY_MESSAGE_FUNCTION.apply(headerName, "' header value must be a Number."));
			}
		}
	}

	@Override
	public boolean isReadOnly(String headerName) {
		return super.isReadOnly(headerName) || this.readOnlyHeaders.contains(headerName);
	}

	@Override
	public Map<String, Object> toMap() {
		if (ObjectUtils.isEmpty(this.readOnlyHeaders)) {
			return super.toMap();
		}
		else {
			Map<String, Object> headers = super.toMap();
			for (String header : this.readOnlyHeaders) {
				headers.remove(header);
			}
			return headers;
		}
	}
}
