package org.springframework.roo.shell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

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
