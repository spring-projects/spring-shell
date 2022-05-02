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
package org.springframework.shell.completion;

import java.util.List;

import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.command.CommandRegistration;

/**
 * Interface resolving completion proposals.
 *
 * @author Janne Valkealahti
 */
public interface CompletionResolver {

	/**
	 * Resolve completions.
	 *
	 * @param registration the command registration
	 * @param context the completion context
	 * @return list of resolved completions
	 */
	List<CompletionProposal> resolve(CommandRegistration registration, CompletionContext context);
}
