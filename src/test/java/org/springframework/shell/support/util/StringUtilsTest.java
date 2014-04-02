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

import static org.junit.Assert.*;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testPadRightWithNullString() {
		assertEquals("     ", StringUtils.padRight(null, 5));
	}

	@Test
	public void testPadRightWithEmptyString() {
		assertEquals("     ", StringUtils.padRight("", 5));
	}

	@Test
	public void testPadRight() {
		assertEquals("foo  ", StringUtils.padRight("foo", 5));
	}
	
	@Test
    public void testLeftPad_StringInt() {
        assertEquals(null, StringUtils.padLeft(null, 5));
        assertEquals("     ", StringUtils.padLeft("", 5));
        assertEquals("  abc", StringUtils.padLeft("abc", 5));
        assertEquals("abc", StringUtils.padLeft("abc", 2));
    }
        
	@Test
    public void testLeftPad_StringIntChar() {
        assertEquals(null, StringUtils.padLeft(null, 5, ' '));
        assertEquals("     ", StringUtils.padLeft("", 5, ' '));
        assertEquals("  abc", StringUtils.padLeft("abc", 5, ' '));
        assertEquals("xxabc", StringUtils.padLeft("abc", 5, 'x'));
        assertEquals("\uffff\uffffabc", StringUtils.padLeft("abc", 5, '\uffff'));
        assertEquals("abc", StringUtils.padLeft("abc", 2, ' '));
        String str = StringUtils.padLeft("aaa", 10000, 'a');  // bigger than pad length
        assertEquals(10000, str.length());
        //Note, did not include the next assert to avoid pulling in a long chain of methods from commons lang
        //assertEquals(true, StringUtils.containsOnly(str, new char[] {'a'}));
    }
     
	@Test
    public void testLeftPad_StringIntString() {
        assertEquals(null, StringUtils.padLeft(null, 5, "-+"));
        assertEquals(null, StringUtils.padLeft(null, 5, null));
        assertEquals("     ", StringUtils.padLeft("", 5, " "));
        assertEquals("-+-+abc", StringUtils.padLeft("abc", 7, "-+"));
        assertEquals("-+~abc", StringUtils.padLeft("abc", 6, "-+~"));
        assertEquals("-+abc", StringUtils.padLeft("abc", 5, "-+~"));
        assertEquals("abc", StringUtils.padLeft("abc", 2, " "));
        assertEquals("abc", StringUtils.padLeft("abc", -1, " "));
        assertEquals("  abc", StringUtils.padLeft("abc", 5, null));
        assertEquals("  abc", StringUtils.padLeft("abc", 5, ""));
    }
}
