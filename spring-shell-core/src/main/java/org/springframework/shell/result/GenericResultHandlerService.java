/*
 * Copyright 2021 the original author or authors.
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
package org.springframework.shell.result;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.shell.ResultHandler;
import org.springframework.shell.ResultHandlerService;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Base {@ResultHandlerService} implementation suitable for use in most
 * environments.
 *
 * @author Janne Valkealahti
 */
public class GenericResultHandlerService implements ResultHandlerService {

	private final ResultHandlers resultHandlers = new ResultHandlers();

	@Override
	public void handle(Object source) {
		handle(source, TypeDescriptor.forObject(source));
	}

	@Override
	public void handle(Object result, TypeDescriptor resultType) {
		if (result == null) {
			return;
		}
		GenericResultHandler handler = getResultHandler(resultType);
		if (handler != null) {
			invokeHandler(handler, result, resultType);
			return;
		}
		handleResultHandlerNotFound(result, resultType);
	}

	/**
	 * Add a plain result handler to this registry.
	 *
	 * @param resultHandler the result handler
	 */
	public void addResultHandler(ResultHandler<?> resultHandler) {
		ResolvableType[] typeInfo = getRequiredTypeInfo(resultHandler.getClass(), ResultHandler.class);
		if (typeInfo == null) {
			throw new IllegalArgumentException("Unable to determine result type <T> for your " +
					"ResultHandler [" + resultHandler.getClass().getName() + "]; does the class parameterize those types?");
		}
		addResultHandler(new ResultHandlerAdapter(resultHandler, typeInfo[0]));
	}

	/**
	 * Add a plain result handler to this registry.
	 *
	 * @param <T> the type of result handler
	 * @param resultType the class of a result type
	 * @param resultHandler the result handler
	 */
	public <T> void addResultHandler(Class<T> resultType, ResultHandler<? super T> resultHandler) {
		addResultHandler(new ResultHandlerAdapter(resultHandler, ResolvableType.forClass(resultType)));
	}

	/**
	 * Add a generic result handler this this registry.
	 *
	 * @param handler the generic result handler
	 */
	public void addResultHandler(GenericResultHandler handler) {
		this.resultHandlers.add(handler);
	}

	private GenericResultHandler getResultHandler(TypeDescriptor resultType) {
		return this.resultHandlers.find(resultType);
	}

	@Nullable
	private Object handleResultHandlerNotFound(
			@Nullable Object source, @Nullable TypeDescriptor sourceType) {
		if (source == null) {
			return null;
		}
		if (sourceType == null) {
			return source;
		}
		throw new ResultHandlerNotFoundException(sourceType);
	}

	@Nullable
	private ResolvableType[] getRequiredTypeInfo(Class<?> handlerClass, Class<?> genericIfc) {
		ResolvableType resolvableType = ResolvableType.forClass(handlerClass).as(genericIfc);
		ResolvableType[] generics = resolvableType.getGenerics();
		if (generics.length < 1) {
			return null;
		}
		Class<?> resultType = generics[0].resolve();
		if (resultType == null) {
			return null;
		}
		return generics;
	}

	@SuppressWarnings("unchecked")
	private final static class ResultHandlerAdapter implements GenericResultHandler {

		ResultHandler<Object> handler;
		Class<?> result;

		public ResultHandlerAdapter(ResultHandler<?> handler, ResolvableType resultType) {
			this.handler = (ResultHandler<Object>) handler;
			this.result = resultType.toClass();
		}

		@Override
		public Set<Class<?>> getHandlerTypes() {
			return Collections.singleton(this.result);
		}

		@Override
		public void handle(Object result, TypeDescriptor resultType) {
			this.handler.handleResult(result);
		}

		@Override
		public boolean matches(TypeDescriptor resultType) {
			// always true until we create conditional handlers
			return true;
		}
	}

	/**
	 * Manages handlers registered with a specific {@link Class}.
	 */
	private static class ResultHandlersForType {

		private final Deque<GenericResultHandler> handlers = new ConcurrentLinkedDeque<>();

		public void add(GenericResultHandler handler) {
			this.handlers.addFirst(handler);
		}

