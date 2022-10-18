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
package org.springframework.shell.style;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jline.utils.AttributedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;
import org.stringtemplate.v4.misc.STMessage;

/**
 * Template executor which knows to use styling.
 *
 * @author Janne Valkealahti
 */
public class TemplateExecutor {

	private final static Logger log = LoggerFactory.getLogger(TemplateExecutor.class);
	private final static STErrorListener ERROR_LISTENER = new LoggingSTErrorListener();
	private final ThemeResolver themeResolver;
	private StringToStyleExpressionRenderer renderer1;
	private PartsTextRenderer renderer2;

	public TemplateExecutor(ThemeResolver themeResolver) {
		this.themeResolver = themeResolver;
		renderer1 = new StringToStyleExpressionRenderer(themeResolver);
		renderer2 = new PartsTextRenderer(themeResolver);
	}

	/**
	 * Render template with a given attributes.
	 *
	 * @param template the ST template
	 * @param attributes the ST template attributes
	 * @return a rendered template
	 */
	public AttributedString render(String template, Map<String, Object> attributes) {
		STGroup group = new STGroup();
		group.setListener(ERROR_LISTENER);
		group.registerRenderer(String.class, renderer1);
		group.registerRenderer(PartsText.class, renderer2);

		ST st = new ST(group, template);
		if (attributes != null) {
			attributes.entrySet().stream().forEach(e -> st.add(e.getKey(), e.getValue()));
		}
		String templateRendered = st.render();
		return themeResolver.evaluateExpression(templateRendered);
	}

	/**
	 * Render template group with a given attributes expecting to find instance
	 * named {@code main}.
	 *
	 * @param template   the ST template
	 * @param attributes the ST template attributes
	 * @return a rendered template
	 */
	public AttributedString renderGroup(String template, Map<String, Object> attributes) {
		STGroup group = new STGroupString(template);
		group.setListener(ERROR_LISTENER);
		group.registerRenderer(String.class, renderer1);
		group.registerRenderer(PartsText.class, renderer2);

		// define styled figures as dictionary
		Map<String, Object> figureDict = Stream.of(FigureSettings.tags())
			.collect(Collectors.toMap(tag -> tag, tag -> this.themeResolver.resolveFigureTag(tag)));
		group.defineDictionary("figures", figureDict);

		ST st = group.getInstanceOf("main");
		if (st == null) {
			throw new IllegalArgumentException("template instance 'main' not found from a group");
		}
		if (attributes != null) {
			attributes.entrySet().stream().forEach(e -> st.add(e.getKey(), e.getValue()));
		}
		String templateRendered = st.render();
		log.debug("Rendered template {}", templateRendered);
		return themeResolver.evaluateExpression(templateRendered);
	}

	private static class LoggingSTErrorListener implements STErrorListener {

		private final static Logger log = LoggerFactory.getLogger(LoggingSTErrorListener.class);

		@Override
		public void compileTimeError(STMessage msg) {
			log.debug("compileTimeError [{}]", msg);
		}

		@Override
		public void runTimeError(STMessage msg) {
			log.debug("runTimeError [{}]", msg);
		}

		@Override
		public void IOError(STMessage msg) {
			log.debug("IOError [{}]", msg);
		}

		@Override
		public void internalError(STMessage msg) {
			log.debug("internalError [{}]", msg);
		}
	}
}
