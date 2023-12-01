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
import java.util.Optional;
import java.util.function.Function;

import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.component.NumberInput.NumberInputContext;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.support.AbstractTextComponent;
import org.springframework.shell.component.support.AbstractTextComponent.TextComponentContext;
import org.springframework.shell.component.support.AbstractTextComponent.TextComponentContext.MessageLevel;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

/**
 * Component for a number input.
 *
 * @author Nicola Di Falco
 */
public class NumberInput extends AbstractTextComponent<Number, NumberInputContext> {

	private static final Logger log = LoggerFactory.getLogger(NumberInput.class);
	private final Number defaultValue;
	private Class<? extends Number> clazz;
	private boolean required;
	private NumberInputContext currentContext;

	public NumberInput(Terminal terminal) {
		this(terminal, null);
	}

	public NumberInput(Terminal terminal, String name) {
		this(terminal, name, null);
	}

	public NumberInput(Terminal terminal, String name, Number defaultValue) {
		this(terminal, name, defaultValue, Integer.class);
	}

	public NumberInput(Terminal terminal, String name, Number defaultValue, Class<? extends Number> clazz) {
		this(terminal, name, defaultValue, clazz, false);
	}

	public NumberInput(Terminal terminal, String name, Number defaultValue, Class<? extends Number> clazz, boolean required) {
		this(terminal, name, defaultValue, clazz, required, null);
	}

	public NumberInput(Terminal terminal, String name, Number defaultValue, Class<? extends Number> clazz, boolean required,
			Function<NumberInputContext, List<AttributedString>> renderer) {
		super(terminal, name, null);
		setRenderer(renderer != null ? renderer : new DefaultRenderer());
		setTemplateLocation("classpath:org/springframework/shell/component/number-input-default.stg");
		this.defaultValue = defaultValue;
		this.clazz = clazz;
		this.required = required;
	}

	public void setNumberClass(Class<? extends Number>  clazz) {
		this.clazz = clazz;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	@Override
	public NumberInputContext getThisContext(ComponentContext<?> context) {
		if (context != null && currentContext == context) {
			return currentContext;
		}
		currentContext = NumberInputContext.of(defaultValue, clazz, required);
		currentContext.setName(getName());
		Optional.ofNullable(context).map(ComponentContext::stream)
				.ifPresent(entryStream -> entryStream.forEach(e -> currentContext.put(e.getKey(), e.getValue())));
		return currentContext;
	}

	@Override
	protected boolean read(BindingReader bindingReader, KeyMap<String> keyMap, NumberInputContext context) {
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
				} else {
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
				Number num = parseNumber(context.getInput());

				if (num != null) {
					context.setResultValue(parseNumber(context.getInput()));
				} else if (StringUtils.hasText(context.getInput())) {
					printInvalidInput(context.getInput(), context);
					break;
				} else if (context.getDefaultValue() != null) {
					context.setResultValue(context.getDefaultValue());
				} else if (required) {
					context.setMessage("This field is mandatory", TextComponentContext.MessageLevel.ERROR);
					break;
				}
				return true;
			default:
				break;
		}
		return false;
	}

	private Number parseNumber(String input) {
		if (!StringUtils.hasText(input)) {
			return null;
		}

		try {
			return NumberUtils.parseNumber(input, clazz);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private void checkInput(String input, NumberInputContext context) {
		if (!StringUtils.hasText(input)) {
			context.setMessage(null);
			return;
		}
		Number num =  parseNumber(input);
		if (num == null) {
			printInvalidInput(input, context);
		}
		else {
			context.setMessage(null);
		}
	}

	private void printInvalidInput(String input, NumberInputContext context) {
		String msg = String.format("Sorry, your input is invalid: '%s', try again", input);
		context.setMessage(msg, MessageLevel.ERROR);
	}

	public interface NumberInputContext extends TextComponentContext<Number, NumberInputContext> {

		/**
		 * Gets a default value.
		 *
		 * @return a default value
		 */
		Number getDefaultValue();

		/**
		 * Sets a default value.
		 *
		 * @param defaultValue the default value
		 */
		void setDefaultValue(Number defaultValue);

		/**
		 * Gets a default number class.
		 *
		 * @return a default number class
		 */
		Class<? extends Number> getDefaultClass();

		/**
		 * Sets a default number class.
		 *
		 * @param defaultClass the default number class
		 */
		void setDefaultClass(Class<? extends Number> defaultClass);

		/**
		 * Sets flag for mandatory input.
		 *
		 * @param required true if input is required
		 */
		void setRequired(boolean required);

		/**
		 * Returns flag if input is required.
		 *
		 * @return true if input is required, false otherwise
		 */
		boolean isRequired();

		/**
		 * Gets an empty {@link NumberInputContext}.
		 *
		 * @return empty number input context
		 */
		public static NumberInputContext empty() {
			return of(null);
		}

		/**
		 * Gets an {@link NumberInputContext}.
		 *
		 * @return number input context
		 */
		public static NumberInputContext of(Number defaultValue) {
			return new DefaultNumberInputContext(defaultValue, Integer.class, false);
		}

		/**
		 * Gets an {@link NumberInputContext}.
		 *
		 * @return number input context
		 */
		public static NumberInputContext of(Number defaultValue, Class<? extends Number> defaultClass) {
			return new DefaultNumberInputContext(defaultValue, defaultClass, false);
		}

		/**
		 * Gets an {@link NumberInputContext}.
		 *
		 * @return number input context
		 */
		public static NumberInputContext of(Number defaultValue, Class<? extends Number> defaultClass, boolean required) {
			return new DefaultNumberInputContext(defaultValue, defaultClass, required);
		}
	}

	private static class DefaultNumberInputContext extends BaseTextComponentContext<Number, NumberInputContext> implements NumberInputContext {

		private Number defaultValue;
		private Class<? extends Number> defaultClass;
		private boolean required;

		public DefaultNumberInputContext(Number defaultValue, Class<? extends Number> defaultClass, boolean required) {
			this.defaultValue = defaultValue;
			this.defaultClass = defaultClass;
			this.required = required;
		}

		@Override
		public Number getDefaultValue() {
			return defaultValue;
		}

		@Override
		public void setDefaultValue(Number defaultValue) {
			this.defaultValue = defaultValue;
		}

		@Override
		public Class<? extends Number> getDefaultClass() {
			return defaultClass;
		}

		@Override
		public void setDefaultClass(Class<? extends Number> defaultClass) {
			this.defaultClass = defaultClass;
		}

		@Override
		public void setRequired(boolean required) {
			this.required = required;
		}

		@Override
		public boolean isRequired() {
			return required;
		}

		@Override
		public Map<String, Object> toTemplateModel() {
			Map<String, Object> attributes = super.toTemplateModel();
			attributes.put("defaultValue", getDefaultValue() != null ? getDefaultValue() : null);
			attributes.put("defaultClass", getDefaultClass().getSimpleName());
			attributes.put("required", isRequired());
			Map<String, Object> model = new HashMap<>();
			model.put("model", attributes);
			return model;
		}
	}

	private class DefaultRenderer implements Function<NumberInputContext, List<AttributedString>> {

		@Override
		public List<AttributedString> apply(NumberInputContext context) {
			return renderTemplateResource(context.toTemplateModel());
		}
	}
}
