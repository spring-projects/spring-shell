/*
 * Copyright 2023-2024 the original author or authors.
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

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.shell.command.parser.Lexer.LexerResult;
import org.springframework.shell.command.parser.ParserConfig.Feature;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for default implementation in a {@link Lexer}.
 *
 * @author Janne Valkealahti
 */
class LexerTests extends AbstractParsingTests {

	@Nested
	class NoOptions {

		@Test
		void rootCommandNoOptions() {
			register(ROOT1);
			List<Token> tokens = tokenize("root1");

			assertThat(tokens).satisfiesExactly(
					token -> {
						ParserAssertions.assertThat(token)
							.isType(TokenType.COMMAND)
							.hasPosition(0)
							.hasValue("root1");
					});
		}
	}


	@Nested
	class CaseSensitivity {

		@Test
		void commandRegUpperCommandLower() {
			register(ROOT1_UP);
			ParserConfig config = new ParserConfig()
					.disable(Feature.CASE_SENSITIVE_COMMANDS)
					.disable(Feature.CASE_SENSITIVE_OPTIONS);
			List<Token> tokens = tokenize(lexer(config), "root1");

			assertThat(tokens).satisfiesExactly(
					token -> {
						ParserAssertions.assertThat(token)
							.isType(TokenType.COMMAND)
							.hasPosition(0)
							.hasValue("root1");
					});
		}

		@Test
		void commandRegLowerCommandUpper() {
			register(ROOT1);
			ParserConfig config = new ParserConfig()
					.disable(Feature.CASE_SENSITIVE_COMMANDS)
					.disable(Feature.CASE_SENSITIVE_OPTIONS);
			List<Token> tokens = tokenize(lexer(config), "Root1");

			assertThat(tokens).satisfiesExactly(
					token -> {
						ParserAssertions.assertThat(token)
							.isType(TokenType.COMMAND)
							.hasPosition(0)
							.hasValue("Root1");
					});
		}

	}


	// @Test
	// void commandWithCaseInsensitiveInArguments() {
	// 	register(ROOT1);
	// 	ParserConfig configuration = new ParserConfig()
	// 			.setOptionsCaseSensitive(false);
	// 	List<Token> tokens = tokenize(lexer(configuration), "ROOT1");

	// 	assertThat(tokens).satisfiesExactly(
	// 			token -> {
	// 				ParserAssertions.assertThat(token)
	// 					.isType(TokenType.COMMAND)
	// 					.hasPosition(0)
	// 					.hasValue("ROOT1");
	// 			});
	// }

	@Test
	void rootCommandWithChildGetsRootForRoot() {
		register(ROOT1);
		register(ROOT2_SUB1);
		List<Token> tokens = tokenize("root1");

		assertThat(tokens).satisfiesExactly(
			token -> {
				ParserAssertions.assertThat(token)
					.isType(TokenType.COMMAND)
					.hasPosition(0)
					.hasValue("root1");
			});
	}

	@Test
	void subCommandLevel1WithoutRoot() {
		register(ROOT2_SUB1);
		List<Token> tokens = tokenize("root2", "sub1");

		assertThat(tokens).satisfiesExactly(
			token -> {
				ParserAssertions.assertThat(token)
					.isType(TokenType.COMMAND)
					.hasPosition(0)
					.hasValue("root2");
			},
			token -> {
				ParserAssertions.assertThat(token)
					.isType(TokenType.COMMAND)
					.hasPosition(1)
					.hasValue("sub1");
			});
	}

	@Test
	void subCommandLevel2WithoutRoot() {
		register(ROOT2_SUB1_SUB2);
		List<Token> tokens = tokenize("root2", "sub1", "sub2");

		assertThat(tokens).satisfiesExactly(
			token -> {
				ParserAssertions.assertThat(token)
					.isType(TokenType.COMMAND)
					.hasPosition(0)
					.hasValue("root2");
			},
			token -> {
				ParserAssertions.assertThat(token)
					.isType(TokenType.COMMAND)
					.hasPosition(1)
					.hasValue("sub1");
			},
			token -> {
				ParserAssertions.assertThat(token)
					.isType(TokenType.COMMAND)
					.hasPosition(2)
					.hasValue("sub2");
			});
	}

	@Test
	void subCommandLevel2WithoutRootWithOption() {
		register(ROOT2_SUB1_SUB2);
		List<Token> tokens = tokenize("root2", "sub1", "sub2", "--arg1", "xxx");

		assertThat(tokens).satisfiesExactly(
			token -> {
				ParserAssertions.assertThat(token)
					.isType(TokenType.COMMAND)
					.hasPosition(0)
					.hasValue("root2");
			},
			token -> {
				ParserAssertions.assertThat(token)
					.isType(TokenType.COMMAND)
					.hasPosition(1)
					.hasValue("sub1");
			},
			token -> {
				ParserAssertions.assertThat(token)
					.isType(TokenType.COMMAND)
					.hasPosition(2)
					.hasValue("sub2");
			},
			token -> {
				ParserAssertions.assertThat(token)
					.isType(TokenType.OPTION)
					.hasPosition(3)
					.hasValue("--arg1");
			},
			token -> {
				ParserAssertions.assertThat(token)
					.isType(TokenType.ARGUMENT)
					.hasPosition(4)
					.hasValue("xxx");
			});
	}

