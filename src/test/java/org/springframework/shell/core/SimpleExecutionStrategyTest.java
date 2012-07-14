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
package org.springframework.shell.core;

import java.lang.reflect.Method;

import org.junit.Test;
import org.springframework.shell.core.ExecutionProcessor;
import org.springframework.shell.core.SimpleExecutionStrategy;
import org.springframework.shell.event.ParseResult;
import org.springframework.util.ReflectionUtils;

import static org.junit.Assert.*;

/**
 * @author Costin Leau
 */
public class SimpleExecutionStrategyTest {

	public static class Target implements ExecutionProcessor {
		Object before = null;
		Object after = null;
		ParseResult beforeReturn = null;

		public String one() {
			return "one";
		}

		public String two() {
			return "two";
		}

		public ParseResult beforeInvocation(ParseResult invocationContext) {
			before = invocationContext;
			return (beforeReturn == null ? invocationContext : beforeReturn);
		}

		public void afterReturningInvocation(ParseResult invocationContext, Object result) {
			after = invocationContext;
		}

		public void afterThrowingInvocation(ParseResult invocationContext, Throwable thrown) {
			//
		}
	}

	private SimpleExecutionStrategy execution = new SimpleExecutionStrategy();

	@Test
	public void testSimpleCommandProcessor() throws Exception {
		Target target = new Target();
		Method one = ReflectionUtils.findMethod(target.getClass(), "one");
		ParseResult result = new ParseResult(one, target, null);

		assertEquals("one", execution.execute(result));
		assertSame(result, target.before);
		assertSame(result, target.after);
	}

	@Test
	public void testRedirectCommandProcessor() throws Exception {
		Target target = new Target();
		Method one = ReflectionUtils.findMethod(target.getClass(), "one");
		Method two = ReflectionUtils.findMethod(target.getClass(), "two");
		ParseResult given = new ParseResult(one, target, null);
		ParseResult redirect = new ParseResult(two, target, null);
		target.beforeReturn = redirect;

		assertEquals("two", execution.execute(given));
		assertSame(given, target.before);
		assertSame(redirect, target.after);
	}
}