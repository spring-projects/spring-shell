package org.springframework.roo.support.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test of {@link ObjectUtils}
 *
 * @author Andrew Swan
 * @since 1.2.0
 */
public class ObjectUtilsTest {

	@Test
	public void testCompareTwoNulls() {
		assertEquals(0, ObjectUtils.nullSafeComparison(null, null));
	}

	@Test
	public void testCompareNullWithNonNull() {
		// Invoke
		final int result = ObjectUtils.nullSafeComparison(null, "");

		// Check
		assertTrue(result < 0);
	}

	@Test
	public void testCompareNonNullWithNull() {
		// Invoke
		final int result = ObjectUtils.nullSafeComparison("", null);

		// Check
		assertTrue(result > 0);
	}

	@Test
	public void testCompareLesserWithGreater() {
		// Invoke
		final int result = ObjectUtils.nullSafeComparison(100, 200);

		// Check
		assertTrue(result < 0);
	}

	@Test
	public void testCompareGreaterWithLesser() {
		// Invoke
		final int result = ObjectUtils.nullSafeComparison(300, 200);

		// Check
		assertTrue(result > 0);
	}

	@Test
	public void testCompareTwoEqualObjects() {
		assertEquals(0, ObjectUtils.nullSafeComparison(400, 400));
	}
	
	@Test
	public void testToStringWithNullObjectAndNullDefault() {
		assertNull(ObjectUtils.toString(null, null));
	}
	
	@Test
	public void testToStringWithNullObjectAndEmptyDefault() {
		assertEquals("", ObjectUtils.toString(null, ""));
	}
	
	@Test
	public void testToStringWithNullObjectAndNonEmptyDefault() {
		assertEquals("x", ObjectUtils.toString(null, "x"));
	}
	
	@Test
	public void testToStringWithNonNullObjectAndNullDefault() {
		assertEquals("1", ObjectUtils.toString(1, "anything"));
	}
	
	@Test
	public void testDefaultIfNullWhenObjectIsNullAndDefaultIsNull() {
		assertNull(ObjectUtils.defaultIfNull(null, null));
	}
	
	@Test
	public void testDefaultIfNullWhenObjectIsNullAndDefaultIsNotNull() {
		final Object defaultValue = 27;
		assertEquals(defaultValue, ObjectUtils.defaultIfNull(null, defaultValue));
	}
	
	@Test
	public void testDefaultIfNullWhenObjectIsNotNullAndDefaultIsNull() {
		final Object value = 27;
		assertEquals(value, ObjectUtils.defaultIfNull(value, null));
	}
	
	@Test
	public void testDefaultIfNullWhenObjectIsNotNullAndDefaultIsNotNull() {
		final Integer value = 27;
		assertEquals(value, ObjectUtils.defaultIfNull(value, value + 1));
	}
}
