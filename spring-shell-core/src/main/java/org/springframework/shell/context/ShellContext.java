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
package org.springframework.shell.context;

/**
 * Interface defining a contract for a context which allows to loosely connect
 * different components together and keep things alive between commands.
 *
 * @author Janne Valkealahti
 */
public interface ShellContext {

	/**
	 * Gets an interaction mode.
	 *
	 * @return a current interaction mode
	 */
	InteractionMode getInteractionMode();

	/**
	 * Sets an interaction mode.
	 *
	 * @param interactionMode the interaction mode
	 */
	void setInteractionMode(InteractionMode interactionMode);
}
