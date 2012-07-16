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

/**
 * A class which contains a number of number manipulation operations
 *
 * @author James Tyrrell
 * @since 1.2.0
 */
public class MathUtils {

	public static double round(final double valueToRound, final int numberOfDecimalPlaces) {
		double multiplicationFactor = Math.pow(10, numberOfDecimalPlaces);
		double interestedInZeroDPs = valueToRound * multiplicationFactor;
		return Math.round(interestedInZeroDPs) / multiplicationFactor;
	}
}
