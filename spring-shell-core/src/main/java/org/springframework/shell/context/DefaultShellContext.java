/*
 * Copyright 2022-2024 the original author or authors.
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
package org.springframework.shell.context;

import org.springframework.util.Assert;

/**
 * Default implementation of a {@link ShellContext}.
 *
 * @author Janne Valkealahti
 */
public class DefaultShellContext implements ShellContext {

	private InteractionMode interactionMode = InteractionMode.ALL;
	private final boolean pty;

	public DefaultShellContext() {
		this(false);
	}

	public DefaultShellContext(boolean pty) {
		this.pty = pty;
	}

	@Override
	public InteractionMode getInteractionMode() {
		return interactionMode;
	}

	@Override
	public void setInteractionMode(InteractionMode interactionMode) {
		Assert.notNull(interactionMode, "mode cannot be null");
		this.interactionMode = interactionMode;
	}

	@Override
	public boolean hasPty() {
		return this.pty;
	}
}
