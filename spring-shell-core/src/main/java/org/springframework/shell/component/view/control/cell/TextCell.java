/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.component.view.control.cell;

import java.util.function.Function;

import org.springframework.shell.geom.HorizontalAlign;
import org.springframework.shell.geom.VerticalAlign;

/**
 * Extension of a {@link Cell} to make it aware of an item style and selection state.
 *
 * @author Janne Valkealahti
 */
public interface TextCell<T> extends Cell<T> {

	/**
	 * Sets horizontal align for a text to draw. Defaults to
	 * {@link HorizontalAlign#CENTER}.
	 *
	 * @param hAlign the horizontal align
	 */
	void setHorizontalAlign(HorizontalAlign hAlign);

	/**
	 * Sets vertical align for a text to draw. Defaults to
	 * {@link VerticalAlign#CENTER}.
	 *
	 * @param vAlign the vertical align
	 */
	void setVerticalAlign(VerticalAlign vAlign);

	/**
	 * Helper method to build a {@code TextCell}.
	 *
	 * @param <T> type of an item
	 * @param item the item
	 * @param itemFunction the item function
	 * @return a default text cell
	 */
	static <T> TextCell<T> of(T item, Function<T, String> itemFunction) {
		return new DefaultTextCell<T>(item, itemFunction);
	}

	/**
	 * Helper method to build a {@code TextCell}.
	 *
	 * @param <T> type of an item
	 * @param item the item
	 * @param itemFunction the item function
	 * @param hAlign item horizontal alignment
	 * @param vAlign item vertical alignment
	 * @return
	 */
	static <T> TextCell<T> of(T item, Function<T, String> itemFunction, HorizontalAlign hAlign, VerticalAlign vAlign) {
		return new DefaultTextCell<T>(item, itemFunction, hAlign, vAlign);
	}

	static class DefaultTextCell<T> extends AbstractTextCell<T> {

		DefaultTextCell(T item, Function<T, String> itemFunction) {
			super(item, itemFunction);
		}

		DefaultTextCell(T item, Function<T, String> itemFunction, HorizontalAlign hAlign, VerticalAlign vAlign) {
			super(item, itemFunction, hAlign, vAlign);
		}

	}

}
