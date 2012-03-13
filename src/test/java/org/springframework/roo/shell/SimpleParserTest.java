package org.springframework.roo.shell;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of {@link SimpleParser}
 *
 * @author Andrew Swan
 * @since 1.2.0
 */
public class SimpleParserTest {

	// Fixture
	private SimpleParser simpleParser;

	@Before
	public void setUp() {
		this.simpleParser = new SimpleParser();
	}

	@Test
	public void testNormaliseEmptyString() {
		assertNormalised("", "");
	}

	@Test
	public void testNormaliseSpaces() {
		assertNormalised("    ", "");
	}

	@Test
	public void testNormaliseSingleWord() {
		assertNormalised("hint", "hint");
	}

	@Test
	public void testNormaliseMultipleWords() {
		assertNormalised(" security   setup ", "security setup");
	}

	/**
	 * Asserts that normalising the given input produces the given output
	 *
	 * @param input can't be <code>null</code>
	 * @param output
	 */
	private void assertNormalised(final String input, final String output) {
		Assert.assertEquals(output, simpleParser.normalise(input));
	}
}
