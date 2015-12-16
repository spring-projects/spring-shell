package org.springframework.shell2;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.util.ReflectionUtils.findMethod;

import java.lang.reflect.Method;

import org.junit.Test;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.support.DefaultConversionService;

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
		), is(true));
		assertThat(resolver.resolve(
				makeMethodParameter(method, 1),
				asList("--force --name --foo y".split(" "))
		), is("--foo"));
		assertThat(resolver.resolve(
				makeMethodParameter(method, 2),
				asList("--force --name --foo y".split(" "))
		), is("y"));
		assertThat(resolver.resolve(
				makeMethodParameter(method, 3),
				asList("--force --name --foo y".split(" "))
		), is("last"));

	}

	private MethodParameter makeMethodParameter(Method method, int parameterIndex) {
		MethodParameter methodParameter = new MethodParameter(method, parameterIndex);
		methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
		return methodParameter;
	}


}
