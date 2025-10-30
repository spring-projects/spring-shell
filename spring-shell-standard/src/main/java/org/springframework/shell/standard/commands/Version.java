/*
 * Copyright 2022-present the original author or authors.
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
package org.springframework.shell.standard.commands;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.jline.utils.AttributedString;

import org.springframework.core.io.Resource;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.tui.style.TemplateExecutor;
import org.springframework.util.FileCopyUtils;

/**
 * Command to list version and other build related infos.
 *
 * @author Janne Valkealahti
 * @author Mahmoud Ben Hassine
 */
@ShellComponent
public class Version extends AbstractShellComponent {

	/**
	 * Marker interface used in auto-config.
	 */
	public interface Command {
	}

	private TemplateExecutor templateExecutor;
	private String template;

	public Version(TemplateExecutor templateExecutor) {
		this.templateExecutor = templateExecutor;
	}

	@ShellMethod(key = "version", value = "Show version info")
	public AttributedString version() {
		String templateResource = resourceAsString(getResourceLoader().getResource(template));

		Map<String, Object> attributes = new HashMap<>();
		return templateExecutor.render(templateResource, attributes);
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	private static String resourceAsString(Resource resource) {
		try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
			return FileCopyUtils.copyToString(reader);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
