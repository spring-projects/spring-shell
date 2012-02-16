package org.springframework.shell;

import java.util.ArrayList;
import java.util.List;

import jline.Completor;

import org.springframework.roo.shell.Completion;
import org.springframework.roo.shell.Parser;
import org.springframework.roo.support.util.Assert;

/**
 * An implementation of JLine's {@link Completor} interface that delegates to a {@link Parser}.
 *
 * @author Ben Alex
 * @since 1.0
 */
public class JLineCompletorAdapter implements Completor {

	// Fields
	private final Parser parser;

	public JLineCompletorAdapter(final Parser parser) {
		Assert.notNull(parser, "Parser required");
		this.parser = parser;
	}

	@SuppressWarnings("all")
	public int complete(final String buffer, final int cursor, final List candidates) {
		int result;
		try {
			JLineLogHandler.cancelRedrawProhibition();
			List<Completion> completions = new ArrayList<Completion>();
			result = parser.completeAdvanced(buffer, cursor, completions);
			for (Completion completion : completions) {
				candidates.add(new jline.Completion(completion.getValue(), completion.getFormattedValue(), completion.getHeading()));
			}
		} finally {
			JLineLogHandler.prohibitRedraw();
		}
		return result;
	}
}
