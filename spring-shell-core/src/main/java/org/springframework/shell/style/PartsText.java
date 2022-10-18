/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.shell.style;

import java.util.Arrays;
import java.util.List;

public class PartsText {

	private List<PartText> parts;

	PartsText(List<PartText> parts) {
		this.parts = parts;
	}

	public static PartsText of(PartText... parts) {
		return new PartsText(Arrays.asList(parts));
	}

	public static PartsText of(List<PartText> parts) {
		return new PartsText(parts);
	}

	public List<PartText> getParts() {
		return parts;
	}

	public static class PartText {

		private String text;
		private boolean match;

		public PartText(String text, boolean match) {
			this.text = text;
			this.match = match;
		}

		public static PartText of(String text, boolean match) {
			return new PartText(text, match);
		}

		public String getText() {
			return text;
		}

		public boolean isMatch() {
			return match;
		}
	}
}
