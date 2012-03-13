package org.springframework.roo.shell;

import org.springframework.roo.support.util.AnsiEscapeCode;
import org.springframework.roo.support.util.StringUtils;

public class Completion {

	// Fields
	private final int order;
	private final String formattedValue;
	private final String heading;
	private final String value;

	/**
	 * Constructor
	 *
	 * @param value
	 */
	public Completion(final String value) {
		this(value, value, null, 0);
	}

	/**
	 * Constructor
	 *
	 * @param value
	 * @param formattedValue
	 * @param heading
	 * @param order
	 */
	public Completion(final String value, final String formattedValue, String heading, final int order) {
		this.formattedValue = formattedValue;
		this.order = order;
		this.value = value;
		if (StringUtils.hasText(heading)) {
			heading = AnsiEscapeCode.decorate(heading, AnsiEscapeCode.UNDERSCORE, AnsiEscapeCode.FG_GREEN);
		}
		this.heading = heading;
	}

	public String getValue() {
		return value;
	}

	public String getFormattedValue() {
		return formattedValue;
	}

	public String getHeading() {
		return heading;
	}

	public int getOrder() {
		return order;
	}

	@Override
	public String toString() {
		return order + ". " + heading + " - " + value;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final Completion that = (Completion) o;
		if (formattedValue != null ? !formattedValue.equals(that.formattedValue) : that.formattedValue != null) {
			return false;
		}
		if (heading != null ? !heading.equals(that.heading) : that.heading != null) {
			return false;
		}
		if (value != null ? !value.equals(that.value) : that.value != null) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result = value != null ? value.hashCode() : 0;
		result = 31 * result + (formattedValue != null ? formattedValue.hashCode() : 0);
		result = 31 * result + (heading != null ? heading.hashCode() : 0);
		return result;
	}
}

