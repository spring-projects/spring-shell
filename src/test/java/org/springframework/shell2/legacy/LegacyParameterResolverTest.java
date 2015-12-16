package org.springframework.shell2.legacy;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.shell.converters.BooleanConverter;
import org.springframework.shell.converters.EnumConverter;
import org.springframework.shell.converters.StringConverter;
import org.springframework.shell.core.Converter;
import org.springframework.shell2.ParameterResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LegacyParameterResolverTest.Config.class)
public class LegacyParameterResolverTest {

	private static final int NAME_OR_ANONYMOUS = 0;
	private static final int TYPE = 1;
	private static final int COORDINATES = 2;
	private static final int FORCE = 3;

	@Autowired
	ParameterResolver parameterResolver;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void supportsParameterAnnotatedWithCliOption() throws Exception {
		MethodParameter methodParameter = buildMethodParameter(LegacyCommands.REGISTER_METHOD, NAME_OR_ANONYMOUS);

		boolean supports = parameterResolver.supports(methodParameter);

		assertThat(supports, is(true));
	}

	private MethodParameter buildMethodParameter(Method method, int index) {
		MethodParameter methodParameter = new MethodParameter(method, index);
		methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
		return methodParameter;
	}

	@Test
	public void resolvesParameterAnnotatedWithCliOption() throws Exception {
		MethodParameter methodParameter = buildMethodParameter(LegacyCommands.REGISTER_METHOD, NAME_OR_ANONYMOUS);

		Object result = parameterResolver.resolve(methodParameter, asList("--foo bar --name baz --qix bux".split(" ")));

		assertThat(result, is("baz"));
	}

	@Test
	public void resolvesAnonymousParameterAnnotatedWithCliOption() throws Exception {
		MethodParameter methodParameter = buildMethodParameter(LegacyCommands.REGISTER_METHOD, NAME_OR_ANONYMOUS);

		Object result = parameterResolver.resolve(methodParameter, asList("--foo bar baz --qix bux".split(" ")));
		assertThat(result, is("baz"));

		// As first param
		result = parameterResolver.resolve(methodParameter, asList("baz --foo bar --qix bux".split(" ")));
		assertThat(result, is("baz"));
	}

	@Test
	public void usesLegacyConverters() throws Exception {
		MethodParameter methodParameter = buildMethodParameter(LegacyCommands.REGISTER_METHOD, TYPE);

		Object result = parameterResolver.resolve(methodParameter, asList("--foo bar --name baz --qix bux --type processor".split(" ")));

		assertThat(result, Matchers.is(ArtifactType.processor));
	}

	@Test
	public void testUnspecifiedDefaultValue() throws Exception {
		MethodParameter methodParameter = buildMethodParameter(LegacyCommands.REGISTER_METHOD, FORCE);

		Object result = parameterResolver.resolve(methodParameter, asList("--foo bar --name baz --qix bux".split(" ")));

		assertThat(result, is(false));
	}

	@Test
	public void testSpecifiedDefaultValue() throws Exception {
		MethodParameter methodParameter = buildMethodParameter(LegacyCommands.REGISTER_METHOD, FORCE);

		assertThat(parameterResolver.resolve(methodParameter, asList("--force --foo bar --name baz --qix bux".split(" "))), is(true));
		assertThat(parameterResolver.resolve(methodParameter, asList("--foo bar --name baz --qix bux --force".split(" "))), is(true));
	}

	@Test
	public void testParameterNotFound() throws Exception {
		MethodParameter methodParameter = buildMethodParameter(LegacyCommands.REGISTER_METHOD, COORDINATES);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Could not find parameter values for [--coordinates, --coords] in [--force, --foo, bar, --name, baz, --qix, bux]");
		parameterResolver.resolve(methodParameter, asList("--force --foo bar --name baz --qix bux".split(" ")));
	}

	@Test
	public void testParameterFoundTooManyTimes() throws Exception {
		MethodParameter methodParameter = buildMethodParameter(LegacyCommands.REGISTER_METHOD, COORDINATES);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Option --coordinates has already been set");
		// with the exact same name
		parameterResolver.resolve(methodParameter, asList("--force --coordinates bar --coordinates baz --qix bux".split(" ")));
		// or even with aliases
		parameterResolver.resolve(methodParameter, asList("--force --coordinates bar --coords baz --qix bux".split(" ")));
	}

	@Test
	public void testNoConverterFound() throws Exception {
		MethodParameter methodParameter = buildMethodParameter(LegacyCommands.SUM_METHOD, 0);

		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("No converter found for --v1 from '1' to type int");
		parameterResolver.resolve(methodParameter, asList("--v1 1 --v2 2".split(" ")));


	}

	@Test
	public void testNoConverterFoundForUnspecifiedValue() throws Exception {
		MethodParameter methodParameter = buildMethodParameter(LegacyCommands.SUM_METHOD, 0);

		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("No converter found for --v1 from '38' to type int");
		parameterResolver.resolve(methodParameter, asList("--v2 2".split(" ")));
	}

	@Test
	public void testNoConverterFoundForSpecifiedValue() throws Exception {
		MethodParameter methodParameter = buildMethodParameter(LegacyCommands.SUM_METHOD, 1);

		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("No converter found for --v2 from '42' to type int");
		parameterResolver.resolve(methodParameter, asList("--v1 1 --v2".split(" ")));
	}

	@Configuration
	static class Config {

		@Bean
		public Converter<String> stringConverter() {
			return new StringConverter();
		}

		@Bean
		public Converter<Boolean> booleanConverter() {
			return new BooleanConverter();
		}

		@Bean
		public Converter<Enum<?>> enumConverter() {
			return new EnumConverter();
		}

		@Bean
		public ParameterResolver parameterResolver() {
			return new LegacyParameterResolver();
		}
	}
}
