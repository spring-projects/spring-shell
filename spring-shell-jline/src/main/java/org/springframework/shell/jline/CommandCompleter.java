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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A JLine {@link Completer} that completes command names from a {@link CommandRegistry}.
 *
 * @author Mahmoud Ben Hassine
 * @author David Pilar
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
			List<CommandOption> options = commandByName.getOptions();
			CommandOption commandOption = findOptionByWords(line.words(), options);
			if (commandOption == null) {
				// add option completions for the command
				for (CommandOption option : options) {
					boolean present = isOptionPresent(line, option);
					if (option.longName() != null && !present) {
						candidates.add(new Candidate("--" + option.longName()));
					}
					if (option.shortName() != ' ' && !present) {
						candidates.add(new Candidate("-" + option.shortName()));
					}
				}
			}
			// add custom completions from the command's completion provider
			CompletionProvider completionProvider = commandByName.getCompletionProvider();
			CompletionContext context = new CompletionContext(line.words(), line.wordIndex(), line.wordCursor(),
					commandByName, commandOption);
			List<CompletionProposal> proposals = completionProvider.apply(context);
			for (CompletionProposal proposal : proposals) {
				candidates.add(new Candidate(proposal.value()));
			}
		}
		else {
			this.commandRegistry.getCommandsByPrefix(line.line())
				.stream()
				.map(command -> toCommandCandidate(command, line.words()))
				.forEach(candidates::add);
		}
	}

	private Candidate toCommandCandidate(Command command, List<String> words) {
		String prefix = words.size() > 1 ? String.join(" ", words.subList(0, words.size() - 1)) : "";
		return new Candidate(command.getName().substring(prefix.length()).trim(),
				command.getName() + ": " + command.getDescription(), command.getGroup(), null, null, null, true);
	}

	private boolean isOptionPresent(ParsedLine line, CommandOption option) {
		return option.longName() != null
				&& (line.line().contains(" --" + option.longName() + " ")
						|| line.line().contains(" --" + option.longName() + "="))
				|| option.shortName() != ' ' && (line.line().contains(" -" + option.shortName() + " ")
						|| line.line().contains(" -" + option.shortName() + "="));
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

	@Nullable private CommandOption findOptionByWords(List<String> words, List<CommandOption> options) {
		List<String> reversed = new ArrayList<>(words);
		Collections.reverse(reversed);
		String optionName = reversed.stream()
			.filter(word -> !word.trim().isEmpty())
			.findFirst()
			.filter(word -> !word.contains("=") || !reversed.get(0).isEmpty())
			.orElse("");

		return options.stream().filter(option -> isOptionEqual(optionName, option)).findFirst().orElse(null);
	}

	private static boolean isOptionEqual(String optionName, CommandOption option) {
		return option.longName() != null
				&& (optionName.equals("--" + option.longName())
						|| optionName.startsWith("--" + option.longName() + "="))
				|| option.shortName() != ' ' && (optionName.equals("-" + option.shortName())
						|| optionName.startsWith("-" + option.shortName() + "="));
	}

}
