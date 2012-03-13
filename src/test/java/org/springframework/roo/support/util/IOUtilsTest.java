package org.springframework.roo.support.util;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.Closeable;
import java.io.IOException;

import org.junit.Test;

/**
 * Unit test of {@link IOUtils}.
 *
 * @author Andrew Swan
 * @since 1.2.0
 */
public class IOUtilsTest {

	@Test
	public void testCloseNullCloseable() {
		IOUtils.closeQuietly((Closeable) null); // Shouldn't throw an exception
	}

	@Test
	public void testCloseNonNullCloseableWithoutError() throws Exception {
		// Set up
		final Closeable mockCloseable = mock(Closeable.class);

		// Invoke
		IOUtils.closeQuietly(mockCloseable);

		// Check
		verify(mockCloseable).close();
	}

	@Test
	public void testCloseTwoNonNullCloseableWithErrorOnFirst() throws Exception {
		// Set up
		final Closeable mockCloseable1 = mock(Closeable.class);
		doThrow(new IOException("dummy")).when(mockCloseable1).close();
		final Closeable mockCloseable2 = mock(Closeable.class);

		// Invoke
		IOUtils.closeQuietly(mockCloseable1, mockCloseable2);

		// Check
		verify(mockCloseable1).close();
		verify(mockCloseable2).close();
	}
}
