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

/**
 * Node representing an option argument.
 *
 * @author Janne Valkealahti
 */
public final class OptionArgumentNode extends TerminalAstNode {

	private final OptionNode parentOptionNode;
	private final String value;

	public OptionArgumentNode(Token token, OptionNode parentOptionNode, String value) {
		super(token);
		this.parentOptionNode = parentOptionNode;
		this.value = value;
	}

	public OptionNode getParentOptionNode() {
		return parentOptionNode;
	}

	public String getValue() {
		return value;
	}

}
