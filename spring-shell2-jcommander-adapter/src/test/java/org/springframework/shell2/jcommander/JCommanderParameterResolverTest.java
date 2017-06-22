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

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.Test;

import org.springframework.core.MethodParameter;
import org.springframework.shell2.Utils;
import org.springframework.util.ReflectionUtils;

/**
 * Unit test for {@link JCommanderParameterResolver}.
 *
 * @author Eric Bottard
 * @author Florent Biville
 */
public class JCommanderParameterResolverTest {

	private static final Method COMMAND_METHOD = ReflectionUtils.findMethod(MyLordCommands.class, "genesis", FieldCollins.class);

	private JCommanderParameterResolver resolver = new JCommanderParameterResolver();

	@Test
	public void testSupportsJCommanderPojos() throws Exception {
		assertThat(resolver.supports(Utils.createMethodParameter(COMMAND_METHOD, 0))).isEqualTo(true);
	}

	@Test
	public void testDoesNotSupportsNonJCommanderPojos() throws Exception {
		Method method = ReflectionUtils.findMethod(MyLordCommands.class, "apocalypse", String.class);

		assertThat(resolver.supports(Utils.createMethodParameter(method, 0))).isFalse();
	}

	@Test
	public void testPojoValuesAreCorrectlySet() {
		MethodParameter methodParameter = Utils.createMethodParameter(COMMAND_METHOD, 0);

		FieldCollins resolved = (FieldCollins) resolver.resolve(methodParameter, asList("--name foo -level 2 something-else yet-something-else".split(" ")));

		assertThat(resolved.getName()).isEqualTo("foo");
		assertThat(resolved.getLevel()).isEqualTo(2);
		assertThat(resolved.getRest()).containsOnlyOnce("something-else", "yet-something-else");
	}
}
