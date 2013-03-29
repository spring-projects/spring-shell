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

import org.springframework.shell.event.ParseResult;

/**
 * Extension interface allowing command provider to be called
 * in a generic fashion just before, and right after, executing a command.
 * 
 * @author Costin Leau
 */
public interface ExecutionProcessor extends CommandMarker {

	/**
	 * Method called before invoking the target command (described by {@link ParseResult}).
	 * Additionally, for advanced cases, the parse result itself effectively changing the invocation
	 * calling site.
	 * 
	 * @param invocationContext target command context
	 * @return the invocation target 
	 */
	ParseResult beforeInvocation(ParseResult invocationContext);

	/**
	 * Method called after successfully invoking the target command (described by {@link ParseResult}).
	 * 
	 * @param invocationContext target command context
	 * @param result the invocation result
	 */
	void afterReturningInvocation(ParseResult invocationContext, Object result);

	/**
	 * Method called after invoking the target command (described by {@link ParseResult}) had thrown an exception .
	 * 
	 * @param invocationContext target command context
	 * @param thrown the thrown object
	 */
	void afterThrowingInvocation(ParseResult invocationContext, Throwable thrown);

}
