/*
 * Copyright 2023 the original author or authors.
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
package org.springframework.shell;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.annotation.ReflectiveProcessor;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * A {@link ReflectiveProcessor} implementation that registers methods of a
 * return type {@link Availability} from a target which is a class.
 *
 * @author Janne Valkealahti
 */
public final class AvailabilityReflectiveProcessor implements ReflectiveProcessor {

	@Override
	public void registerReflectionHints(ReflectionHints hints, AnnotatedElement element) {
		if (element instanceof Class<?> type) {
			ReflectionUtils.doWithMethods(type, method -> {
				registerMethodHint(hints, method);
			}, mf -> {
				return ClassUtils.isAssignable(mf.getReturnType(), Availability.class)
						&& mf.getDeclaringClass() != Object.class;
			});
		}
	}

	/**
	 * Register {@link ReflectionHints} against the specified {@link Method}.
	 *
	 * @param hints the reflection hints instance to use
	 * @param method the method to process
	 */
	protected void registerMethodHint(ReflectionHints hints, Method method) {
		hints.registerMethod(method, ExecutableMode.INVOKE);
	}
}
