package org.springframework.shell.jline;

import java.util.List;
import java.util.Set;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.completion.CompletionContext;
import org.springframework.shell.core.command.completion.CompletionProposal;
import org.springframework.shell.core.command.completion.CompletionProvider;

/**
 * A JLine {@link Completer} that completes command names from a {@link CommandRegistry}.
 *
 * @author Mahmoud Ben Hassine
 * @since 4.0.0
 */
public class CommandCompleter implements Completer {

	private final CommandRegistry commandRegistry;

	/**
	 * Create a new {@link CommandCompleter} instance.
	 * @param commandRegistry the command registry
	 */
	public CommandCompleter(CommandRegistry commandRegistry) {
		this.commandRegistry = commandRegistry;
	}

	@Override
	public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
		Set<Command> commands = this.commandRegistry.getCommands();
		if (this.commandRegistry.getCommandByName(line.word().trim()) != null) {
			Command command = this.commandRegistry.getCommandByName(line.word().trim());
			CompletionProvider completionProvider = command.getCompletionProvider();
			CompletionContext context = new CompletionContext(line.words(), line.wordIndex(), line.wordCursor(), null,
					null);
			List<CompletionProposal> proposals = completionProvider.apply(context);
			for (CompletionProposal proposal : proposals) {
				candidates.add(new Candidate(proposal.value()));
			}
		}
		else {
			for (Command command : commands) {
				candidates.add(new Candidate(command.getName(), command.getName() + ": " + command.getDescription(),
						command.getGroup(), command.getHelp(), null, null, true));
			}
		}
	}

}