		@Nullable
		public GenericResultHandler getHandler(TypeDescriptor resultType) {
			for (GenericResultHandler handler : this.handlers) {
				if (handler.matches(resultType)) {
					return handler;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return StringUtils.collectionToCommaDelimitedString(this.handlers);
		}
	}

	private static class ResultHandlers {

		private final Set<GenericResultHandler> globalHandlers = new CopyOnWriteArraySet<>();
		private final Map<Class<?>, ResultHandlersForType> handlers = new ConcurrentHashMap<>(16);

		public void add(GenericResultHandler handler) {
			Set<Class<?>> handlerTypes = handler.getHandlerTypes();
			if (handlerTypes == null) {
				this.globalHandlers.add(handler);
			}
			else {
				for (Class<?> handlerType : handlerTypes) {
					getMatchableConverters(handlerType).add(handler);
				}
			}
		}

		private ResultHandlersForType getMatchableConverters(Class<?> handlerType) {
			return this.handlers.computeIfAbsent(handlerType, k -> new ResultHandlersForType());
		}

		public GenericResultHandler find(TypeDescriptor resultType) {
			List<Class<?>> resultCandidates = getClassHierarchy(resultType.getType());
			for (Class<?> resultCandidate : resultCandidates) {
				GenericResultHandler handler = getRegisteredHandler(resultType, resultCandidate);
				if (handler != null) {
					return handler;
				}
			}
			return null;
		}

		@Nullable
		private GenericResultHandler getRegisteredHandler(TypeDescriptor resultType, Class<?> handlerType) {
			ResultHandlersForType resultHandlersForType = this.handlers.get(handlerType);
			if (resultHandlersForType != null) {
				GenericResultHandler handler = resultHandlersForType.getHandler(resultType);
				if (handler != null) {
					return handler;
				}
			}
			for (GenericResultHandler globalHandler : this.globalHandlers) {
				if (globalHandler.matches(resultType)) {
					return globalHandler;
				}
			}
			return null;
		}

		private List<Class<?>> getClassHierarchy(Class<?> type) {
			List<Class<?>> hierarchy = new ArrayList<>(20);
			Set<Class<?>> visited = new HashSet<>(20);
			addToClassHierarchy(0, ClassUtils.resolvePrimitiveIfNecessary(type), false, hierarchy, visited);
			boolean array = type.isArray();

			int i = 0;
			while (i < hierarchy.size()) {
				Class<?> candidate = hierarchy.get(i);
				candidate = (array ? candidate.getComponentType() : ClassUtils.resolvePrimitiveIfNecessary(candidate));
				Class<?> superclass = candidate.getSuperclass();
				if (superclass != null && superclass != Object.class && superclass != Enum.class) {
					addToClassHierarchy(i + 1, candidate.getSuperclass(), array, hierarchy, visited);
				}
				addInterfacesToClassHierarchy(candidate, array, hierarchy, visited);
				i++;
			}

			if (Enum.class.isAssignableFrom(type)) {
				addToClassHierarchy(hierarchy.size(), Enum.class, array, hierarchy, visited);
				addToClassHierarchy(hierarchy.size(), Enum.class, false, hierarchy, visited);
				addInterfacesToClassHierarchy(Enum.class, array, hierarchy, visited);
			}

			addToClassHierarchy(hierarchy.size(), Object.class, array, hierarchy, visited);
			addToClassHierarchy(hierarchy.size(), Object.class, false, hierarchy, visited);
			return hierarchy;
		}

		private void addInterfacesToClassHierarchy(Class<?> type, boolean asArray,
				List<Class<?>> hierarchy, Set<Class<?>> visited) {
			for (Class<?> implementedInterface : type.getInterfaces()) {
				addToClassHierarchy(hierarchy.size(), implementedInterface, asArray, hierarchy, visited);
			}
		}

		private void addToClassHierarchy(int index, Class<?> type, boolean asArray,
				List<Class<?>> hierarchy, Set<Class<?>> visited) {
			if (asArray) {
				type = Array.newInstance(type, 0).getClass();
			}
			if (visited.add(type)) {
				hierarchy.add(index, type);
			}
		}
	}

	private static void invokeHandler(GenericResultHandler handler, Object result, TypeDescriptor resultType) {
		handler.handle(result, resultType);;
	}
}
