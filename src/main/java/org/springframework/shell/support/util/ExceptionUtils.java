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

import org.springframework.util.Assert;

/**
 * Methods for working with exceptions.
 *
 * @author Ben Alex
 * @since 1.0
 */
public abstract class ExceptionUtils {

	/**
	 * Obtains the root cause of an exception, if available.
	 *
	 * @param ex to extract the root cause from (required)
	 * @return the root cause, or original exception is unavailable (guaranteed to never be null)
	 */
	public final static Throwable extractRootCause(final Throwable ex) {
		Assert.notNull(ex, "An exception is required");
		Throwable root = ex;
		if (ex.getCause() != null) {
			root = ex.getCause();
		}
		return root;
	}
}
