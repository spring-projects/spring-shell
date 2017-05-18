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

import java.util.Collections;

import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.shell2.result.TypeHierarchyResultHandler;

/**
 * Unit tests for {@link JLineShell}.
 *
 * @author Eric Bottard
 */
public class JLineShellTest {

	private JLineShell shell = new JLineShell();

	@Test
	public void testCtrlCInterception() throws Exception {
		shell.lineReader = mock(LineReader.class);
		AssertingExitRequestResultHandler resultHandler = new AssertingExitRequestResultHandler();
		shell.resultHandler = resultHandler;
		when(shell.lineReader.readLine(Mockito.<String>any())).thenThrow(
			new UserInterruptException("some input"),
			new UserInterruptException("")
		);

		try {
			shell.run();
		}
		catch (BreakControlFlow breakControlFlow) {

		}
		assertThat(resultHandler.exited, is(true));
	}

	private static class AssertingExitRequestResultHandler implements ResultHandler<ExitRequest> {

		private boolean exited;

		@Override
		public void handleResult(ExitRequest result) {
			exited = true;
			throw new BreakControlFlow();
		}
	}

	private static class BreakControlFlow extends RuntimeException {
	}



}
