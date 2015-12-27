/*
 * Copyright 2015 the original author or authors.
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

package org.springframework.shell2.result;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A unique entry point delegating to the most appropriate {@link ResultHandler},
 * according to the type of result to handle.
 *
 * @author Eric Bottard
 */
@Component
public class ResultHandlers {

	private Map<Class<?>, ResultHandler<?>> resultHandlers = new HashMap<>();

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
			Type type = ((ParameterizedType) resultHandler.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
			this.resultHandlers.put((Class<?>) type, resultHandler);
		}
	}

}
