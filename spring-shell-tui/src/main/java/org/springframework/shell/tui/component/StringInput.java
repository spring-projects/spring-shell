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
package org.springframework.shell.tui.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.shell.tui.component.StringInput.StringInputContext;
import org.springframework.shell.tui.component.context.ComponentContext;
import org.springframework.shell.tui.component.support.AbstractTextComponent;
import org.springframework.util.StringUtils;

/**
 * Component for a simple string input.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 */
public class StringInput extends AbstractTextComponent<String, StringInputContext> {

	private final static Logger log = LoggerFactory.getLogger(StringInput.class);
	private final @Nullable String defaultValue;
	private @Nullable StringInputContext currentContext;
	private @Nullable Character maskCharacter;

	public StringInput(Terminal terminal) {
		this(terminal, null, null, null);
	}

	public StringInput(Terminal terminal, @Nullable String name, @Nullable String defaultValue) {
		this(terminal, name, defaultValue, null);
	}

	public StringInput(Terminal terminal, @Nullable String name, @Nullable String defaultValue,
					  @Nullable Function<StringInputContext, List<AttributedString>> renderer) {
		super(terminal, name, null);
		setRenderer(renderer != null ? renderer : new DefaultRenderer());
		setTemplateLocation("classpath:org/springframework/shell/component/string-input-default.stg");
		this.defaultValue = defaultValue;
	}

	/**
	 * Sets a mask character for input and result value.
	 *
	 * @param maskCharacter a mask character
	 */
	public void setMaskCharacter(@Nullable Character maskCharacter) {
		this.maskCharacter = maskCharacter;
	}

	@Override
	public StringInputContext getThisContext(@Nullable ComponentContext<?> context) {
		if (context != null && currentContext == context) {
			return currentContext;
		}
		currentContext = StringInputContext.of(defaultValue, maskCharacter);
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
		log.debug("Binding read result {}", operation);
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
				return true;
			default:
				break;
		}
		return false;
	}

	public interface StringInputContext extends TextComponentContext<String, StringInputContext> {

		/**
		 * Gets a default value.
		 *
		 * @return a default value
		 */
		@Nullable String getDefaultValue();

		/**
		 * Sets a default value.
		 *
		 * @param defaultValue the default value
		 */
		void setDefaultValue(@Nullable String defaultValue);

		/**
		 * Sets a mask character.
		 *
		 * @param maskCharacter the mask character
		 */
		void setMaskCharacter(Character maskCharacter);

		/**
		 * Gets a masked input.
		 *
		 * @return a masked input
		 */
		@Nullable String getMaskedInput();

		/**
		 * Gets a masked result value.
		 *
		 * @return masked result value
		 */
		@Nullable String getMaskedResultValue();

		/**
		 * Returns flag if there is a mask character defined.
		 *
		 * @return true if mask character defined, false otherwise
		 */
		boolean hasMaskCharacter();

		/**
		 * Gets a mask character.
		 *
		 * @return a mask character.
		 */
		@Nullable Character getMaskCharacter();

		/**
		 * Gets an empty {@link StringInputContext}.
		 *
		 * @return empty path input context
		 */
		public static StringInputContext empty() {
			return of(null, null);
		}

		/**
		 * Gets an {@link StringInputContext}.
		 *
		 * @return path input context
		 */
		public static StringInputContext of(@Nullable String defaultValue, @Nullable Character maskCharacter) {
			return new DefaultStringInputContext(defaultValue, maskCharacter);
		}
	}

	private static class DefaultStringInputContext extends BaseTextComponentContext<String, StringInputContext>
			implements StringInputContext {

		private @Nullable String defaultValue;
		private @Nullable Character maskCharacter;

		public DefaultStringInputContext(@Nullable String defaultValue, @Nullable Character maskCharacter) {
			this.defaultValue = defaultValue;
			this.maskCharacter = maskCharacter;
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
		public Map<String, @Nullable Object> toTemplateModel() {
			Map<String, @Nullable Object> attributes = super.toTemplateModel();
			attributes.put("defaultValue", getDefaultValue() != null ? getDefaultValue() : null);
			attributes.put("maskedInput", getMaskedInput());
			attributes.put("maskedResultValue", getMaskedResultValue());
			attributes.put("maskCharacter", getMaskCharacter());
			attributes.put("hasMaskCharacter", hasMaskCharacter());
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
