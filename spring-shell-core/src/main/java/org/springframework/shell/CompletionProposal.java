/*
 * Copyright 2016 the original author or authors.
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

package org.springframework.shell;

/**
 * Represents a proposal for TAB completion, made not only of the text to append, but also metadata about the proposal.
 *
 * @author Eric Bottard
 */
public class CompletionProposal {

	/**
	 * The actual value of the proposal.
	 */
	private String value;

	/**
	 * The text displayed while the proposal is being considered.
	 */
	private String displayText;

	/**
	 * The description for the proposal.
	 */
	private String description;

	/**
	 * The category of the proposal, which may be used to group proposals together.
	 */
	private String category;

	/**
	 * Whether the proposal should bypass escaping and quoting rules. This is useful for command proposals, which can
	 * appear as true multi-word Strings.
	 */
	private boolean dontQuote = false;

	/**
	 * Whether the proposal cant be completed further. By setting complete to false then it will not append an space
	 * making it easier to continue tab completion
	 */
	private boolean complete = true;

	public CompletionProposal(String value) {
		this.value = this.displayText = value;
	}

	public String value() {
		return value;
	}

	public CompletionProposal value(String value) {
		this.value = value;
		return this;
	}

	public String displayText() {
		return displayText;
	}

	public CompletionProposal displayText(String displayText) {
		this.displayText = displayText;
		return this;
	}

	public String description() {
		return description;
	}

	public CompletionProposal description(String description) {
		this.description = description;
		return this;
	}

	public String category() {
		return category;
	}

	public CompletionProposal category(String category) {
		this.category = category;
		return this;
	}

	public CompletionProposal complete(boolean complete) {
		this.complete = complete;
		return this;
	}

	public boolean complete() {
		return complete;
	}


	public CompletionProposal dontQuote(boolean dontQuote) {
		this.dontQuote = dontQuote;
		return this;
	}

	public boolean dontQuote() {
		return dontQuote;
	}

	@Override
	public String toString() {
		return value;
	}
}
