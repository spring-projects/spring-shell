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

import org.junit.jupiter.api.Test;

import org.springframework.shell.command.parser.Ast.AstResult;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for default implementation in an {@link Ast}.
 *
 * @author Janne Valkealahti
 */
class AstTests extends AbstractParsingTests {

	@Test
	void createsCommandNode() {
		register(ROOT1);
		Token root1 = token("root1", TokenType.COMMAND, 0);
		AstResult result = ast(root1);

		assertThat(result).isNotNull();
		assertThat(result.nonterminalNodes()).satisfiesExactly(
			node -> {
				assertThat(node).isInstanceOf(CommandNode.class);
				CommandNode cn = (CommandNode)node;
				assertThat(cn.getCommand()).isEqualTo("root1");
			});
	}

	@Test
	void createsMultipleCommandNodes() {
		register(ROOT2_SUB1_SUB2);
		Token root1 = token("root1", TokenType.COMMAND, 0);
		Token sub1 = token("sub1", TokenType.COMMAND, 1);
		Token sub2 = token("sub2", TokenType.COMMAND, 2);
		Token arg1 = token("--arg1", TokenType.OPTION, 3);
		Token value1 = token("xxx", TokenType.ARGUMENT, 4);
		AstResult result = ast(root1, sub1, sub2, arg1, value1);

		assertThat(result).isNotNull();
		assertThat(result.nonterminalNodes()).satisfiesExactly(
			n1 ->  {
				assertThat(n1).isInstanceOf(CommandNode.class);
				CommandNode cn1 = (CommandNode)n1;
				assertThat(cn1.getCommand()).isEqualTo("root1");
				assertThat(cn1.getChildren()).satisfiesExactly(
					n2 -> {
						assertThat(n2).isInstanceOf(CommandNode.class);
						CommandNode cn2 = (CommandNode)n2;
						assertThat(cn2.getCommand()).isEqualTo("sub1");
						assertThat(cn2.getChildren()).satisfiesExactly(
							n3 -> {
								assertThat(n3).isInstanceOf(CommandNode.class);
								CommandNode cn3 = (CommandNode)n3;
								assertThat(cn3.getCommand()).isEqualTo("sub2");
								assertThat(cn3.getChildren()).satisfiesExactly(
									n4 -> {
										assertThat(n4).isInstanceOf(OptionNode.class);
										OptionNode on4 = (OptionNode)n4;
										assertThat(on4.getChildren()).satisfiesExactly(
											n5 -> {
												assertThat(n5).isInstanceOf(OptionArgumentNode.class);
											}
										);
									}
								);
							}
						);
					});
			});
	}

	@Test
	void createsOptionNodeNoOptionArg() {
		register(ROOT3);
		Token root3 = token("root3", TokenType.COMMAND, 0);
		Token arg1 = token("--arg1", TokenType.OPTION, 1);
		AstResult result = ast(root3, arg1);

		assertThat(result).isNotNull();
		assertThat(result.nonterminalNodes()).hasSize(1);
		assertThat(result.nonterminalNodes().get(0)).isInstanceOf(CommandNode.class);
		assertThat(result.nonterminalNodes().get(0)).satisfies(n -> {
			CommandNode cn = (CommandNode)n;
			assertThat(cn.getCommand()).isEqualTo("root3");
			assertThat(cn.getChildren()).hasSize(1);
			assertThat(cn.getChildren())
				.filteredOn(on -> on instanceof OptionNode)
				.extracting(on -> {
					return ((OptionNode)on).getName();
				})
				.containsExactly("--arg1");
		});
	}

	@Test
	void createsOptionNodeWithOptionArg() {
		register(ROOT3);
		Token root3 = token("root3", TokenType.COMMAND, 0);
		Token arg1 = token("--arg1", TokenType.OPTION, 1);
		Token value1 = token("value1", TokenType.ARGUMENT, 2);
		AstResult result = ast(root3, arg1, value1);

		assertThat(result).isNotNull();
		assertThat(result.nonterminalNodes()).hasSize(1);
		assertThat(result.nonterminalNodes().get(0)).isInstanceOf(CommandNode.class);
		assertThat(result.nonterminalNodes().get(0)).satisfies(n -> {
			CommandNode cn = (CommandNode)n;
			assertThat(cn.getCommand()).isEqualTo("root3");
			assertThat(cn.getChildren()).hasSize(1);
			assertThat(cn.getChildren())
				.filteredOn(on -> on instanceof OptionNode)
				.extracting(on -> {
					return ((OptionNode)on).getName();
				})
				.containsExactly("--arg1");
			OptionNode on = (OptionNode) cn.getChildren().get(0);
			assertThat(on.getChildren()).hasSize(1);
			OptionArgumentNode oan = (OptionArgumentNode) on.getChildren().get(0);
			assertThat(oan.getValue()).isEqualTo("value1");
		});
	}

