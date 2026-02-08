/*
 * Copyright 2025-present the original author or authors.
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
package org.springframework.shell.core.command.adapter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.ExitStatus;
import org.springframework.shell.core.command.annotation.Option;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Andrey Litvitski
 */
class MethodInvokerCommandAdapterDefaultValueTests {

	private static class Target {

		int seen;

		public void run(@Option(longName = "retries", defaultValue = "3") int retries) {
			this.seen = retries;
		}

	}

	@Test
	void optionDefaultValueIsUsedForPrimitiveWhenOptionMissing() throws Exception {
		Target target = new Target();
		Method method = Target.class.getDeclaredMethod("run", int.class);

		DefaultConversionService conversionService = new DefaultConversionService();
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

		CommandContext ctx = Mockito.mock(CommandContext.class);
		Mockito.when(ctx.getOptionByLongName("retries")).thenReturn(null);

		StringWriter out = new StringWriter();
		Mockito.when(ctx.outputWriter()).thenReturn(new PrintWriter(out));

		MethodInvokerCommandAdapter adapter = new MethodInvokerCommandAdapter("name", "desc", "group", "help", false,
				method, target, conversionService, validator);

		ExitStatus status = adapter.doExecute(ctx);

		assertThat(status).isEqualTo(ExitStatus.OK);
		assertThat(target.seen).isEqualTo(3);
	}

}