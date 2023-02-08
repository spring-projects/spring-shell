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

import java.util.List;

import org.springframework.shell.command.parser.Parser.ParseResult;

/**
 * Interface to vising nodes.
 *
 * @author Janne Valkealahti
 */
public interface NodeVisitor {

	/**
	 * Visit lists of non terminal and terminal nodes.
	 *
	 * @param nonterminalNodes non terminal nodes
	 * @param terminalNodes terminal nodes
	 * @return parser result
	 */
	ParseResult visit(List<NonterminalAstNode> nonterminalNodes, List<TerminalAstNode> terminalNodes);
}
