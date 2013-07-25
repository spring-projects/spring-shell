/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

/**
 * Utilities dealing with shell plugins.
 *
 * @author Erwin Vervaet
 */
public final class PluginUtils {

	private PluginUtils() {
	}

	/**
	 * Returns the highest priority {@link PluginProvider} of specified type defined in
	 * given application context.
	 *
	 * @since 1.0.1
	 */
	public static <T extends NamedProvider> T getHighestPriorityProvider(ApplicationContext applicationContext, Class<T> t) {
		Map<String, T> providers = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, t);
		List<T> sortedProviders = new ArrayList<T>(providers.values());
		Collections.sort(sortedProviders, new AnnotationAwareOrderComparator());
		T highestPriorityProvider = sortedProviders.get(0);
		return highestPriorityProvider;
	}
}
