package org.springframework.shell2;

import org.junit.Test;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.support.DefaultConversionService;

import java.lang.reflect.Method;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.ReflectionUtils.findMethod;

/**
 * Unit tests for DefaultParameterResolver.
 *
 * @author Eric Bottard
 * @author Florent Biville
 */
public class DefaultParameterResolverTest {


	private DefaultParameterResolver resolver = new DefaultParameterResolver(new DefaultConversionService());

	@Test
	public void testParses() throws Exception {
		Method method = findMethod(Remote.class, "zap", boolean.class, String.class, String.class, String.class);

		assertThat(resolver.resolve(
				makeMethodParameter(method, 0),
				asList("--force --name --foo y".split(" "))
		)).isEqualTo(true);
		assertThat(resolver.resolve(
				makeMethodParameter(method, 1),
				asList("--force --name --foo y".split(" "))
		)).isEqualTo("--foo");
		assertThat(resolver.resolve(
				makeMethodParameter(method, 2),
				asList("--force --name --foo y".split(" "))
		)).isEqualTo("y");
		assertThat(resolver.resolve(
				makeMethodParameter(method, 3),
				asList("--force --name --foo y".split(" "))
		)).isEqualTo("last");

	}

	private MethodParameter makeMethodParameter(Method method, int parameterIndex) {
		MethodParameter methodParameter = new MethodParameter(method, parameterIndex);
		methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
		return methodParameter;
	}


}
