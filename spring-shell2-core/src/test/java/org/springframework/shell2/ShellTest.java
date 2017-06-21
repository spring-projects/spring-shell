/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.shell2;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link Shell}.
 *
 * @author Eric Bottard
 */
public class ShellTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private Shell.InputProvider inputProvider;

	@Mock
	private ResultHandler resultHandler;

	@Mock
	private ParameterResolver parameterResolver;
	
	private ValueResult valueResult;

	@InjectMocks
	private Shell shell;

	private boolean invoked;

	@Before
	public void setUp() {
		shell.parameterResolvers = Arrays.asList(parameterResolver);
	}

	@Test
	public void commandMatch() throws IOException {
		when(parameterResolver.supports(any())).thenReturn(true);
		when(inputProvider.readInput()).thenReturn(() -> "hello world how are you doing ?");
		valueResult = new ValueResult(null, "test");
		when(parameterResolver.resolve(any(), any())).thenReturn(valueResult);
		doThrow(new Exit()).when(resultHandler).handleResult(any());

		shell.methodTargets = Collections.singletonMap("hello world", MethodTarget.of("helloWorld", this, "Say hello"));

		try {
			shell.run();
			fail("Exit expected");
		}
		catch (Exit expected) {

		}

		Assert.assertTrue(invoked);
	}

	@Test
	public void commandNotFound() throws IOException {
		when(inputProvider.readInput()).thenReturn(() -> "hello world how are you doing ?");
		doThrow(new Exit()).when(resultHandler).handleResult(any(CommandNotFound.class));

		shell.methodTargets = Collections.singletonMap("bonjour", MethodTarget.of("helloWorld", this, "Say hello"));

		try {
			shell.run();
			fail("Exit expected");
		}
		catch (Exit expected) {

		}
	}

	@Test
	public void noCommand() throws IOException {
		when(parameterResolver.supports(any())).thenReturn(true);
		when(inputProvider.readInput()).thenReturn(() -> "", () -> "hello world how are you doing ?");
		valueResult = new ValueResult(null, "test");
		when(parameterResolver.resolve(any(), any())).thenReturn(valueResult);
		doThrow(new Exit()).when(resultHandler).handleResult(any());

		shell.methodTargets = Collections.singletonMap("hello world", MethodTarget.of("helloWorld", this, "Say hello"));

		try {
			shell.run();
			fail("Exit expected");
		}
		catch (Exit expected) {

		}

		Assert.assertTrue(invoked);
	}

	@Test
	public void commandThrowingAnException() throws IOException {
		when(parameterResolver.supports(any())).thenReturn(true);
		when(inputProvider.readInput()).thenReturn(() -> "fail");
		doThrow(new Exit()).when(resultHandler).handleResult(any(SomeException.class));

		shell.methodTargets = Collections.singletonMap("fail", MethodTarget.of("failing", this, "Will throw an exception"));

		try {
			shell.run();
			fail("Exit expected");
		}
		catch (Exit expected) {

		}

		Assert.assertTrue(invoked);

	}

	private void helloWorld(String a) {
		invoked = true;
	}

	private String failing() {
		invoked = true;
		throw new SomeException();
	}


	private static class Exit extends RuntimeException {
	}

	private static class SomeException extends RuntimeException {

	}


}
