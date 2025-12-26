package org.springframework.shell.core.command.completion;

import java.util.Arrays;
import java.util.List;

/**
 * A completion provider that composes multiple completion providers.
 *
 * @author Mahmoud Ben Hassine
 * @since 4.0.0
 */
public class CompositeCompletionProvider implements CompletionProvider {

	private final CompletionProvider[] providers;

	/**
	 * Create a new {@link CompositeCompletionProvider} with the given providers.
	 * @param providers the completion providers to compose
	 */
	public CompositeCompletionProvider(CompletionProvider... providers) {
		this.providers = providers;
	}

	@Override
	public List<CompletionProposal> apply(CompletionContext completionContext) {
		return Arrays.stream(providers).flatMap(provider -> provider.apply(completionContext).stream()).toList();
	}

}
