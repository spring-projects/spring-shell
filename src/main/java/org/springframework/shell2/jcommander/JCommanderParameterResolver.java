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

}
