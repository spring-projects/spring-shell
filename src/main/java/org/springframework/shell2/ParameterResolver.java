package org.springframework.shell2;

import java.util.List;

import org.springframework.core.MethodParameter;

/**
 * Created by ericbottard on 27/11/15.
 */
public interface ParameterResolver {

	boolean supports(MethodParameter parameter);

	public Object resolve(MethodParameter methodParameter, List<String> words);

}
