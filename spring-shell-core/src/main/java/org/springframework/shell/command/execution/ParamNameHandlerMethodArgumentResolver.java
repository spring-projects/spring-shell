package org.springframework.shell.command.execution;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionService;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;

import java.util.HashMap;
import java.util.Map;

@Order(100)
public class ParamNameHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final Map<String, Object> paramValues = new HashMap<>();
    private final ConversionService conversionService;

    ParamNameHandlerMethodArgumentResolver(Map<String, Object> paramValues, ConversionService conversionService) {
        this.paramValues.putAll(paramValues);
        this.conversionService = conversionService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        String parameterName = parameter.getParameterName();
        if (parameterName == null) {
            return false;
        }
        Class<?> sourceType = paramValues.get(parameterName) != null ? paramValues.get(parameterName).getClass()
                : null;
        return paramValues.containsKey(parameterName) && conversionService
                .canConvert(sourceType, parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Message<?> message) {
        return conversionService.convert(paramValues.get(parameter.getParameterName()), parameter.getParameterType());
    }

}
