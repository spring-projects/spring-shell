package org.springframework.shell.jline;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jspecify.annotations.Nullable;
import org.springframework.shell.core.command.Command;
import org.springframework.shell.core.command.CommandOption;
import org.springframework.shell.core.command.CommandRegistry;
import org.springframework.shell.core.command.completion.CompletionContext;
import org.springframework.shell.core.command.completion.CompletionProposal;
import org.springframework.shell.core.command.completion.CompletionProvider;

import java.util.List;

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
		Command commandByName = findCommandByWords(line.words());
		if (commandByName != null) {
			// add option completions for the command
			List<CommandOption> options = commandByName.getOptions();
			for (CommandOption option : options) {
				if (option.longName() != null && !line.line().contains("--" + option.longName())) {
					candidates.add(new Candidate("--" + option.longName()));
				}
				if (option.shortName() != ' ' && !line.line().contains("-" + option.shortName())) {
					candidates.add(new Candidate("-" + option.shortName()));
				}
			}
			// add custom completions from the command's completion provider
			CompletionProvider completionProvider = commandByName.getCompletionProvider();
			CompletionContext context = new CompletionContext(line.words(), line.wordIndex(), line.wordCursor(), null,
					null);
			List<CompletionProposal> proposals = completionProvider.apply(context);
			for (CompletionProposal proposal : proposals) {
				candidates.add(new Candidate(proposal.value()));
			}
		}
		else {
			for (Command command : this.commandRegistry.getCommands()) {
				candidates.add(new Candidate(command.getName(), command.getName() + ": " + command.getDescription(),
						command.getGroup(), command.getHelp(), null, null, true));
			}
		}
	}

	@Nullable private Command findCommandByWords(List<String> words) {
		StringBuilder commandName = new StringBuilder();
		for (String word : words) {
			if (word.startsWith("-")) {
				break;
			}
			commandName.append(word).append(" ");
		}
		return this.commandRegistry.getCommandByName(commandName.toString().trim());
	}

}
