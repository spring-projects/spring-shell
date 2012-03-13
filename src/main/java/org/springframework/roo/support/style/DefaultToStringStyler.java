/*
 * Copyright 2002-2008 the original author or authors.
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

package org.springframework.roo.support.style;

import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.ClassUtils;
import org.springframework.roo.support.util.ObjectUtils;

/**
 * Spring's default <code>toString()</code> styler.
 *
 * <p>This class is used by {@link ToStringCreator} to style <code>toString()</code>
 * output in a consistent manner according to Spring conventions.
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 1.2.2
 */
public class DefaultToStringStyler implements ToStringStyler {

	// Fields
	private final ValueStyler valueStyler;

	/**
	 * Create a new DefaultToStringStyler.
	 * @param valueStyler the ValueStyler to use
	 */
	public DefaultToStringStyler(final ValueStyler valueStyler) {
		Assert.notNull(valueStyler, "ValueStyler must not be null");
		this.valueStyler = valueStyler;
	}

	/**
	 * Return the ValueStyler used by this ToStringStyler.
	 */
	protected final ValueStyler getValueStyler() {
		return this.valueStyler;
	}

	public void styleStart(final StringBuilder buffer, final Object obj) {
		if (!obj.getClass().isArray()) {
			buffer.append('[').append(ClassUtils.getShortName(obj.getClass()));
			styleIdentityHashCode(buffer, obj);
		}
		else {
			buffer.append('[');
			styleIdentityHashCode(buffer, obj);
			buffer.append(' ');
			styleValue(buffer, obj);
		}
	}

	private void styleIdentityHashCode(final StringBuilder buffer, final Object obj) {
		buffer.append('@');
		buffer.append(ObjectUtils.getIdentityHexString(obj));
	}

	public void styleEnd(final StringBuilder buffer, final Object o) {
		buffer.append(']');
	}

	public void styleField(final StringBuilder buffer, final String fieldName, final Object value) {
		styleFieldStart(buffer, fieldName);
		styleValue(buffer, value);
		styleFieldEnd(buffer, fieldName);
	}

	protected void styleFieldStart(final StringBuilder buffer, final String fieldName) {
		buffer.append(' ').append(fieldName).append(" = ");
	}

	protected void styleFieldEnd(final StringBuilder buffer, final String fieldName) {
	}

	public void styleValue(final StringBuilder buffer, final Object value) {
		buffer.append(this.valueStyler.style(value));
	}

	public void styleFieldSeparator(final StringBuilder buffer) {
		buffer.append(',');
	}
}
