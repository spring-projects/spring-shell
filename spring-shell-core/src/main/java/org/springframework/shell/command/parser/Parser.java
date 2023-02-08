/*
 * Copyright 2023 the original author or authors.
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
package org.springframework.shell.command.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.shell.command.CommandOption;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.shell.command.parser.Ast.AstResult;
import org.springframework.shell.command.parser.CommandModel.CommandInfo;
import org.springframework.shell.command.parser.Lexer.LexerResult;
import org.springframework.shell.command.parser.Parser.ParseResult.ArgumentResult;
import org.springframework.shell.command.parser.Parser.ParseResult.OptionResult;
import org.springframework.shell.command.parser.ParserConfig.Feature;

/**
 * Interface to parse command line arguments.
 *
 * @author Janne Valkealahti
 */
public interface Parser {

	/**
	 * Parse given arguments into a {@link ParseResult}.
	 *
	 * @param arguments the command line arguments
	 * @return a parsed results
	 */
	ParseResult parse(List<String> arguments);


	/**
	 * Results from a {@link Parser} containing needed information like resolved
	 * {@link CommandRegistration}, list of {@link CommandOption} instances, errors
	 * and directive.
	 *
	 * @param commandRegistration command registration
	 * @param optionResults option results
	 * @param argumentResults argument results
	 * @param messageResults message results
	 * @param directiveResults directive result
	 */
	public record ParseResult(CommandRegistration commandRegistration, List<OptionResult> optionResults,
			List<ArgumentResult> argumentResults, List<MessageResult> messageResults,
			List<DirectiveResult> directiveResults) {

		public record OptionResult(CommandOption option, Object value) {

			public static OptionResult of(CommandOption option, Object value) {
				return new OptionResult(option, value);
			}
		}

		public record ArgumentResult(String value, int position) {

			public static ArgumentResult of(String value, int position) {
				return new ArgumentResult(value, position);
			}
		}
	}

	/**
	 * Default implementation of a {@link Parser}. Uses {@link Lexer} and
	 * {@link Ast}.
	 */
	public class DefaultParser implements Parser {

		private final ParserConfig config;
		private final CommandModel commandModel;
		private final Lexer lexer;
		private final Ast ast;
		private ConversionService conversionService;

		public DefaultParser(CommandModel commandModel, Lexer lexer, Ast ast) {
			this(commandModel, lexer, ast, new ParserConfig());
		}

		public DefaultParser(CommandModel commandModel, Lexer lexer, Ast ast, ParserConfig config) {
			this(commandModel, lexer, ast, config, null);
		}

		public DefaultParser(CommandModel commandModel, Lexer lexer, Ast ast, ParserConfig config,
				ConversionService conversionService) {
			this.commandModel = commandModel;
			this.lexer = lexer;
			this.ast = ast;
			this.config = config;
			this.conversionService = conversionService != null ? conversionService : new DefaultConversionService();
		}

		@Override
		public ParseResult parse(List<String> arguments) {
			// 1. tokenize arguments
			LexerResult lexerResult = lexer.tokenize(arguments);
			List<Token> tokens = lexerResult.tokens();

			// 2. generate syntax tree results from tokens
			//    result from it is then feed into node visitor
			AstResult astResult = ast.generate(tokens);

			// 3. visit nodes
			//    whoever uses this parser can then do further
			//    things with final parsing results
			NodeVisitor visitor = new DefaultNodeVisitor(commandModel, conversionService, config);
			ParseResult parseResult = visitor.visit(astResult.nonterminalNodes(), astResult.terminalNodes());
			parseResult.messageResults().addAll(lexerResult.messageResults());
			return parseResult;
		}
	}

	/**
	 * Default implementation of a {@link NodeVisitor}.
	 */
	class DefaultNodeVisitor extends AbstractNodeVisitor {

		private final CommandModel commandModel;
		private final ConversionService conversionService;
		private final ParserConfig config;
		private final List<MessageResult> commonMessageResults = new ArrayList<>();
		private List<String> resolvedCommmand = new ArrayList<>();
		private List<OptionResult> optionResults = new ArrayList<>();
		private List<String> currentOptionArgument = new ArrayList<>();
		private List<DirectiveResult> directiveResults = new ArrayList<>();
		private List<OptionNode> invalidOptionNodes = new ArrayList<>();
		private List<ArgumentResult> argumentResults = new ArrayList<>();
		private int commandArgumentPos = 0;