	@Test
	void definedOptionShouldBeOptionAfterCommand() {
		register(ROOT3);
		List<Token> tokens = tokenize("root3", "--arg1");

		assertThat(tokens).satisfiesExactly(
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.COMMAND)
						.hasPosition(0)
						.hasValue("root3");
				},
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.OPTION)
						.hasPosition(1)
						.hasValue("--arg1");
				});
	}

	@Test
	void notDefinedOptionShouldBeOptionAfterCommand() {
		register(ROOT3);
		List<Token> tokens = tokenize("root3", "--arg2");

		assertThat(tokens).satisfiesExactly(
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.COMMAND)
						.hasPosition(0)
						.hasValue("root3");
				},
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.OPTION)
						.hasPosition(1)
						.hasValue("--arg2");
				});
	}

	@Test
	void notDefinedOptionShouldBeOptionAfterDefinedOption() {
		register(ROOT3);
		List<Token> tokens = tokenize("root3", "--arg1", "--arg2");

		assertThat(tokens).satisfiesExactly(
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.COMMAND)
						.hasPosition(0)
						.hasValue("root3");
				},
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.OPTION)
						.hasPosition(1)
						.hasValue("--arg1");
				},
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.OPTION)
						.hasPosition(2)
						.hasValue("--arg2");
				});
	}

	@Test
	void optionsWithoutValuesFromRoot() {
		register(ROOT5);
		List<Token> tokens = tokenize("root5", "--arg1", "--arg2");

		assertThat(tokens).satisfiesExactly(
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.COMMAND)
						.hasPosition(0)
						.hasValue("root5");
				},
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.OPTION)
						.hasPosition(1)
						.hasValue("--arg1");
				},
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.OPTION)
						.hasPosition(2)
						.hasValue("--arg2");
				});
	}

	@Test
	void optionsWithValuesFromRoot() {
		register(ROOT5);
		List<Token> tokens = tokenize("root5", "--arg1", "value1", "--arg2", "value2");

		assertThat(tokens).satisfiesExactly(
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.COMMAND)
						.hasPosition(0)
						.hasValue("root5");
				},
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.OPTION)
						.hasPosition(1)
						.hasValue("--arg1");
				},
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.ARGUMENT)
						.hasPosition(2)
						.hasValue("value1");
				},
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.OPTION)
						.hasPosition(3)
						.hasValue("--arg2");
				},
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.ARGUMENT)
						.hasPosition(4)
						.hasValue("value2");
				});
	}

	@Test
	void shortOptionWithValuesFromRoot() {
		register(ROOT3_SHORT_OPTION_A);
		List<Token> tokens = tokenize("root3", "-a", "value1");

		assertThat(tokens).satisfiesExactly(
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.COMMAND)
						.hasPosition(0)
						.hasValue("root3");
				},
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.OPTION)
						.hasPosition(1)
						.hasValue("-a");
				},
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.ARGUMENT)
						.hasPosition(2)
						.hasValue("value1");
				});
	}

	@Test
	void shortOptionsWithValuesFromRoot() {
		register(ROOT3_SHORT_OPTION_A_B);
		List<Token> tokens = tokenize("root3", "-a", "value1", "-b", "value2");

		assertThat(tokens).satisfiesExactly(
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.COMMAND)
						.hasPosition(0)
						.hasValue("root3");
				},
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.OPTION)
						.hasPosition(1)
						.hasValue("-a");
				},
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.ARGUMENT)
						.hasPosition(2)
						.hasValue("value1");
				},
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.OPTION)
						.hasPosition(3)
						.hasValue("-b");
				},
				token -> {
					ParserAssertions.assertThat(token)
						.isType(TokenType.ARGUMENT)
						.hasPosition(4)
						.hasValue("value2");
				});
	}

	@Test
	void optionValueFromRoot() {
		register(ROOT3);
		List<Token> tokens = tokenize("root3", "--arg1", "value1");

		assertThat(tokens).extracting(Token::getType).containsExactly(TokenType.COMMAND, TokenType.OPTION,
				TokenType.ARGUMENT);
	}

	@Nested
	class DoubleDash {

		@Test
		void doubleDashAddsCommandArguments() {
			register(ROOT3);
			List<Token> tokens = tokenize("root3", "--", "arg1");

			assertThat(tokens).extracting(Token::getType).containsExactly(TokenType.COMMAND, TokenType.DOUBLEDASH,
					TokenType.ARGUMENT);
		}
	}

	@Test
	void commandArgsWithoutOption() {
		register(ROOT3);
		List<Token> tokens = tokenize("root3", "value1", "value2");

		assertThat(tokens).extracting(Token::getType).containsExactly(TokenType.COMMAND, TokenType.ARGUMENT,
				TokenType.ARGUMENT);
	}

	@Nested
	class ShortOptions {

		@ParameterizedTest
		@ValueSource(strings = {
			"-1",
			"-1a",
			"-a1",
			"-ab1",
			"-ab1c"
		})
		void shouldNotBeOptionWhenDoesntLookLikeShortPosix(String arg) {
			register(ROOT6_OPTION_INT);
			List<Token> tokens = tokenize("root6", "--arg1", arg);

			assertThat(tokens).extracting(Token::getType).containsExactly(TokenType.COMMAND, TokenType.OPTION,
					TokenType.ARGUMENT);
		}

		@ParameterizedTest
		@ValueSource(strings = {
			"-a",
			"-ab",
			"-abc",
			"--abc"
		})
		void shouldBeOptionWhenLooksLikeShortPosix(String arg) {
			register(ROOT6_OPTION_INT);
			List<Token> tokens = tokenize("root6", "--arg1", arg);

			assertThat(tokens).extracting(Token::getType).containsExactly(TokenType.COMMAND, TokenType.OPTION,
					TokenType.OPTION);
		}
	}


	@Nested
	class Directives {

		@Test
		void directiveWithCommand() {
			register(ROOT1);
			ParserConfig config = new ParserConfig().enable(Feature.ALLOW_DIRECTIVES);
			List<Token> tokens = tokenize(lexer(config), "[fake]", "root1");

			assertThat(tokens).satisfiesExactly(
					token -> {
						ParserAssertions.assertThat(token)
							.isType(TokenType.DIRECTIVE)
							.hasPosition(0)
							.hasValue("fake");
					},
					token -> {
						ParserAssertions.assertThat(token)
							.isType(TokenType.COMMAND)
							.hasPosition(1)
							.hasValue("root1");
					});
		}

		@Test
		void hasOneDirective() {
			register(ROOT1);
			ParserConfig config = new ParserConfig().enable(Feature.ALLOW_DIRECTIVES);
			List<Token> tokens = tokenize(lexer(config), "[fake]");

			assertThat(tokens).satisfiesExactly(
					token -> {
						ParserAssertions.assertThat(token)
							.isType(TokenType.DIRECTIVE)
							.hasPosition(0)
							.hasValue("fake");
					});
		}

		@Test
		void hasMultipleDirectives() {
			register(ROOT1);
			ParserConfig config = new ParserConfig().enable(Feature.ALLOW_DIRECTIVES);
			List<Token> tokens = tokenize(lexer(config), "[fake1][fake2]");

			assertThat(tokens).satisfiesExactly(
					token -> {
						ParserAssertions.assertThat(token)
							.isType(TokenType.DIRECTIVE)
							.hasPosition(0)
							.hasValue("fake1");
					},
					token -> {
						ParserAssertions.assertThat(token)
							.isType(TokenType.DIRECTIVE)
							.hasPosition(0)
							.hasValue("fake2");
					});
		}

		@Test
		void errorIfDirectivesDisabledAndIgnoreDisabled() {
			ParserConfig config = new ParserConfig()
					.disable(Feature.ALLOW_DIRECTIVES)
					.disable(Feature.IGNORE_DIRECTIVES);
			LexerResult result = tokenizeAsResult(lexer(config),"[fake]");
			assertThat(result.messageResults()).isNotEmpty();
		}

		@Test
		void noErrorIfDirectivesDisabledAndIgnoreEnabled() {
			ParserConfig config = new ParserConfig()
					.enable(Feature.IGNORE_DIRECTIVES);
			LexerResult result = tokenizeAsResult(lexer(config), "[fake]");
			assertThat(result.messageResults()).isEmpty();
		}

		@Test
		void ignoreDirectiveIfDisabled() {
			register(ROOT1);
			List<Token> tokens = tokenize("[fake]", "root1");

			assertThat(tokens).extracting(Token::getType).containsExactly(TokenType.COMMAND);
		}
	}

	@Nested
	class ParserMessages {

		@Test
		void hasErrorMessageWithoutArguments() {
			LexerResult result = tokenizeAsResult("--");
			assertThat(result.messageResults()).satisfiesExactly(
					message -> {
						assertThat(message.parserMessage().getCode()).isEqualTo(1000);
						assertThat(message.position()).isEqualTo(0);
					},
					message -> {
						assertThat(message.parserMessage().getCode()).isEqualTo(1000);
						assertThat(message.position()).isEqualTo(0);
					});
		}
	}
}
