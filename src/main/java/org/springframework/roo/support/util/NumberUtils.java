package org.springframework.roo.support.util;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Provides extra functionality for Java Number classes.
 *
 * @author Alan Stewart
 * @since 1.2.0
 */
public final class NumberUtils {

	/**
	 * Returns the minimum value in the array.
	 *
	 * @param array an array of Numbers (can be <code>null</code>)
	 * @return the minimum value in the array, or null if all the elements are null
	 */
	public static BigDecimal min(final Number... array) {
		return minOrMax(true, array);
	}

	/**
	 * Returns the maximum value in the array.
	 *
	 * @param array an array of Numbers (can be <code>null</code>)
	 * @return the maximum value in the array, or null if all the elements are null
	 */
	public static BigDecimal max(final Number... array) {
		return minOrMax(false, array);
	}

	/**
	 * Finds the minimum or maxiumum value contained in the given array,
	 * ignoring any <code>null</code> elements
	 *
	 * @param findMinimum <code>false</code> to get the maximum
	 * @param numbers can be <code>null</code>, empty, or contain <code>null</code>
	 * elements
	 * @return <code>null</code> if the array is <code>null</code>, empty, or
	 * all its elements are <code>null</code>
	 */
	private static BigDecimal minOrMax(final boolean findMinimum, final Number... numbers) {
		if (numbers == null || numbers.length == 0) {
			return null;
		}
		BigDecimal extreme = null;
		for (final Number number : numbers) {
			if (number != null) {
				final BigDecimal candidate = getBigDecimal(number);
				if (extreme == null || (findMinimum ? candidate.compareTo(extreme) < 0 : candidate.compareTo(extreme) > 0)) {
					// The non-null candidate is the new extreme
					extreme = candidate;
				}
			}
		}
		return extreme;
	}

	/**
	 * Converts the given number to a {@link BigDecimal}
	 *
	 * @param number the number to convert (can be <code>null</code>)
	 * @return <code>null</code> if the given number was <code>null</code>
	 */
	private static BigDecimal getBigDecimal(final Number number) {
		if (number == null || number instanceof BigDecimal) {
			return (BigDecimal) number;
		}
		if (number instanceof BigInteger) {
			return new BigDecimal((BigInteger) number);
		}
		return new BigDecimal(number.toString());
	}

	/**
	 * Constructor is private to prevent instantiation
	 */
	private NumberUtils() {}
}
