/*
 * Copyright 2017-2022 the original author or authors.
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

package org.springframework.shell.standard;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;

/**
 * A {@link ValueProvider} that can populate names of local {@link File}s, either absolute or relative to the
 * current working directory.
 *
 * @author Eric Bottard
 * @author Janne Valkealahti
 */
public class FileValueProvider implements ValueProvider {

	@Override
	public List<CompletionProposal> complete(CompletionContext completionContext) {
		String input = completionContext.currentWordUpToCursor();
		int lastSlash = input.lastIndexOf(File.separatorChar);
		Path dir = lastSlash > -1 ? Paths.get(input.substring(0, lastSlash + 1)) : Paths.get("");
		String prefix = input.substring(lastSlash + 1);

		try {
			return Files
				.find(dir, 1, (p, a) -> p.getFileName() != null && p.getFileName().toString().startsWith(prefix),
					FOLLOW_LINKS)
				.map(p -> {
					boolean directory = Files.isDirectory(p);
					String value = p.toString() + (directory ? File.separatorChar : "");
					return new CompletionProposal(value).complete(!directory);
				})
				.collect(Collectors.toList());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