		DefaultNodeVisitor(CommandModel commandModel, ConversionService conversionService, ParserConfig config) {
			this.commandModel = commandModel;
			this.conversionService = conversionService;
			this.config = config;
		}

		@Override
		protected ParseResult buildResult() {
			CommandInfo info = commandModel.resolve(resolvedCommmand);
			CommandRegistration registration = info != null ? info.registration : null;

			List<MessageResult> messageResults = new ArrayList<>();
			if (registration != null) {
				messageResults.addAll(commonMessageResults);
				messageResults.addAll(validateOptionNotMissing(registration));
				messageResults.addAll(validateOptionIsValid(registration));


				// add options with default values
				Set<CommandOption> resolvedOptions1 = optionResults.stream()
					.map(or -> or.option())
					.collect(Collectors.toSet());
				registration.getOptions().stream()
					.filter(o -> o.getDefaultValue() != null)
					.filter(o -> !resolvedOptions1.contains(o))
					.forEach(o -> {
						resolvedOptions1.add(o);
						Object value = convertOptionType(o, o.getDefaultValue());
						optionResults.add(OptionResult.of(o, value));
					});

				List<CommandOption> optionsForArguments = registration.getOptions().stream()
					.filter(o -> !resolvedOptions1.contains(o))
					.filter(o -> o.getPosition() > -1)
					.filter(o -> o.getArityMin() > -1)
					.sorted(Comparator.comparingInt(o -> o.getPosition()))
					.collect(Collectors.toList());

				List<String> argumentValues = argumentResults.stream()
					.sorted(Comparator.comparingInt(ar -> ar.position()))
					.map(ar -> ar.value())
					.collect(Collectors.toList());

				int i = 0;
				for (CommandOption o : optionsForArguments) {
					int j = i + o.getArityMax();
					j = Math.min(argumentValues.size(), j);

					List<String> asdf = argumentValues.subList(i, j);
					if (asdf.isEmpty()) {
						optionResults.add(OptionResult.of(o, null));
					}
					else {
						Object value = convertOptionType(o, asdf);
						optionResults.add(OptionResult.of(o, value));
					}

					if (j == argumentValues.size()) {
						break;
					}
					i = j;
				}

			}

			return new ParseResult(registration, optionResults, argumentResults, messageResults, directiveResults);
		}

		@Override
		protected void onEnterDirectiveNode(DirectiveNode node) {
			directiveResults.add(DirectiveResult.of(node.getName(), node.getValue()));
		}

		@Override
		protected void onExitDirectiveNode(DirectiveNode node) {
		}

		@Override
		protected void onEnterRootCommandNode(CommandNode node) {
			resolvedCommmand.add(node.getCommand());
		}

		@Override
		protected void onExitRootCommandNode(CommandNode node) {
		}

		@Override
		protected void onEnterCommandNode(CommandNode node) {
			resolvedCommmand.add(node.getCommand());
		}

		@Override
		protected void onExitCommandNode(CommandNode node) {
		}

		private List<CommandOption> currentOptions = new ArrayList<>();

		@Override
		protected void onEnterOptionNode(OptionNode node) {
			commandArgumentPos = 0;
			currentOptions.clear();
			currentOptionArgument.clear();
			CommandInfo info = commandModel.resolve(resolvedCommmand);

			String name = node.getName();
			if (name.startsWith("--")) {
				info.registration.getOptions().forEach(option -> {
					Set<String> longNames = Arrays.asList(option.getLongNames()).stream()
						.map(n -> "--" + n)
						.collect(Collectors.toSet());
					String nameToMatch = config.isEnabled(Feature.CASE_SENSITIVE_OPTIONS) ? name : name.toLowerCase();
					boolean match = longNames.contains(nameToMatch);
					if (match) {
						currentOptions.add(option);
					}
				});
			}
			else if (name.startsWith("-")) {
				if (name.length() == 2) {
					info.registration.getOptions().forEach(option -> {
						Set<String> shortNames = Arrays.asList(option.getShortNames()).stream()
								.map(n -> "-" + Character.toString(n))
								.collect(Collectors.toSet());
						boolean match = shortNames.contains(name);
						if (match) {
							currentOptions.add(option);
						}
					});
				}
				else if (name.length() > 2) {
					info.registration.getOptions().forEach(option -> {
						Set<String> shortNames = Arrays.asList(option.getShortNames()).stream()
								.map(n -> "-" + Character.toString(n))
								.collect(Collectors.toSet());
						for (int i = 1; i < name.length(); i++) {
							boolean match = shortNames.contains("-" + name.charAt(i));
							if (match) {
								currentOptions.add(option);
							}
						}
					});
				}
			}
		}

