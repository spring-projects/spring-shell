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
 * Encodes a given byte array as hex.
 *
 * <p>
 * Most methods in this class were obtained from the Spring Security class,
 * org.springframework.security.core.codec.Hex. Spring Security is licensed
 * under the Apache Software License version 2.0 and the following code is used
 * pursuant to that license.
 *
 * @author Luke Taylor
 * @author Ben Alex
 * @since 1.1.1
 */
public abstract class HexUtils {

	public static String toHex(final byte[] bytes) {
		return new String(encode(bytes));
	}

	private static final char[] HEX = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	};

	public static char[] encode(final byte[] bytes) {
		final int nBytes = bytes.length;
		char[] result = new char[2 * nBytes];
		int j = 0;

		for (int i = 0; i < nBytes; i++) {
			// Char for top 4 bits
			result[j++] = HEX[(0xF0 & bytes[i]) >>> 4];

			// Bottom 4
			result[j++] = HEX[(0x0F & bytes[i])];
		}

		return result;
	}
}
