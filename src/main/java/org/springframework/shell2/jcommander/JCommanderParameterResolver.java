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

package org.springframework.shell2.jcommander;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;

import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.shell2.ParameterDescription;
import org.springframework.shell2.ParameterResolver;
import org.springframework.util.ReflectionUtils;

/**
 * Created by ericbottard on 15/12/15.
 */
public class JCommanderParameterResolver implements ParameterResolver {

	private static final Collection<Class<? extends Annotation>> JCOMMANDER_ANNOTATIONS =
			Arrays.asList(Parameter.class, DynamicParameter.class, ParametersDelegate.class);

	@Override
	public boolean supports(MethodParameter parameter) {
		AtomicBoolean isSupported = new AtomicBoolean(false);

		Class<?> parameterType = parameter.getParameterType();
		ReflectionUtils.doWithFields(parameterType, field -> {
			ReflectionUtils.makeAccessible(field);
			boolean hasAnnotation = Arrays.asList(field.getAnnotations())
					.stream()
					.map(Annotation::annotationType)
					.anyMatch(JCOMMANDER_ANNOTATIONS::contains);
			isSupported.compareAndSet(false, hasAnnotation);

		});

		ReflectionUtils.doWithMethods(parameterType, method -> {
			ReflectionUtils.makeAccessible(method);
			boolean hasAnnotation = Arrays.asList(method.getAnnotations())
					.stream()
					.map(Annotation::annotationType)
					.anyMatch(Parameter.class::equals);
			isSupported.compareAndSet(false, hasAnnotation);
		});
		return isSupported.get();
	}

	@Override
	public Object resolve(MethodParameter methodParameter, List<String> words) {
		Object pojo = BeanUtils.instantiateClass(methodParameter.getParameterType());
		JCommander jCommander = new JCommander();
		jCommander.addObject(pojo);
		jCommander.setAcceptUnknownOptions(true);
		jCommander.parse(words.toArray(new String[words.size()]));
		return pojo;
	}

	@Override
	public ParameterDescription describe(MethodParameter parameter) {
		throw new UnsupportedOperationException();
	}
}
