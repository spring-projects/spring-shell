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
import org.springframework.shell.core.utils.Utils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
					if (StringUtils.hasLength(option.longName()) && !present) {
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
				candidates.add(new Candidate(proposal.value(), proposal.displayText(), proposal.category(),
						proposal.description(), null, null, proposal.complete(), 0));
			}
		}
		else {
			this.commandRegistry.getCommandsByPrefix(line.line())
				.stream()
				.map(command -> toCommandCandidates(command, line.words()))
				.flatMap(List::stream)
				.sorted(Candidate::compareTo)
				.forEach(candidates::add);
		}
	}

	private List<Candidate> toCommandCandidates(Command command, List<String> words) {
		String prefix = words.size() > 1 ? String.join(" ", words.subList(0, words.size() - 1)) : "";
		return getCommandNames(command).filter(name -> name.startsWith(words.get(0)))
			.filter(name -> name.startsWith(prefix))
			.map(cmd -> new Candidate(cmd.substring(prefix.length()).trim(), cmd + ": " + command.getDescription(),
					command.getGroup(), null, null, null, !Utils.QUIT_COMMAND.equals(command)))
			.toList();
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

		Command command = this.commandRegistry.getCommandByName(commandName.toString().trim());
		// the command is found but was not completed on the line
		if (command != null && getCommandNames(command).toList().contains(String.join(" ", words))) {
			command = null;
		}
		return command;
	}

	@Nullable private CommandOption findOptionByWords(List<String> words, List<CommandOption> options) {
		List<String> reversed = new ArrayList<>(words);
		Collections.reverse(reversed);

		CommandOption option;
		if (reversed.get(0).isEmpty()) {
			// the option name was completed, but no value provided ---> "--optionName "
			option = findOption(options, o -> isOptionEqual(reversed.get(1), o));
		}
		else {
			// the option uses key-value pair ---> "--optionName=someValue"
			option = findOption(options, o -> isOptionStartWith(reversed.get(0), o));

			// the option uses completion on the value level ---> "--optionName someValue"
			if (option == null) {
				option = findOption(options, o -> isOptionEqual(reversed.get(1), o));
			}
		}

		return option;
	}

	@Nullable private CommandOption findOption(List<CommandOption> options, Predicate<CommandOption> optionFilter) {
		return options.stream().filter(optionFilter).findFirst().orElse(null);
	}

	private static boolean isOptionEqual(String optionName, CommandOption option) {
		return option.longName() != null && optionName.equals("--" + option.longName())
				|| option.shortName() != ' ' && optionName.equals("-" + option.shortName());
	}

	private static boolean isOptionStartWith(String optionName, CommandOption option) {
		return option.longName() != null && optionName.startsWith("--" + option.longName() + "=")
				|| option.shortName() != ' ' && optionName.startsWith("-" + option.shortName() + "=");
	}

	private Stream<String> getCommandNames(Command command) {
		return Stream.concat(Stream.of(command.getName()), command.getAliases().stream());
	}

}
