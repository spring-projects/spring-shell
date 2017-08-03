/*
 * Copyright 2016 the original author or authors.
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

package org.springframework.shell.standard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.stereotype.Component;

/**
 * A {@link ValueProvider} that knows how to complete values for {@link Enum} typed parameters.
 * @author Eric Bottard
 */
@Component
public class EnumValueProvider implements ValueProvider {

	@Override
	public boolean supports(MethodParameter parameter, CompletionContext completionContext) {
		return Enum.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public List<CompletionProposal> complete(MethodParameter parameter, CompletionContext completionContext, String[] hints) {
		List<CompletionProposal> result = new ArrayList<>();
		for (Object v : parameter.getParameterType().getEnumConstants()) {
			Enum e = (Enum) v;
			String prefix = completionContext.currentWordUpToCursor();
			if (prefix == null) {
				prefix = "";
			}
			if (e.name().startsWith(prefix)) {
				result.add(new CompletionProposal(e.name()));
			}
		}
		return result;
	}
}
