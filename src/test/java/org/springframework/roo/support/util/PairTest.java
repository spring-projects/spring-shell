package org.springframework.roo.support.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * Unit test of the {@link Pair} class.
 *
 * @author Andrew Swan
 * @since 1.2.0
 */
public class PairTest {

	@Test
	public void testConstructWithNullKey() {
		new Pair<Object, Object>(null, "");
	}

	@Test
	public void testConstructWithNullValue() {
		new Pair<Object, Object>("", null);
	}

	@Test
	public void testInstanceEqualsItself() {
		final Pair<Integer, String> pair = new Pair<Integer, String>(1, "a");
		assertEquals(pair, pair);
	}

	@Test
	public void testEqualKeyAndValueAreEqual() {
		assertEquals(new Pair<Integer, String>(1, "a"), new Pair<Integer, String>(1, "a"));
	}

	@Test
	public void testUnequalKeyIsNotEqual() {
		assertFalse(new Pair<Integer, String>(1, "a").equals(new Pair<Integer, String>(2, "a")));
	}

	@Test
	public void testUnequalValueIsNotEqual() {
		assertFalse(new Pair<Integer, String>(1, "a").equals(new Pair<Integer, String>(1, "b")));
	}

	@Test
	public void testOtherClassIsNotAPair() {
		assertFalse(new Pair<Integer, String>(1, "a").equals("foo"));
	}
}
