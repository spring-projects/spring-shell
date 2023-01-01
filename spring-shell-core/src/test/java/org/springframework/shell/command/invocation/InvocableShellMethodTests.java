/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.shell.command.invocation;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import org.springframework.util.ReflectionUtils;

import static org.assertj.core.api.Assertions.assertThat;

class InvocableShellMethodTests {

	@Test
	public void resolveArg() throws Exception {
		Handler bean = new Handler();
		Method method = ReflectionUtils.findMethod(Handler.class, "handle", Integer.class, String.class);
		InvocableShellMethod invocable = new InvocableShellMethod(bean, method);
		Object value = invocable.invoke(null, 99, "value");
		assertThat(value).isEqualTo("99-value");
	}

	@SuppressWarnings("unused")
	private static class Handler {

		public String handle(Integer intArg, String stringArg) {
			return intArg + "-" + stringArg;
		}
	}
}
