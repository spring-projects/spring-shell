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
package org.springframework.shell.core.tui.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jspecify.annotations.Nullable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.shell.core.tui.component.StringInput.StringInputContext;
import org.springframework.shell.core.tui.component.context.ComponentContext;
import org.springframework.shell.core.tui.component.support.AbstractTextComponent;
import org.springframework.util.StringUtils;

/**
 * Component for a simple string input.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 */
public class StringInput extends AbstractTextComponent<String, StringInputContext> {

	private final static Log log = LogFactory.getLog(StringInput.class);

	private final @Nullable String defaultValue;

	private @Nullable StringInputContext currentContext;

	private @Nullable Character maskCharacter;

	private boolean required;

	public StringInput(Terminal terminal) {
		this(terminal, null, null, null, false);
	}

	public StringInput(Terminal terminal, @Nullable String name, @Nullable String defaultValue) {
		this(terminal, name, defaultValue, null, false);
	}

	public StringInput(Terminal terminal, @Nullable String name, @Nullable String defaultValue,
			@Nullable Function<StringInputContext, List<AttributedString>> renderer) {
		this(terminal, name, defaultValue, renderer, false);
	}

	public StringInput(Terminal terminal, @Nullable String name, @Nullable String defaultValue,
			@Nullable Function<StringInputContext, List<AttributedString>> renderer, boolean required) {
		super(terminal, name, null);
		setRenderer(renderer != null ? renderer : new DefaultRenderer());
		setTemplateLocation("classpath:org/springframework/shell/component/string-input-default.stg");
		this.defaultValue = defaultValue;
		this.required = required;
	}

	/**
	 * Sets a mask character for input and result value.
	 * @param maskCharacter a mask character
	 */
	public void setMaskCharacter(@Nullable Character maskCharacter) {
		this.maskCharacter = maskCharacter;
	}

	/**
	 * Sets a required flag to check that the result is not empty
	 * @param required if input is required
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	@Override
	public StringInputContext getThisContext(@Nullable ComponentContext<?> context) {
		if (context != null && currentContext == context) {
			return currentContext;
		}
		currentContext = StringInputContext.of(defaultValue, maskCharacter, required);
		currentContext.setName(getName());
		if (context != null) {
			context.stream().forEach(e -> {
				currentContext.put(e.getKey(), e.getValue());
			});
		}
		return currentContext;
	}

	@Override
	protected boolean read(BindingReader bindingReader, KeyMap<String> keyMap, StringInputContext context) {
		String operation = bindingReader.readBinding(keyMap);
		log.debug("Binding read result " + operation);
		if (operation == null) {
			return true;
		}
		String input;
		switch (operation) {
			case OPERATION_UNICODE:
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
				break;
			case OPERATION_BACKSPACE:
				input = context.getInput();
				if (StringUtils.hasLength(input)) {
					input = input.length() > 1 ? input.substring(0, input.length() - 1) : null;
				}
				if (input != null) {
					context.setInput(input);
				}
				break;
			case OPERATION_EXIT:
				if (StringUtils.hasText(context.getInput())) {
					context.setResultValue(context.getInput());
				}
				else if (context.getDefaultValue() != null) {
					context.setResultValue(context.getDefaultValue());
				}
				else if (required) {
					context.setMessage("This field is mandatory", TextComponentContext.MessageLevel.ERROR);
					break;
				}
				return true;
			default:
				break;
		}
		return false;
	}

	public interface StringInputContext extends TextComponentContext<String, StringInputContext> {

		/**
		 * Gets a default value.
		 * @return a default value
		 */
		@Nullable String getDefaultValue();

		/**
		 * Sets a default value.
		 * @param defaultValue the default value
		 */
		void setDefaultValue(@Nullable String defaultValue);

		/**
		 * Sets a mask character.
		 * @param maskCharacter the mask character
		 */
		void setMaskCharacter(Character maskCharacter);

