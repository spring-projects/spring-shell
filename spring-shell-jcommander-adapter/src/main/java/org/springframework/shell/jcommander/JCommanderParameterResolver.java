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

package org.springframework.shell.jcommander;

import static org.springframework.shell.Utils.unCamelify;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.ParameterDescription;
import org.springframework.shell.ParameterResolver;
import org.springframework.shell.ValueResult;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;

/**
 * Provides integration with JCommander.
 *
 * @author Eric Bottard
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
			boolean hasAnnotation = Arrays.stream(field.getAnnotations())
					.map(Annotation::annotationType)
					.anyMatch(JCOMMANDER_ANNOTATIONS::contains);
			isSupported.compareAndSet(false, hasAnnotation);

		});

		ReflectionUtils.doWithMethods(parameterType, method -> {
			ReflectionUtils.makeAccessible(method);
			boolean hasAnnotation = Arrays.stream(method.getAnnotations())
					.map(Annotation::annotationType)
					.anyMatch(Parameter.class::equals);
			isSupported.compareAndSet(false, hasAnnotation);
		});
		return isSupported.get();
	}

	@Override
	public ValueResult resolve(MethodParameter methodParameter, List<String> words) {
		JCommander jCommander = createJCommander(methodParameter);
		jCommander.parse(words.toArray(new String[words.size()]));
		return new ValueResult(methodParameter, jCommander.getObjects().get(0));
	}

	private JCommander createJCommander(MethodParameter methodParameter) {
		Object pojo = BeanUtils.instantiateClass(methodParameter.getParameterType());
		JCommander jCommander = new JCommander(pojo);
		jCommander.setAcceptUnknownOptions(true);
		return jCommander;
	}

	@Override
	public Stream<ParameterDescription> describe(MethodParameter parameter) {
		JCommander jCommander = createJCommander(parameter);
		Stream<com.beust.jcommander.ParameterDescription> jCommanderDescriptions = streamAllJCommanderDescriptions(jCommander);
		return jCommanderDescriptions
			.map(j -> new ParameterDescription(parameter, unCamelify(j.getParameterized().getType().getSimpleName()))
				.keys(Arrays.asList(j.getParameter().names()))
				.help(j.getDescription())
				.mandatoryKey(!j.equals(jCommander.getMainParameter()))
				// Not ideal as this does not take reverse-conversion into account, but just toString()
				.defaultValue(j.getDefault() == null ? "" : String.valueOf(j.getDefault()))
			);
	}

	/**
	 * Return <em>all</em> JCommander parameter descriptions, including the "main" parameter if present.
	 */
	private Stream<com.beust.jcommander.ParameterDescription> streamAllJCommanderDescriptions(JCommander jCommander) {
		return Stream.concat(
				jCommander.getParameters().stream(),
				jCommander.getMainParameter() != null ? Stream.of(jCommander.getMainParameter()) : Stream.empty()
			);
	}

	@Override
	public List<CompletionProposal> complete(MethodParameter parameter, CompletionContext context) {
		JCommander jCommander = createJCommander(parameter);
		List<String> words = context.getWords();
		try {
			jCommander.parseWithoutValidation(words.toArray(new String[words.size()]));
		}
		catch (ParameterException ignored) {
			// Exception here certainly means current buffer is not parseable in full.
			// Better to bail out now.
			return Collections.emptyList();
		}
		return streamAllJCommanderDescriptions(jCommander)
			.filter(p -> !p.isAssigned())
			.flatMap(p -> Arrays.stream(p.getParameter().names()))
			.map(CompletionProposal::new)
			.collect(Collectors.toList());
	}
}
