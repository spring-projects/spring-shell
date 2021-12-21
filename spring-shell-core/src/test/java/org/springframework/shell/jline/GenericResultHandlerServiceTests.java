/*
 * Copyright 2021 the original author or authors.
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
package org.springframework.shell.jline;

import org.junit.jupiter.api.Test;

import org.springframework.shell.ResultHandler;
import org.springframework.shell.result.GenericResultHandlerService;

import static org.assertj.core.api.Assertions.assertThat;

public class GenericResultHandlerServiceTests {

	@Test
	public void testSimpleHandling() {
		StringResultHandler stringResultHandler = new StringResultHandler();
		IntegerResultHandler integerResultHandler = new IntegerResultHandler();
		GenericResultHandlerService resultHandlerService = new GenericResultHandlerService();
		resultHandlerService.addResultHandler(stringResultHandler);
		resultHandlerService.addResultHandler(integerResultHandler);
		resultHandlerService.handle("string");
		assertThat(stringResultHandler.result).isEqualTo("string");
		assertThat(integerResultHandler.result).isNull();;
		resultHandlerService.handle(0);
		assertThat(integerResultHandler.result).isEqualTo(0);
	}

	@Test
	public void testObjectHandling() {
		ObjectResultHandler resultHandler = new ObjectResultHandler();
		GenericResultHandlerService resultHandlerService = new GenericResultHandlerService();
		resultHandlerService.addResultHandler(resultHandler);
		resultHandlerService.handle("string");
		assertThat(resultHandler.result).isEqualTo("string");
	}

	private static class StringResultHandler implements ResultHandler<String> {

		String result;

		@Override
		public void handleResult(String result) {
			this.result = result;
		}
	}

	private static class IntegerResultHandler implements ResultHandler<Integer> {

		Integer result;

		@Override
		public void handleResult(Integer result) {
			this.result = result;
		}
	}

	private static class ObjectResultHandler implements ResultHandler<Object> {

		Object result;

		@Override
		public void handleResult(Object result) {
			this.result = result;
		}
	}
}
