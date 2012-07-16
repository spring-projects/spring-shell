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
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.springframework.shell.support.util.Pair;

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
