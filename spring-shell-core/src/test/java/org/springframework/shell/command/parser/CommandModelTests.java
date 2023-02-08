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

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.shell.command.parser.ParserConfig.Feature;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link CommandModel}.
 *
 * @author Janne Valkealahti
 */
class CommandModelTests extends AbstractParsingTests {

	@Test
	void oneRootCommand() {
		register(ROOT1);
		CommandModel model = commandModel();

		Map<String, Token> tokens = model.getValidRootTokens();
		assertThat(tokens).hasSize(1);
		assertThat(tokens.get("root1")).satisfies(token -> {
			assertThat(token).isNotNull();
			assertThat(token.getValue()).isEqualTo("root1");
			assertThat(token.getType()).isEqualTo(TokenType.COMMAND);
		});
	}

	@Test
	void oneRootCommandCaseInsensitive() {
		register(ROOT1_UP);
		ParserConfig configuration = new ParserConfig()
				.disable(Feature.CASE_SENSITIVE_COMMANDS)
				.disable(Feature.CASE_SENSITIVE_OPTIONS);
		CommandModel model = commandModel(configuration);

		Map<String, Token> tokens = model.getValidRootTokens();
		assertThat(tokens).hasSize(1);
		assertThat(tokens.get("root1")).satisfies(token -> {
			assertThat(token).isNotNull();
			assertThat(token.getValue()).isEqualTo("root1");
			assertThat(token.getType()).isEqualTo(TokenType.COMMAND);
		});
	}

	@Test
	void onePlainSubCommand() {
		register(ROOT2_SUB1);
		CommandModel model = commandModel();

		Map<String, Token> tokens = model.getValidRootTokens();

		assertThat(tokens).hasSize(1);
		assertThat(tokens.get("root2")).satisfies(token -> {
			assertThat(token).isNotNull();
			assertThat(token.getValue()).isEqualTo("root2");
			assertThat(token.getType()).isEqualTo(TokenType.COMMAND);
		});

		assertThat(model.getRootCommand("root2")).satisfies(root1 -> {
			assertThat(root1).isNotNull();
			assertThat(root1.getChildren()).hasSize(1);
			assertThat(root1.registration).isNull();
			assertThat(root1.getChildren()).satisfiesExactly(
				sub1 -> {
					assertThat(sub1).isNotNull();
					assertThat(sub1.getChildren()).isEmpty();
					assertThat(sub1.registration).isNotNull();
				});
		});
	}

	@Test
	void onePlainSubCommand2() {
		register(ROOT2_SUB1);
		register(ROOT2_SUB2);
		CommandModel model = commandModel();

		Map<String, Token> tokens = model.getValidRootTokens();

		assertThat(tokens).hasSize(1);
		assertThat(tokens.get("root2")).satisfies(token -> {
			assertThat(token).isNotNull();
			assertThat(token.getValue()).isEqualTo("root2");
			assertThat(token.getType()).isEqualTo(TokenType.COMMAND);
		});

		assertThat(model.getRootCommand("root2")).satisfies(root1 -> {
			assertThat(root1.command).isEqualTo("root2");
			assertThat(root1.getChildren()).hasSize(2);
			assertThat(root1.registration).isNull();
			assertThat(root1.getValidTokens()).satisfies(map -> {
				assertThat(map).hasSize(2);
				assertThat(map).hasEntrySatisfying("sub1", token -> {
					assertThat(token.getType()).isEqualTo(TokenType.COMMAND);
				});
				assertThat(map).hasEntrySatisfying("sub2", token -> {
					assertThat(token.getType()).isEqualTo(TokenType.COMMAND);
				});
			});
			assertThat(root1.getChildren()).satisfiesExactlyInAnyOrder(
				sub1 -> {
					assertThat(sub1.command).isEqualTo("sub1");
					assertThat(sub1).isNotNull();
					assertThat(sub1.getChildren()).isEmpty();
					assertThat(sub1.registration).isNotNull();
				},
				sub2 -> {
					assertThat(sub2.command).isEqualTo("sub2");
					assertThat(sub2).isNotNull();
					assertThat(sub2.getChildren()).isEmpty();
					assertThat(sub2.registration).isNotNull();
				});
		});
	}

	@Test
	void onePlainSubCommand3() {
		register(ROOT2_SUB1_SUB2);
		register(ROOT2_SUB1_SUB3);
		register(ROOT2_SUB1_SUB4);
		CommandModel model = commandModel();

		Map<String, Token> tokens = model.getValidRootTokens();

		assertThat(tokens).hasSize(1);
		assertThat(tokens.get("root2")).satisfies(token -> {
			assertThat(token).isNotNull();
			assertThat(token.getValue()).isEqualTo("root2");
			assertThat(token.getType()).isEqualTo(TokenType.COMMAND);
		});

		assertThat(model.getRootCommand("root2")).satisfies(root1 -> {
			assertThat(root1.command).isEqualTo("root2");
			assertThat(root1.getChildren()).hasSize(1);
			assertThat(root1.registration).isNull();
			assertThat(root1.getValidTokens()).satisfies(map -> {
				assertThat(map).hasSize(1);
				assertThat(map).hasEntrySatisfying("sub1", token -> {
					assertThat(token.getType()).isEqualTo(TokenType.COMMAND);
				});
			});
			assertThat(root1.getChildren()).satisfiesExactlyInAnyOrder(
				sub1 -> {
					assertThat(sub1.command).isEqualTo("sub1");
					assertThat(sub1.getChildren()).satisfiesExactlyInAnyOrder(
						sub2 -> {
							assertThat(sub2.getChildren()).isEmpty();
						},
						sub3 -> {
							assertThat(sub3.getChildren()).isEmpty();
						},
						sub4 -> {
							assertThat(sub4.getChildren()).isEmpty();
						}
					);
					// assertThat(sub1).isNotNull();
					// assertThat(sub1.getChildren()).isEmpty();
					// assertThat(sub1.registration).isNotNull();
				}
			);
		});
	}

	@Test
	void shouldResolveCommand() {
		register(ROOT2_SUB1);
		CommandModel model = commandModel();

		assertThat(model.resolve(Arrays.asList("root2", "sub1"))).isNotNull();
	}

	@Test
	void shouldResolveParentCommand() {
		register(ROOT2);
		register(ROOT2_SUB1_SUB2);
		CommandModel model = commandModel();

		assertThat(model.resolve(Arrays.asList("root2"))).satisfies(
			info -> {
				assertThat(info.registration).isNotNull();
			}
		);
	}
}
