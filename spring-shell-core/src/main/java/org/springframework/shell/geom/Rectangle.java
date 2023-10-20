/*
 * Copyright 2023 the original author or authors.
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
package org.springframework.shell.geom;

/**
 * Record representing coordinates {@code x}, {@code y} and its {@code width}
 * and {@code height}.
 */
public record Rectangle(int x, int y, int width, int height) {

	public boolean contains(int X, int Y) {
		int w = this.width;
		int h = this.height;
		if ((w | h) < 0) {
			return false;
		}
		int x = this.x;
		int y = this.y;
		if (X < x || Y < y) {
			return false;
		}
		w += x;
		h += y;
		return ((w < x || w > X) && (h < y || h > Y));
	}

	/**
	 * Determines whether the {@code Rectangle} is empty. When the {@code Rectangle}
	 * is empty, it encloses no area.
	 *
	 * @return {@code true} if the {@code Rectangle} is empty; {@code false}
	 *         otherwise.
	 */
	public boolean isEmpty() {
		return width <= 0 || height <= 0;
	}
}
