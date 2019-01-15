/*
	* Copyright 2015 the original author or authors.
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

package org.springframework.shell.jcommander;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.BeanDescriptor;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.ParametersDelegate;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.ParameterDescription;
import org.springframework.shell.ParameterResolver;
import org.springframework.shell.ValueResult;
import org.springframework.util.ReflectionUtils;

import static org.springframework.shell.Utils.unCamelify;

/**
 * Provides integration with JCommander.
 *
 * @author Eric Bottard
 * @author Josh Long
 */
public class JCommanderParameterResolver implements ParameterResolver {

	private static final Collection<Class<? extends Annotation>> JCOMMANDER_ANNOTATIONS = Arrays.asList(Parameter.class,
			DynamicParameter.class, ParametersDelegate.class);

	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Autowired(required = false)
	public void setValidatorFactory(ValidatorFactory validatorFactory) {
		this.validator = validatorFactory.getValidator();
	}

	@Override
	public boolean supports(MethodParameter parameter) {
		AtomicBoolean isSupported = new AtomicBoolean(false);
		Class<?> parameterType = parameter.getParameterType();

		if (isLegalReflectiveAccess(parameterType)) {
			ReflectionUtils.doWithFields(parameterType, field -> {
				if (isLegalReflectiveAccess(field.getType())) {
					ReflectionUtils.makeAccessible(field);
					boolean hasAnnotation = Arrays.stream(field.getAnnotations())
							.map(Annotation::annotationType)
							.anyMatch(JCOMMANDER_ANNOTATIONS::contains);
					isSupported.compareAndSet(false, hasAnnotation);
				}
			});

			ReflectionUtils.doWithMethods(parameterType, method -> {
				if (isLegalReflectiveAccess(method.getDeclaringClass())) {
					ReflectionUtils.makeAccessible(method);
					boolean hasAnnotation = Arrays.stream(method.getAnnotations())
							.map(Annotation::annotationType)
							.anyMatch(Parameter.class::equals);
					isSupported.compareAndSet(false, hasAnnotation);
				}
			});
		}
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
		return new JCommander(pojo);
	}

	@Override
	public Stream<ParameterDescription> describe(MethodParameter parameter) {
		JCommander jCommander = createJCommander(parameter);
		Stream<com.beust.jcommander.ParameterDescription> jCommanderDescriptions = streamAllJCommanderDescriptions(
				jCommander);

		BeanDescriptor constraintsForClass = validator.getConstraintsForClass(parameter.getParameterType());

		return jCommanderDescriptions
				.map(j -> new ParameterDescription(parameter,
						unCamelify(j.getParameterized().getType().getSimpleName()))
								.keys(Arrays.asList(j.getParameter().names()))
								.help(j.getDescription())
								.mandatoryKey(!j.equals(jCommander.getMainParameterValue()))
								// Not ideal as this does not take reverse-conversion into account, but just toString()
								.defaultValue(j.getDefault() == null ? "" : String.valueOf(j.getDefault()))
								.elementDescriptor(
										constraintsForClass.getConstraintsForProperty(j.getParameterized().getName())));
	}

	/**
	 * Return <em>all</em> JCommander parameter descriptions, including the "main" parameter
	 * if present.
	 */
	private Stream<com.beust.jcommander.ParameterDescription> streamAllJCommanderDescriptions(JCommander jCommander) {
		return Stream.concat(
				jCommander.getParameters().stream(),
				jCommander.getMainParameterValue() != null ? Stream.of(jCommander.getMainParameterValue()) : Stream.empty());
	}

	// Java 9+ warn if you try to reflect on JDK types
	private static boolean isLegalReflectiveAccess(Class<?> clzz) {
		return (!clzz.getName().startsWith("java"));
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
