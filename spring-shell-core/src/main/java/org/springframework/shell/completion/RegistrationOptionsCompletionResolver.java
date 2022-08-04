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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;

/**
 * Default implementation of a {@link CompletionResolver}.
 *
 * @author Janne Valkealahti
 */
public class RegistrationOptionsCompletionResolver implements CompletionResolver {

	@Override
	public List<CompletionProposal> apply(CompletionContext context) {
		if (context.getCommandRegistration() == null) {
			return Collections.emptyList();
		}
		List<CompletionProposal> candidates = new ArrayList<>();
		context.getCommandRegistration().getOptions().stream()
			.flatMap(o -> Stream.of(o.getLongNames()))
			.map(ln -> "--" + ln)
			.filter(ln -> !context.getWords().contains(ln))
			.map(CompletionProposal::new)
			.forEach(candidates::add);
		context.getCommandRegistration().getOptions().stream()
			.flatMap(o -> Stream.of(o.getShortNames()))
			.map(ln -> "-" + ln)
			.filter(ln -> !context.getWords().contains(ln))
			.map(CompletionProposal::new)
			.forEach(candidates::add);
		return candidates;
	}
}
