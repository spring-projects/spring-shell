/*
 * Copyright 2011-2012 the original author or authors.
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
package org.springframework.shell.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.shell.core.CliOptionContext;

/**
 * Unit test of {@link CliOptionContext}
 *
 * @author Andrew Swan
 * @since 1.2.0
 */
public class CliOptionContextTest {

	// Constants
	private static final String OPTION_CONTEXT = "anything";

	@Test
	public void testGetOptionContextWhenNoneSet() {
		assertNull(CliOptionContext.getOptionContext());
	}

	@Test
	public void testSetAndGetOptionContext() {
		// Set up
		CliOptionContext.setOptionContext(OPTION_CONTEXT);

		// Invoke and check
		assertEquals(OPTION_CONTEXT, CliOptionContext.getOptionContext());
	}

	@Test
	public void testResetOptionContext() {
		// Set up
		CliOptionContext.setOptionContext(OPTION_CONTEXT);

		// Invoke
		CliOptionContext.resetOptionContext();

		// Check
		assertNull(CliOptionContext.getOptionContext());
	}
}
