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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.shell.command.parser.Parser.ParseResult;

/**
 * Base abstract {@link NodeVisitor} which visits all nodes allowing user to
 * implement callback methods.
 *
 * @author Janne Valkealahti
 */
public abstract class AbstractNodeVisitor implements NodeVisitor {

	private final static Logger log = LoggerFactory.getLogger(AbstractNodeVisitor.class);

	@Override
	public final ParseResult visit(List<NonterminalAstNode> nonterminalNodes,
			List<TerminalAstNode> terminalNodes) {
		for (NonterminalAstNode ntn : nonterminalNodes) {
			log.debug("visit {}", ntn);
			if (ntn instanceof CommandNode node) {
				log.debug("onEnterRootCommandNode {}", node);
				onEnterRootCommandNode(node);
				visitChildren(node);
				log.debug("onExitRootCommandNode {}", node);
				onExitRootCommandNode(node);
			}
		}
		for (TerminalAstNode tn : terminalNodes) {
			if (tn instanceof DirectiveNode node) {
				enterDirectiveNode(node);
				exitDirectiveNode(node);
			}
		}
		return buildResult();
	}

	/**
	 * Called after all nodes has been visited to build results.
	 *
	 * @return the results from this visit operation
	 */
	protected abstract ParseResult buildResult();

	/**
	 * Called when {@link CommandNode} for root is entered. When node is fully
	 * visited, {@link #onExitRootCommandNode(CommandNode)} is called.
	 *
	 * @param node the command node
	 * @see #onExitRootCommandNode(CommandNode)
	 */
	protected abstract void onEnterRootCommandNode(CommandNode node);

	/**
	 * Called when {@link CommandNode} for root is exited.
	 *
	 * @param node the command node
	 * @see #onEnterRootCommandNode(CommandNode)
	 */
	protected abstract void onExitRootCommandNode(CommandNode node);

	/**
	 * Called when {@link CommandNode} is entered. When node is fully visited,
	 * {@link #onExitCommandNode(CommandNode)} is called.
	 *
	 * @param node the command node
	 * @see #onExitCommandNode(CommandNode)
	 */
	protected abstract void onEnterCommandNode(CommandNode node);

	/**
	 * Called when {@link CommandNode} is exited.
	 *
	 * @param node the command node
	 * @see #onEnterCommandNode(CommandNode)
	 */
	protected abstract void onExitCommandNode(CommandNode node);

	/**
	 * Called when {@link OptionNode} is entered. When node is fully visited,
	 * {@link #onExitOptionNode(OptionNode)} is called.
	 *
	 * @param node the option node
	 * @see #onExitOptionNode(OptionNode)
	 */
	protected abstract void onEnterOptionNode(OptionNode node);

	/**
	 * Called when {@link OptionNode} is exited.
	 *
	 * @param node the option node
	 * @see #onEnterOptionNode(OptionNode)
	 */
	protected abstract void onExitOptionNode(OptionNode node);

	/**
	 * Called when {@link CommandArgumentNode} is entered. When node is fully visited,
	 * {@link #onExitCommandArgumentNode(CommandArgumentNode)} is called.
	 *
	 * @param node the command argument node
	 * @see #onExitCommandArgumentNode(CommandArgumentNode)
	 */
	protected abstract void onEnterCommandArgumentNode(CommandArgumentNode node);

	/**
	 * Called when {@link CommandArgumentNode} is exited.
	 *
	 * @param node the command argument node
	 * @see #onEnterCommandArgumentNode(CommandArgumentNode)
	 */
	protected abstract void onExitCommandArgumentNode(CommandArgumentNode node);

	/**
	 * Called when {@link OptionArgumentNode} is entered. When node is fully visited,
	 * {@link #onExitOptionArgumentNode(OptionArgumentNode)} is called.
	 *
	 * @param node the option argument node
	 * @see #onExitOptionArgumentNode(OptionArgumentNode)
	 */
	protected abstract void onEnterOptionArgumentNode(OptionArgumentNode node);

	/**
	 * Called when {@link OptionArgumentNode} is exited.
	 *
	 * @param node the command argument node
	 * @see #onEnterOptionArgumentNode(OptionArgumentNode)
	 */
	protected abstract void onExitOptionArgumentNode(OptionArgumentNode node);

	/**
	 * Called when {@link DirectiveNode} is entered. When node is fully visited,
	 * {@link #onExitDirectiveNode(DirectiveNode)} is called.
	 *
	 * @param node the option node
	 * @see #onExitDirectiveNode(DirectiveNode)
	 */
	protected abstract void onEnterDirectiveNode(DirectiveNode node);

	/**
	 * Called when {@link DirectiveNode} is exited.
	 *
	 * @param node the option node
	 * @see #onEnterDirectiveNode(DirectiveNode)
	 */
	protected abstract void onExitDirectiveNode(DirectiveNode node);

	private void visitChildren(NonterminalAstNode node) {
		log.debug("visitChildren {}", node);
		for (AstNode syntaxNode : node.getChildren()) {
			visitInternal(syntaxNode);
		}
	}

	private void enterCommandNode(CommandNode node) {
		log.debug("enterCommandNode {}", node);
		onEnterCommandNode(node);
	}

	private void exitCommandNode(CommandNode node) {
		log.debug("exitCommandNode {}", node);
		onExitCommandNode(node);
	}

	private void enterOptionNode(OptionNode node) {
		log.debug("enterOptionNode {}", node);
		onEnterOptionNode(node);
	}

	private void exitOptionNode(OptionNode node) {
		log.debug("exitOptionNode {}", node);
		onExitOptionNode(node);
	}

	private void enterCommandArgumentNode(CommandArgumentNode node) {
		log.debug("enterCommandArgumentNode {}", node);
		onEnterCommandArgumentNode(node);
	}

	private void exitCommandArgumentNode(CommandArgumentNode node) {
		log.debug("exitCommandArgumentNode {}", node);
		onExitCommandArgumentNode(node);
	}

	private void enterOptionArgumentNode(OptionArgumentNode node) {
		log.debug("enterOptionArgumentNode {}", node);
		onEnterOptionArgumentNode(node);
	}

	private void exitOptionArgumentNode(OptionArgumentNode node) {
		log.debug("exitOptionArgumentNode {}", node);
		onExitOptionArgumentNode(node);
	}

	private void enterDirectiveNode(DirectiveNode node) {
		log.debug("enterDirectiveNode {}", node);
		onEnterDirectiveNode(node);
	}

	private void exitDirectiveNode(DirectiveNode node) {
		log.debug("exitDirectiveNode {}", node);
		onExitDirectiveNode(node);
	}

	private void visitInternal(AstNode node) {
		log.debug("visitInternal {}", node);
		if (node instanceof CommandNode n) {
			enterCommandNode(n);
			visitChildren(n);
			exitCommandNode(n);
		}
		else if (node instanceof OptionNode n) {
			enterOptionNode(n);
			visitChildren(n);
			exitOptionNode(n);
		}
		else if (node instanceof CommandArgumentNode n) {
			enterCommandArgumentNode(n);
			exitCommandArgumentNode(n);
		}
		else if (node instanceof OptionArgumentNode n) {
			enterOptionArgumentNode(n);
			exitOptionArgumentNode(n);
		}
	}
}
