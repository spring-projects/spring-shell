/*
 * Copyright 2021 the original author or authors.
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
package org.springframework.shell.boot;

import java.util.List;
import java.util.stream.Collectors;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.CompletingParsedLine;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.Shell;

@AutoConfiguration
public class CompleterAutoConfiguration {

	@Autowired
	private Shell shell;

	@Bean
	public CompleterAdapter completer() {
		CompleterAdapter completerAdapter = new CompleterAdapter();
		completerAdapter.setShell(shell);
		return completerAdapter;
	}

	public static class CompleterAdapter implements Completer {

		private Shell shell;

		@Override
		public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
			CompletingParsedLine cpl = (line instanceof CompletingParsedLine) ? ((CompletingParsedLine) line) : t -> t;

			CompletionContext context = new CompletionContext(sanitizeInput(line.words()), line.wordIndex(), line.wordCursor(), null, null);

			List<CompletionProposal> proposals = shell.complete(context);
			proposals.stream()
				.map(p -> new Candidate(
					p.dontQuote() ? p.value() : cpl.emit(p.value()).toString(),
					p.displayText(),
					p.category(),
					p.description(),
					null,
					null,
					p.complete())
				)
				.forEach(candidates::add);
		}

		public void setShell(Shell shell) {
			this.shell = shell;
		}

		static List<String> sanitizeInput(List<String> words) {
			words = words.stream()
				.map(s -> s.replaceAll("^\\n+|\\n+$", "")) // CR at beginning/end of line introduced by backslash continuation
				.map(s -> s.replaceAll("\\n+", " ")) // CR in middle of word introduced by return inside a quoted string
				.collect(Collectors.toList());
			return words;
		}
	}

}
