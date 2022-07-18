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
package org.springframework.shell.component.flow;

/**
 * Default impl for {@link SelectItem}.
 *
 * @author Janne Valkealahti
 */
public class DefaultSelectItem implements SelectItem {

	private String name;
	private String item;
	private boolean enabled;
	private boolean selected;

	public DefaultSelectItem(String name, String item, boolean enabled, boolean selected) {
		this.name = name;
		this.item = item;
		this.enabled = enabled;
		this.selected = selected;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String item() {
		return item;
	}

	@Override
	public boolean enabled() {
		return enabled;
	}

	@Override
	public boolean selected() {
		return selected;
	}
}