	@Test
	void createsOptionNodesWithTwoOptionArg() {
		register(ROOT3);
		Token root3 = token("root3", TokenType.COMMAND, 0);
		Token arg1 = token("--arg1", TokenType.OPTION, 1);
		Token value1 = token("value1", TokenType.ARGUMENT, 2);
		Token arg2 = token("--arg2", TokenType.OPTION, 3);
		Token value2 = token("value2", TokenType.ARGUMENT, 4);
		AstResult result = ast(root3, arg1, value1, arg2, value2);

		assertThat(result).isNotNull();
		assertThat(result.nonterminalNodes()).hasSize(1);
		assertThat(result.nonterminalNodes().get(0)).isInstanceOf(CommandNode.class);
		assertThat(result.nonterminalNodes().get(0)).satisfies(n -> {
			CommandNode cn = (CommandNode)n;
			assertThat(cn.getCommand()).isEqualTo("root3");
			assertThat(cn.getChildren()).hasSize(2);
			assertThat(cn.getChildren())
				.filteredOn(on -> on instanceof OptionNode)
				.extracting(on -> {
					return ((OptionNode)on).getName();
				})
				.containsExactly("--arg1", "--arg2");
			OptionNode on1 = (OptionNode) cn.getChildren().get(0);
			assertThat(on1.getChildren()).hasSize(1);
			OptionArgumentNode oan1 = (OptionArgumentNode) on1.getChildren().get(0);
			assertThat(oan1.getValue()).isEqualTo("value1");
			OptionNode on2 = (OptionNode) cn.getChildren().get(1);
			assertThat(on2.getChildren()).hasSize(1);
			OptionArgumentNode oan2 = (OptionArgumentNode) on2.getChildren().get(0);
			assertThat(oan2.getValue()).isEqualTo("value2");
		});
	}

	@Test
	void createsOptionNodeWithShortOptionArg() {
		register(ROOT3_SHORT_OPTION_A);
		Token root3 = token("root3", TokenType.COMMAND, 0);
		Token arg1 = token("-a", TokenType.OPTION, 1);
		Token value1 = token("value1", TokenType.ARGUMENT, 2);
		AstResult result = ast(root3, arg1, value1);

		assertThat(result).isNotNull();
		assertThat(result.nonterminalNodes()).hasSize(1);
		assertThat(result.nonterminalNodes().get(0)).isInstanceOf(CommandNode.class);
		assertThat(result.nonterminalNodes().get(0)).satisfies(n -> {
			CommandNode cn = (CommandNode)n;
			assertThat(cn.getCommand()).isEqualTo("root3");
			assertThat(cn.getChildren()).hasSize(1);
			assertThat(cn.getChildren())
				.filteredOn(on -> on instanceof OptionNode)
				.extracting(on -> {
					return ((OptionNode)on).getName();
				})
				.containsExactly("-a");
			OptionNode on = (OptionNode) cn.getChildren().get(0);
			assertThat(on.getChildren()).hasSize(1);
			OptionArgumentNode oan = (OptionArgumentNode) on.getChildren().get(0);
			assertThat(oan.getValue()).isEqualTo("value1");
		});
	}

