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
package org.springframework.shell.support.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.shell.support.util.ObjectUtils;

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
