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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;
import org.springframework.shell.support.util.NumberUtils;

/**
 * Unit test of {@link NumberUtils}.
 *
 * @author Alan Stewart
 * @since 1.2.0
 */
public class NumberUtilsTest {

	@Test
	public void testMinValueOfEmptyArray() {
		assertNull(NumberUtils.min(new Number[0]));
	}

	@Test
	public void testNullMinValues() {
		assertNull(NumberUtils.min(null, null));
	}

	@Test
	public void testOneMinValue() {
		assertEquals(BigDecimal.ONE, NumberUtils.min(1));
	}

	@Test
	public void testMinValues() {
		assertEquals(new BigDecimal("11"), NumberUtils.min(21, 11, 20L, 33.3D, new Short("55"), 11.3));
	}

	@Test
	public void testMinValues2() {
		assertEquals(new BigDecimal("3"), NumberUtils.min(null, 3, null, 4));
	}

	@Test
	public void testMultipleMinValues() {
		assertEquals(new BigDecimal(-10), NumberUtils.min(0, 10, null, -10, Integer.MAX_VALUE, BigInteger.TEN));
	}

	@Test
	public void testMultipleSameMinValues() {
		assertEquals(-1, NumberUtils.min(-1, -1F, -1L, -1D).intValueExact());
	}

	@Test
	public void testNullMaxValues() {
		assertNull(NumberUtils.max(null, null));
	}

	@Test
	public void testOneMaxValue() {
		assertEquals(BigDecimal.ONE, NumberUtils.max(1));
	}

	@Test
	public void testMaxValues() {
		assertEquals(BigDecimal.ONE, NumberUtils.max(null, 1, -1, null));
	}

	@Test
	public void testMultipleMaxValues() {
		assertEquals(new BigDecimal(String.valueOf(Double.MAX_VALUE)), NumberUtils.max(0, null, Integer.MIN_VALUE, 10, -10, Integer.MAX_VALUE, Long.MAX_VALUE, Double.MAX_VALUE));
	}
}
