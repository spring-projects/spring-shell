/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.shell2;

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

	public CompletionProposal(String value) {
		this.value = this.displayText = value;
	}

	public String value() {
		return value;
	}

	public void value(String value) {
		this.value = value;
	}

	public String displayText() {
		return displayText;
	}

	public void displayText(String displayText) {
		this.displayText = displayText;
	}

	public String description() {
		return description;
	}

	public void description(String description) {
		this.description = description;
	}

	public String category() {
		return category;
	}

	public void category(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return value;
	}
}
