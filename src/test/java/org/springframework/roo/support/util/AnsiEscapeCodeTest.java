package org.springframework.roo.support.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for the {@link AnsiEscapeCode} enum.
 *
 * @author Andrew Swan
 * @since 1.2.0
 */
public class AnsiEscapeCodeTest {
	
	@Before
	public void init() {
		System.setProperty("roo.console.ansi", Boolean.TRUE.toString());
	}

	@Test
	public void testCodesAreUnique() {
		// Set up
		final Set<Object> codes = new HashSet<Object>();

		// Invoke
		for (final AnsiEscapeCode escapeCode : AnsiEscapeCode.values()) {
			codes.add(escapeCode.code);
		}

		// Check
		assertEquals(AnsiEscapeCode.values().length, codes.size());
	}

	@Test
	public void testDecorateNullText() {
		assertNull(AnsiEscapeCode.decorate(null, AnsiEscapeCode.values()[0]));
	}

	@Test
	public void testDecorateEmptyText() {
		assertEquals("", AnsiEscapeCode.decorate("", AnsiEscapeCode.values()[0]));
	}

	@Test
	public void testDecorateWhitespace() {
		final AnsiEscapeCode effect = AnsiEscapeCode.values()[0]; // Arbitrary
		assertEquals(effect.code + " " + AnsiEscapeCode.OFF.code, AnsiEscapeCode.decorate(" ", effect));
	}
}
