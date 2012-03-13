package org.springframework.roo.support.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

/**
 * Unit test of {@link PairList}
 *
 * @author Andrew Swan
 * @since 1.2.0
 */
public class PairListTest {

	// Constants
	private static final int KEY_1 = 10;
	private static final int KEY_2 = 20;
	private static final String VALUE_1 = "a";
	private static final String VALUE_2 = "b";
	private static final Pair<Integer, String> PAIR_1 = new Pair<Integer, String>(KEY_1, VALUE_1);
	private static final Pair<Integer, String> PAIR_2 = new Pair<Integer, String>(KEY_2, VALUE_2);

	@SuppressWarnings("unchecked")
	@Test
	public void testConstructFromVarargArrayOfPairs() {
		// Invoke
		final PairList<Integer, String> pairs = new PairList<Integer, String>(PAIR_1, PAIR_2);

		// Check
		assertEquals(2, pairs.size());
		assertEquals(Arrays.asList(KEY_1, KEY_2), pairs.getKeys());
		assertEquals(Arrays.asList(VALUE_1, VALUE_2), pairs.getValues());
		final Pair<Integer, String>[] array = pairs.toArray();
		assertEquals(pairs.size(), array.length);
		assertEquals(pairs, Arrays.asList(array));
	}

	@Test
	public void testConstructFromListsOfKeysAndValues() {
		// Invoke
		final PairList<Integer, String> pairs = new PairList<Integer, String>(Arrays.asList(KEY_1, KEY_2), Arrays.asList(VALUE_1, VALUE_2));

		// Check
		assertEquals(2, pairs.size());
		assertEquals(PAIR_1, pairs.get(0));
		assertEquals(PAIR_2, pairs.get(1));
	}

	@Test
	public void testConstructFromNulListsOfKeysAndValues() {
		// Invoke
		final PairList<Integer, String> pairs = new PairList<Integer, String>(null, null);

		// Check
		assertEquals(0, pairs.size());
	}
}