		/**
		 * Gets a masked input.
		 * @return a masked input
		 */
		@Nullable String getMaskedInput();

		/**
		 * Gets a masked result value.
		 * @return masked result value
		 */
		@Nullable String getMaskedResultValue();

		/**
		 * Returns flag if there is a mask character defined.
		 * @return true if mask character defined, false otherwise
		 */
		boolean hasMaskCharacter();

		/**
		 * Gets a mask character.
		 * @return a mask character.
		 */
		@Nullable Character getMaskCharacter();

		/**
		 * Sets flag for mandatory input.
		 * @param required true if input is required
		 */
		void setRequired(boolean required);

		/**
		 * Returns flag if input is required.
		 * @return true if input is required, false otherwise
		 */
		boolean isRequired();

		/**
		 * Gets an empty {@link StringInputContext}.
		 * @return empty path input context
		 */
		public static StringInputContext empty() {
			return of(null, null, false);
		}

		/**
		 * Gets an {@link StringInputContext}.
		 * @return path input context
		 */
		public static StringInputContext of(@Nullable String defaultValue, @Nullable Character maskCharacter) {
			return of(defaultValue, maskCharacter, false);
		}

		/**
		 * Gets an {@link StringInputContext}.
		 * @return path input context
		 */
		public static StringInputContext of(@Nullable String defaultValue, @Nullable Character maskCharacter,
				boolean required) {
			return new DefaultStringInputContext(defaultValue, maskCharacter, required);
		}

	}

	private static class DefaultStringInputContext extends BaseTextComponentContext<String, StringInputContext>
			implements StringInputContext {

		private @Nullable String defaultValue;

		private @Nullable Character maskCharacter;

		private boolean required;

		public DefaultStringInputContext(@Nullable String defaultValue, @Nullable Character maskCharacter,
				boolean required) {
			this.defaultValue = defaultValue;
			this.maskCharacter = maskCharacter;
			this.required = required;
		}

		@Override
		public @Nullable String getDefaultValue() {
			return defaultValue;
		}

		@Override
		public void setDefaultValue(@Nullable String defaultValue) {
			this.defaultValue = defaultValue;
		}

		@Override
		public void setMaskCharacter(Character maskCharacter) {
			this.maskCharacter = maskCharacter;
		}

		@Override
		public void setRequired(boolean required) {
			this.required = required;
		}

		@Override
		public @Nullable String getMaskedInput() {
			return maybeMask(getInput());
		}

		@Override
		public @Nullable String getMaskedResultValue() {
			return maybeMask(getResultValue());
		}

		@Override
		public boolean hasMaskCharacter() {
			return maskCharacter != null;
		}

		@Override
		public @Nullable Character getMaskCharacter() {
			return maskCharacter;
		}

		@Override
		public boolean isRequired() {
			return required;
		}

		@Override
		public Map<String, @Nullable Object> toTemplateModel() {
			Map<String, @Nullable Object> attributes = super.toTemplateModel();
			attributes.put("defaultValue", getDefaultValue() != null ? getDefaultValue() : null);
			attributes.put("maskedInput", getMaskedInput());
			attributes.put("maskedResultValue", getMaskedResultValue());
			attributes.put("maskCharacter", getMaskCharacter());
			attributes.put("hasMaskCharacter", hasMaskCharacter());
			attributes.put("required", isRequired());
			Map<String, Object> model = new HashMap<>();
			model.put("model", attributes);
			return model;
		}

		private @Nullable String maybeMask(@Nullable String str) {
			if (StringUtils.hasLength(str) && maskCharacter != null) {
				return new String(new char[str.length()]).replace('\0', maskCharacter);
			}
			else {
				return str;
			}
		}

	}

	private class DefaultRenderer implements Function<StringInputContext, List<AttributedString>> {

		@Override
		public List<AttributedString> apply(StringInputContext context) {
			return renderTemplateResource(context.toTemplateModel());
		}

	}

}
