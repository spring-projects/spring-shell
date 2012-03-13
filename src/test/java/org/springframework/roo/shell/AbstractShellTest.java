package org.springframework.roo.shell;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;


/**
 * Unit test of {@link AbstractShell} (not a superclass for writing tests for
 * {@link AbstractShell} subclasses)
 *
 * @author Andrew Swan
 * @since 1.2.0
 */
public class AbstractShellTest {

	@Test
	public void testProps() {
		// Set up
		final AbstractShell shell = mock(AbstractShell.class);
		when(shell.props()).thenCallRealMethod();

		// Invoke
		final String props = shell.props();

		// Check
		assertNotNull(props);
	}
}
