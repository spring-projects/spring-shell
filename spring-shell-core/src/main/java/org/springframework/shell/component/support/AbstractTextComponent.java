/*
 * Copyright 2022-2024 the original author or authors.
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
package org.springframework.shell.component.support;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.InfoCmp.Capability;

import org.springframework.shell.component.context.BaseComponentContext;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.support.AbstractTextComponent.TextComponentContext;

import static org.jline.keymap.KeyMap.del;
import static org.jline.keymap.KeyMap.key;

/**
 * Base class for components which work on a simple text input.
 *
 * @author Janne Valkealahti
 */
public abstract class AbstractTextComponent<T, C extends TextComponentContext<T, C>> extends AbstractComponent<C> {

	private final String name;

	public AbstractTextComponent(Terminal terminal) {
		this(terminal, null);
	}

	public AbstractTextComponent(Terminal terminal, String name) {
		this(terminal, name, null);
	}

	public AbstractTextComponent(Terminal terminal, String name, Function<C, List<AttributedString>> renderer) {
		super(terminal);
		this.name = name;
		setRenderer(renderer);
	}

	@Override
	protected void bindKeyMap(KeyMap<String> keyMap) {
		keyMap.bind(OPERATION_EXIT, "\r");
		keyMap.bind(OPERATION_BACKSPACE, del(), key(getTerminal(), Capability.key_backspace));
		// skip 127 - DEL
		for (char i = 32; i < KeyMap.KEYMAP_LENGTH - 1; i++) {
			keyMap.bind(OPERATION_CHAR, Character.toString(i));
		}
		keyMap.setUnicode(OPERATION_UNICODE);
	}

	@Override
	protected C runInternal(C context) {
		// if there's no tty don't try to loop as it would then cause user interaction
		if (hasTty()) {
			loop(context);
		}
		return context;
	}

	/**
	 * Gets a name.
	 *
	 * @return a name
	 */
	protected String getName() {
		return name;
	}

	public interface TextComponentContext<T, C extends TextComponentContext<T, C>> extends ComponentContext<C> {

		/**
		 * Gets a name.
		 *
		 * @return a name
		 */
		String getName();

		/**
		 * Sets a name.
		 *
		 * @param name the name
		 */
		void setName(String name);

		/**
		 * Gets an input.
		 *
		 * @return an input
		 */
		String getInput();

		/**
		 * Sets an input.
		 *
		 * @param input the input
		 */
		void setInput(String input);

		/**
		 * Sets a result value.
		 *
		 * @return a result value
		 */
		T getResultValue();

		/**
		 * Sets a result value.
		 *
		 * @param resultValue the result value
		 */
		void setResultValue(T resultValue);

		/**
		 * Sets a message.
		 *
		 * @return a message
		 */
		String getMessage();

		/**
		 * Sets a message.
		 *
		 * @param message the message
		 */
		void setMessage(String message);

		/**
		 * Sets a message with level.
		 *
		 * @param message the message
		 * @param level the message level
		 */
		void setMessage(String message, MessageLevel level);

		/**
		 * Gets a {@link MessageLevel}.
		 *
		 * @return a message level
		 */
		MessageLevel getMessageLevel();

		/**
		 * Sets a {@link MessageLevel}.
		 *
		 * @param level the message level
		 */
		void setMessageLevel(MessageLevel level);

		/**
		 * Message levels which can be used to alter how message is shown.
		 */
		public enum MessageLevel {
			INFO,
			WARN,
			ERROR
		}
	}

	public static class BaseTextComponentContext<T, C extends TextComponentContext<T, C>> extends BaseComponentContext<C>
			implements TextComponentContext<T, C> {

		private String name;
		private String input;
		private T resultValue;
		private String message;
		private MessageLevel messageLevel = MessageLevel.INFO;

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String getInput() {
			return input;
		}

		@Override
		public void setInput(String input) {
			this.input = input;
		}

		@Override
		public T getResultValue() {
			return resultValue;
		}

		@Override
		public void setResultValue(T resultValue) {
			this.resultValue = resultValue;
		}

		@Override
		public String getMessage() {
			return message;
		}

		@Override
		public void setMessage(String message) {
			this.message = message;
		}

		@Override
		public void setMessage(String message, MessageLevel level) {
			setMessage(message);
			setMessageLevel(level);
		}

		@Override
		public MessageLevel getMessageLevel() {
			return messageLevel;
		}

		@Override
		public void setMessageLevel(MessageLevel messageLevel) {
			this.messageLevel = messageLevel;
		}

		@Override
		public Map<String, Object> toTemplateModel() {
			Map<String, Object> attributes = super.toTemplateModel();
			attributes.put("resultValue", getResultValue() != null ? getResultValue().toString() : null);
			attributes.put("name", getName());
			attributes.put("message", getMessage());
			attributes.put("messageLevel", getMessageLevel());
			attributes.put("hasMessageLevelInfo", getMessageLevel() == MessageLevel.INFO);
			attributes.put("hasMessageLevelWarn", getMessageLevel() == MessageLevel.WARN);
			attributes.put("hasMessageLevelError", getMessageLevel() == MessageLevel.ERROR);
			attributes.put("input", getInput());
			return attributes;
		}
	}
}
