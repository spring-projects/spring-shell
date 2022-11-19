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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import org.springframework.shell.component.PathInput.PathInputContext;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.support.AbstractTextComponent;
import org.springframework.shell.component.support.AbstractTextComponent.TextComponentContext;
import org.springframework.shell.component.support.AbstractTextComponent.TextComponentContext.MessageLevel;
import org.springframework.util.StringUtils;;

/**
 * Component for a simple path input.
 *
 * @author Janne Valkealahti
 */
public class PathInput extends AbstractTextComponent<Path, PathInputContext> {

	private final static Logger log = LoggerFactory.getLogger(PathInput.class);
	private PathInputContext currentContext;
	private Function<String, Path> pathProvider = (path) -> Paths.get(path);

	public PathInput(Terminal terminal) {
		this(terminal, null);
	}

	public PathInput(Terminal terminal, String name) {
		this(terminal, name, null);
	}

	public PathInput(Terminal terminal, String name, Function<PathInputContext, List<AttributedString>> renderer) {
		super(terminal, name, null);
		setRenderer(renderer != null ? renderer : new DefaultRenderer());
		setTemplateLocation("classpath:org/springframework/shell/component/path-input-default.stg");
	}

	@Override
	public PathInputContext getThisContext(ComponentContext<?> context) {
		if (context != null && currentContext == context) {
			return currentContext;
		}
		currentContext = PathInputContext.empty();
		currentContext.setName(getName());
		if (context != null) {
			context.stream().forEach(e -> {
				currentContext.put(e.getKey(), e.getValue());
			});
		}
		return currentContext;
	}

	@Override
	protected boolean read(BindingReader bindingReader, KeyMap<String> keyMap, PathInputContext context) {
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
				checkPath(input, context);
				break;
			case OPERATION_BACKSPACE:
				input = context.getInput();
				if (StringUtils.hasLength(input)) {
					input = input.length() > 1 ? input.substring(0, input.length() - 1) : null;
				}
				context.setInput(input);
				checkPath(input, context);
				break;
			case OPERATION_EXIT:
				if (StringUtils.hasText(context.getInput())) {
					context.setResultValue(Paths.get(context.getInput()));
				}
				return true;
			default:
				break;
		}
		return false;
	}

	/**
	 * Sets a path provider.
	 *
	 * @param pathProvider the path provider
	 */
	public void setPathProvider(Function<String, Path> pathProvider) {
		this.pathProvider = pathProvider;
	}

	/**
	 * Resolves a {@link Path} from a given raw {@code path}.
	 *
	 * @param path the raw path
	 * @return a resolved path
	 */
	protected Path resolvePath(String path) {
		return this.pathProvider.apply(path);
	}

	private void checkPath(String path, PathInputContext context) {
		if (!StringUtils.hasText(path)) {
			context.setMessage(null);
			return;
		}
		Path p = resolvePath(path);
		boolean isDirectory = Files.isDirectory(p);
		if (isDirectory) {
			context.setMessage("Directory exists", MessageLevel.ERROR);
		}
		else {
			context.setMessage("Path ok", MessageLevel.INFO);
		}
	}

	public interface PathInputContext extends TextComponentContext<Path, PathInputContext> {

		/**
		 * Gets an empty {@link PathInputContext}.
		 *
		 * @return empty path input context
		 */
		public static PathInputContext empty() {
			return new DefaultPathInputContext();
		}
	}

	private static class DefaultPathInputContext extends BaseTextComponentContext<Path, PathInputContext>
			implements PathInputContext {

		@Override
		public Map<String, Object> toTemplateModel() {
			Map<String, Object> attributes = super.toTemplateModel();
			Map<String, Object> model = new HashMap<>();
			model.put("model", attributes);
			return model;
		}
	}

	private class DefaultRenderer implements Function<PathInputContext, List<AttributedString>> {

		@Override
		public List<AttributedString> apply(PathInputContext context) {
			return renderTemplateResource(context.toTemplateModel());
		}
	}
}
