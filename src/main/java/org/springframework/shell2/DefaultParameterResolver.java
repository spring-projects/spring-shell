package org.springframework.shell2;

import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

/**
 * Created by ericbottard on 09/12/15.
 */
class DefaultParameterResolver implements ParameterResolver {

	private final ConversionService conversionService;

	public DefaultParameterResolver(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public boolean supports(MethodParameter parameter) {
		return true;
	}

	@Override
	public Object resolve(MethodParameter methodParameter, List<String> words) {
		TypeDescriptor targetType = new TypeDescriptor(methodParameter);
		Object converted = conversionService.convert(words.get(methodParameter.getParameterIndex()), TypeDescriptor.valueOf(String.class), targetType);
		return converted;
	}
}
