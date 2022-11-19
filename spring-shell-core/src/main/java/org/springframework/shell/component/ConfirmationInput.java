/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.shell.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.shell.component.ConfirmationInput.ConfirmationInputContext;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.support.AbstractTextComponent;
import org.springframework.shell.component.support.AbstractTextComponent.TextComponentContext;
import org.springframework.shell.component.support.AbstractTextComponent.TextComponentContext.MessageLevel;
import org.springframework.util.StringUtils;

/**
 * Component for a confirmation question.
 *
 * @author Janne Valkealahti
 */
public class ConfirmationInput extends AbstractTextComponent<Boolean, ConfirmationInputContext> {

	private final static Logger log = LoggerFactory.getLogger(ConfirmationInput.class);
	private final boolean defaultValue;
	private ConfirmationInputContext currentContext;

	public ConfirmationInput(Terminal terminal) {
		this(terminal, null);
	}

	public ConfirmationInput(Terminal terminal, String name) {
		this(terminal, name, true, null);
	}

	public ConfirmationInput(Terminal terminal, String name, boolean defaultValue) {
		this(terminal, name, defaultValue, null);
	}

	public ConfirmationInput(Terminal terminal, String name, boolean defaultValue,
			Function<ConfirmationInputContext, List<AttributedString>> renderer) {
		super(terminal, name, null);
		setRenderer(renderer != null ? renderer : new DefaultRenderer());
		setTemplateLocation("classpath:org/springframework/shell/component/confirmation-input-default.stg");
		this.defaultValue = defaultValue;
	}

	@Override
	public ConfirmationInputContext getThisContext(ComponentContext<?> context) {
		if (context != null && currentContext == context) {
			return currentContext;
		}
		currentContext = ConfirmationInputContext.of(defaultValue);
		currentContext.setName(getName());
		if (context != null) {
			context.stream().forEach(e -> {
				currentContext.put(e.getKey(), e.getValue());
			});
		}
		return currentContext;
	}

	@Override
	protected boolean read(BindingReader bindingReader, KeyMap<String> keyMap, ConfirmationInputContext context) {
		String operation = bindingReader.readBinding(keyMap);
		log.debug("Binding read result {}", operation);
		if (operation == null) {
			return true;
		}
		String input;
		switch (operation) {
			case OPERATION_CHAR:
				String lastBinding = bindingReader.getLastBinding();
				input = context.getInput();
				if (input == null) {
					input = lastBinding;
				}
				else {
					input = input + lastBinding;
				}
				context.setInput(input);
				checkInput(input, context);
				break;
			case OPERATION_BACKSPACE:
				input = context.getInput();
				if (StringUtils.hasLength(input)) {
					input = input.length() > 1 ? input.substring(0, input.length() - 1) : null;
				}
				context.setInput(input);
				checkInput(input, context);
				break;
			case OPERATION_EXIT:
				if (StringUtils.hasText(context.getInput())) {
					context.setResultValue(parseBoolean(context.getInput()));
				}
				else if (context.getDefaultValue() != null) {
					context.setResultValue(context.getDefaultValue());
				}
				return true;
			default:
				break;
		}
		return false;
	}

	private Boolean parseBoolean(String input) {
		if (!StringUtils.hasText(input)) {
			return null;
		}
		input = input.trim().toLowerCase();
		switch (input) {
			case "y":
			case "yes":
			case "1":
				return true;
			case "n":
			case "no":
			case "0":
				return false;
			default:
				return null;
		}
	}

	private void checkInput(String input, ConfirmationInputContext context) {
		if (!StringUtils.hasText(input)) {
			context.setMessage(null);
			return;
		}
		Boolean yesno =  parseBoolean(input);
		if (yesno == null) {
			String msg = String.format("Sorry, your input is invalid: '%s', try again", input);
			context.setMessage(msg, MessageLevel.ERROR);
		}
		else {
			context.setMessage(null);
		}
	}

	public interface ConfirmationInputContext extends TextComponentContext<Boolean, ConfirmationInputContext> {

		/**
		 * Gets a default value.
		 *
		 * @return a default value
		 */
		Boolean getDefaultValue();

		/**
		 * Sets a default value.
		 *
		 * @param defaultValue the default value
		 */
		void setDefaultValue(Boolean defaultValue);

		/**
		 * Gets an empty {@link ConfirmationInputContext}.
		 *
		 * @return empty path input context
		 */
		public static ConfirmationInputContext empty() {
			return of(null);
		}

		/**
		 * Gets an {@link ConfirmationInputContext}.
		 *
		 * @return path input context
		 */
		public static ConfirmationInputContext of(Boolean defaultValue) {
			return new DefaultConfirmationInputContext(defaultValue);
		}
	}

	private static class DefaultConfirmationInputContext extends BaseTextComponentContext<Boolean, ConfirmationInputContext>
			implements ConfirmationInputContext {

		private Boolean defaultValue;

		public DefaultConfirmationInputContext(Boolean defaultValue) {
			this.defaultValue = defaultValue;
		}

		@Override
		public Boolean getDefaultValue() {
			return defaultValue;
		}

		@Override
		public void setDefaultValue(Boolean defaultValue) {
			this.defaultValue = defaultValue;
		}

		@Override
		public Map<String, Object> toTemplateModel() {
			Map<String, Object> attributes = super.toTemplateModel();
			attributes.put("defaultValue", getDefaultValue() != null ? getDefaultValue() : null);
			Map<String, Object> model = new HashMap<>();
			model.put("model", attributes);
			return model;
		}
	}

	private class DefaultRenderer implements Function<ConfirmationInputContext, List<AttributedString>> {

		@Override
		public List<AttributedString> apply(ConfirmationInputContext context) {
			return renderTemplateResource(context.toTemplateModel());
		}
	}
}