		@Override
		protected void onExitOptionNode(OptionNode node) {
			if (!currentOptions.isEmpty()) {
				for (CommandOption currentOption : currentOptions) {
					int max = currentOption.getArityMax() > 0 ? currentOption.getArityMax() : Integer.MAX_VALUE;
					max = Math.min(max, currentOptionArgument.size());
					List<String> toUse = currentOptionArgument.subList(0, max);

					if (currentOption.getArityMin() > -1 && currentOptionArgument.size() < currentOption.getArityMin()) {
						String arg = currentOption.getLongNames()[0];
						commonMessageResults.add(MessageResult.of(ParserMessage.NOT_ENOUGH_OPTION_ARGUMENTS, 0, arg,
								currentOptionArgument.size()));
					}
					else if (currentOption.getArityMax() > -1 && currentOptionArgument.size() > currentOption.getArityMax()) {
						String arg = currentOption.getLongNames()[0];
						commonMessageResults.add(MessageResult.of(ParserMessage.TOO_MANY_OPTION_ARGUMENTS, 0, arg,
								currentOption.getArityMax()));
					}

					Object value = null;
					if (toUse.size() == 1) {
						value = toUse.get(0);
					}
					else if (toUse.size() > 1) {
						value = new ArrayList<>(toUse);
					}

					try {
						value = convertOptionType(currentOption, value);
					} catch (Exception e) {
						commonMessageResults.add(MessageResult.of(ParserMessage.ILLEGAL_OPTION_VALUE, 0, value, e.getMessage()));
					}
					optionResults.add(new OptionResult(currentOption, value));
				}
			}
			else {
				invalidOptionNodes.add(node);
			}
		}

		@Override
		protected void onEnterCommandArgumentNode(CommandArgumentNode node) {
			argumentResults.add(ArgumentResult.of(node.getToken().getValue(), commandArgumentPos++));
		}

		@Override
		protected void onExitCommandArgumentNode(CommandArgumentNode node) {
		}

		@Override
		protected void onEnterOptionArgumentNode(OptionArgumentNode node) {
			currentOptionArgument.add(node.getValue());
		}

		@Override
		protected void onExitOptionArgumentNode(OptionArgumentNode node) {
		}

		private Object convertOptionType(CommandOption option, Object value) {
			ResolvableType type = option.getType();
			if (value == null && type != null && type.isAssignableFrom(boolean.class)) {
				return true;
			}
			if (conversionService != null && option.getType() != null && value != null) {
				if (conversionService.canConvert(value.getClass(), option.getType().getRawClass())) {
					value = conversionService.convert(value, option.getType().getRawClass());
				}
			}
			return value;
		}

		private List<MessageResult> validateOptionNotMissing(CommandRegistration registration) {
			HashSet<CommandOption> requiredOptions = registration.getOptions().stream()
				.filter(o -> o.isRequired())
				.collect(Collectors.toCollection(() -> new HashSet<>()));

			optionResults.stream().map(or -> or.option()).forEach(o -> {
				requiredOptions.remove(o);
			});

			return requiredOptions.stream()
				.map(o -> {
					String ln = o.getLongNames() != null
							? Stream.of(o.getLongNames()).collect(Collectors.joining(","))
							: "";
					String sn = o.getShortNames() != null ? Stream.of(o.getShortNames()).map(n -> Character.toString(n))
							.collect(Collectors.joining(",")) : "";
					return MessageResult.of(ParserMessage.MANDATORY_OPTION_MISSING, 0, ln, sn);
				})
				.collect(Collectors.toList());
		}

		private List<MessageResult> validateOptionIsValid(CommandRegistration registration) {
			return invalidOptionNodes.stream()
				.map(on -> {
					return MessageResult.of(ParserMessage.UNRECOGNISED_OPTION, 0, on.getName());
				})
				.collect(Collectors.toList());
		}
	}
}
