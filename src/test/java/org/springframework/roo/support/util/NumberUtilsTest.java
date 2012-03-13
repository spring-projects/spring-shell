package org.springframework.roo.support.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

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
