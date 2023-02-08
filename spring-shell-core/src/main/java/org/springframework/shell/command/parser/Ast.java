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
import java.util.List;

/**
 * Interface to generate abstract syntax tree from tokens. Generic language
 * parser usually contains lexing and parsing where this {@code Ast} represents
 * the latter parsing side.
 *
 * Parsing looks tokens and combines those together into nodes and we get
 * closer to understand commands, its options and arguments whether those
 * belong to command or option. Parser don't look if for example option
 * arguments makes sense which happen later when ast tree is visited.
 *
 * @author Janne Valkealahti
 */
public interface Ast {

	/**
	 * Generate ast result from a tokens. {@link AstResult} contains info about
	 * token to ast tree generation.
	 *
	 * @param tokens the tokens
	 * @return a result containing further syntax info
	 */
	AstResult generate(List<Token> tokens);

	/**
	 * Representing result from tokens to ast tree generation.
	 *
	 * @param nonterminalNodes list of nonterminal nodes
	 * @param terminalNodes list of terminal nodes
	 */
	public record AstResult(List<NonterminalAstNode> nonterminalNodes, List<TerminalAstNode> terminalNodes) {
	}

	/**
	 * Default implementation of an {@link Ast}.
	 */
	public class DefaultAst implements Ast {

		@Override
		public AstResult generate(List<Token> tokens) {
			List<CommandNode> commandNodes = new ArrayList<>();
			CommandNode commandNode = null;
			OptionNode optionNode = null;
			List<DirectiveNode> directiveNodes = new ArrayList<>();

			for (Token token : tokens) {
				switch (token.getType()) {
					case DIRECTIVE:
						String raw = token.getValue();
						String[] split = raw.split(":", 2);
						directiveNodes.add(new DirectiveNode(token, split[0], split.length > 1 ? split[1] : null));
						break;
					case COMMAND:
						CommandNode n = new CommandNode(token, token.getValue());
						if (commandNode == null) {
							commandNode = n;
						}
						else {
							commandNode.addChildNode(n);
							commandNode = n;
						}
						commandNodes.add(commandNode);
						break;
					case OPTION:
						optionNode = new OptionNode(token, token.getValue());
						commandNode.addChildNode(optionNode);
						break;
					case ARGUMENT:
						if (optionNode != null) {
							OptionArgumentNode optionArgumentNode = new OptionArgumentNode(token, optionNode, token.getValue());
							optionNode.addChildNode(optionArgumentNode);
						}
						else {
							CommandArgumentNode commandArgumentNode = new CommandArgumentNode(token, commandNode);
							commandNode.addChildNode(commandArgumentNode);
						}
						break;
					case DOUBLEDASH:
						optionNode = null;
						break;
					default:
						break;
				}
			}

			List<NonterminalAstNode> nonterminalNodes = new ArrayList<>();
			List<TerminalAstNode> terminalNodes = new ArrayList<>();

			if (commandNodes.size() > 0) {
				nonterminalNodes.add(commandNodes.get(0));
			}
			for (DirectiveNode dn : directiveNodes) {
				terminalNodes.add(dn);
			}

			return new AstResult(nonterminalNodes, terminalNodes);
		}
	}
}
