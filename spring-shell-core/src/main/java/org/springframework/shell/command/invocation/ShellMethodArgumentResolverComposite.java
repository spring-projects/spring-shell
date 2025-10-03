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
package org.springframework.shell.command.invocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;

/**
 * Resolves method parameters by delegating to a list of registered
 * {@link HandlerMethodArgumentResolver HandlerMethodArgumentResolvers}. Previously
 * resolved method parameters are cached for faster lookups.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 */
public class ShellMethodArgumentResolverComposite implements HandlerMethodArgumentResolver {

	private final List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>();

	private final Map<MethodParameter, HandlerMethodArgumentResolver> argumentResolverCache = new ConcurrentHashMap<>(
			256);

	/**
	 * Add the given {@link HandlerMethodArgumentResolver}.
	 */
	public ShellMethodArgumentResolverComposite addResolver(HandlerMethodArgumentResolver resolver) {
		this.argumentResolvers.add(resolver);
		AnnotationAwareOrderComparator.sort(this.argumentResolvers);
		return this;
	}

	/**
	 * Add the given {@link HandlerMethodArgumentResolver HandlerMethodArgumentResolvers}.
	 * @since 4.3
	 */
	public ShellMethodArgumentResolverComposite addResolvers(@Nullable HandlerMethodArgumentResolver... resolvers) {

		if (resolvers != null) {
			Collections.addAll(this.argumentResolvers, resolvers);
		}
		AnnotationAwareOrderComparator.sort(this.argumentResolvers);
		return this;
	}

	/**
	 * Add the given {@link HandlerMethodArgumentResolver HandlerMethodArgumentResolvers}.
	 */
	public ShellMethodArgumentResolverComposite addResolvers(
			@Nullable List<? extends HandlerMethodArgumentResolver> resolvers) {

		if (resolvers != null) {
			this.argumentResolvers.addAll(resolvers);
		}
		AnnotationAwareOrderComparator.sort(this.argumentResolvers);
		return this;
	}

	/**
	 * Return a read-only list with the contained resolvers, or an empty list.
	 */
	public List<HandlerMethodArgumentResolver> getResolvers() {
		return Collections.unmodifiableList(this.argumentResolvers);
	}

	/**
	 * Clear the list of configured resolvers and the resolver cache.
	 */
	public void clear() {
		this.argumentResolvers.clear();
		this.argumentResolverCache.clear();
	}

	/**
	 * Whether the given {@linkplain MethodParameter method parameter} is supported by any
	 * registered {@link HandlerMethodArgumentResolver}.
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return getArgumentResolver(parameter) != null;
	}

	/**
	 * Iterate over registered {@link HandlerMethodArgumentResolver
	 * HandlerMethodArgumentResolvers} and invoke the one that supports it.
	 * @throws IllegalArgumentException if no suitable argument resolver is found
	 */
	@Override
	@Nullable
	public Object resolveArgument(MethodParameter parameter, Message<?> message) throws Exception {
		HandlerMethodArgumentResolver resolver = getArgumentResolver(parameter);
		if (resolver == null) {
			throw new IllegalArgumentException("Unsupported parameter type [" + parameter.getParameterType().getName()
					+ "]. supportsParameter should be called first.");
		}
		return resolver.resolveArgument(parameter, message);
	}

	/**
	 * Find a registered {@link HandlerMethodArgumentResolver} that supports the given
	 * method parameter.
	 */
	@Nullable
	private HandlerMethodArgumentResolver getArgumentResolver(MethodParameter parameter) {
		HandlerMethodArgumentResolver result = this.argumentResolverCache.get(parameter);
		if (result == null) {
			for (HandlerMethodArgumentResolver resolver : this.argumentResolvers) {
				if (resolver.supportsParameter(parameter)) {
					result = resolver;
					this.argumentResolverCache.put(parameter, result);
					break;
				}
			}
		}
		return result;
	}

}
