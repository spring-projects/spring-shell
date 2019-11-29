/*
 * Copyright 2017 the original author or authors.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.shell.ResultHandler;

/**
 * A delegating {@link ResultHandler} that dispatches handling based on the type of the result.
 * <p>
 * If no direct match is found, the type hierarchy of the result is considered, including implemented interfaces.
 * Auto-populates the handler map based on Generics type declaration of each discovered {@link ResultHandler} in the
 * ApplicationContext.
 * </p>
 *
 * @author Eric Bottard
 */
public class TypeHierarchyResultHandler implements ResultHandler<Object> {

	private Map<Class<?>, ResultHandler<?>> resultHandlers = new HashMap<>();

	@Override
	@SuppressWarnings("unchecked")
	public void handleResult(Object result) {
		if (result == null) { // void methods
			return;
		}
		Class<?> clazz = result.getClass();
		ResultHandler handler = getResultHandler(clazz);
		handler.handleResult(result);
	}

	private ResultHandler getResultHandler(Class<?> clazz) {
		ResultHandler handler = resultHandlers.get(clazz);
		if (handler != null) {
			return handler;
		}
		else {
			for (Class type : clazz.getInterfaces()) {
				handler = getResultHandler(type);
				if (handler != null) {
					return handler;
				}
			}
			return clazz.getSuperclass() != null ? getResultHandler(clazz.getSuperclass()) : null;
		}
	}

	@Autowired
	public void setResultHandlers(Set<ResultHandler<?>> resultHandlers) {
		for (ResultHandler<?> resultHandler : resultHandlers) {
			ResolvableType type = ResolvableType.forInstance(resultHandler).as(ResultHandler.class);
			registerHandler(type.resolveGeneric(0), resultHandler);
		}
	}

	private void registerHandler(Class<?> type, ResultHandler<?> resultHandler) {
		ResultHandler<?> previous = this.resultHandlers.put(type, resultHandler);
		if (previous != null) {
			throw new IllegalArgumentException(String.format("Multiple ResultHandlers configured for %s: both %s and %s", type, previous, resultHandler));
		}
	}

}