	@Test
	void createsOptionNodesWithTwoShortOptionArg() {
		register(ROOT3_SHORT_OPTION_A_B);
		Token root3 = token("root3", TokenType.COMMAND, 0);
		Token arg1 = token("-a", TokenType.OPTION, 1);
		Token value1 = token("value1", TokenType.ARGUMENT, 2);
		Token arg2 = token("-b", TokenType.OPTION, 3);
		Token value2 = token("value2", TokenType.ARGUMENT, 4);
		AstResult result = ast(root3, arg1, value1, arg2, value2);

		assertThat(result).isNotNull();
		assertThat(result.nonterminalNodes()).hasSize(1);
		assertThat(result.nonterminalNodes().get(0)).isInstanceOf(CommandNode.class);
		assertThat(result.nonterminalNodes().get(0)).satisfies(n -> {
			CommandNode cn = (CommandNode)n;
			assertThat(cn.getCommand()).isEqualTo("root3");
			assertThat(cn.getChildren()).hasSize(2);
			assertThat(cn.getChildren())
				.filteredOn(on -> on instanceof OptionNode)
				.extracting(on -> {
					return ((OptionNode)on).getName();
				})
				.containsExactly("-a", "-b");
			OptionNode on1 = (OptionNode) cn.getChildren().get(0);
			assertThat(on1.getChildren()).hasSize(1);
			OptionArgumentNode oan1 = (OptionArgumentNode) on1.getChildren().get(0);
			assertThat(oan1.getValue()).isEqualTo("value1");
			OptionNode on2 = (OptionNode) cn.getChildren().get(1);
			assertThat(on2.getChildren()).hasSize(1);
			OptionArgumentNode oan2 = (OptionArgumentNode) on2.getChildren().get(0);
			assertThat(oan2.getValue()).isEqualTo("value2");
		});
	}

	@Test
	void createOptionNodesWhenNoOptionArguments() {
		register(ROOT3);
		Token root3 = token("root3", TokenType.COMMAND, 0);
		Token arg1 = token("--arg1", TokenType.OPTION, 1);
		Token arg2 = token("--arg2", TokenType.OPTION, 2);
		AstResult result = ast(root3, arg1, arg2);

		assertThat(result).isNotNull();
		assertThat(result.nonterminalNodes()).hasSize(1);
		assertThat(result.nonterminalNodes().get(0)).isInstanceOf(CommandNode.class);
		assertThat(result.nonterminalNodes().get(0)).satisfies(n -> {
			CommandNode cn = (CommandNode)n;
			assertThat(cn.getCommand()).isEqualTo("root3");
			assertThat(cn.getChildren()).hasSize(2);
			assertThat(cn.getChildren())
				.filteredOn(on -> on instanceof OptionNode)
				.extracting(on -> {
					return ((OptionNode)on).getName();
				})
				.containsExactly("--arg1", "--arg2");
		});
	}

	@Test
	void directiveNoValueWithCommand() {
		register(ROOT1);
		Token directive = token("fake", TokenType.DIRECTIVE, 0);
		Token root1 = token("root1", TokenType.COMMAND, 1);
		AstResult result = ast(directive, root1);

		assertThat(result.terminalNodes()).hasSize(1);
		assertThat(result.terminalNodes().get(0)).isInstanceOf(DirectiveNode.class);
		DirectiveNode dn = (DirectiveNode) result.terminalNodes().get(0);
		assertThat(dn.getName()).isEqualTo("fake");
		assertThat(dn.getValue()).isNull();
	}

	@Test
	void directiveWithValueWithCommand() {
		register(ROOT1);
		Token directive = token("fake:value", TokenType.DIRECTIVE, 0);
		Token root1 = token("root1", TokenType.COMMAND, 1);
		AstResult result = ast(directive, root1);

		assertThat(result.terminalNodes()).hasSize(1);
		assertThat(result.terminalNodes().get(0)).isInstanceOf(DirectiveNode.class);
		DirectiveNode dn = (DirectiveNode) result.terminalNodes().get(0);
		assertThat(dn.getName()).isEqualTo("fake");
		assertThat(dn.getValue()).isEqualTo("value");
	}

	@Test
	void multipleDirectivesWithValueWithCommand() {
		register(ROOT1);
		Token directive1 = token("fake1:value1", TokenType.DIRECTIVE, 0);
		Token directive2 = token("fake2:value2", TokenType.DIRECTIVE, 1);
		Token root1 = token("root1", TokenType.COMMAND, 2);
		AstResult result = ast(directive1, directive2, root1);

		assertThat(result.terminalNodes()).hasSize(2);
		assertThat(result.terminalNodes().get(0)).isInstanceOf(DirectiveNode.class);
		DirectiveNode dn1 = (DirectiveNode) result.terminalNodes().get(0);
		assertThat(dn1.getName()).isEqualTo("fake1");
		assertThat(dn1.getValue()).isEqualTo("value1");
		DirectiveNode dn2 = (DirectiveNode) result.terminalNodes().get(1);
		assertThat(dn2.getName()).isEqualTo("fake2");
		assertThat(dn2.getValue()).isEqualTo("value2");
	}

}
