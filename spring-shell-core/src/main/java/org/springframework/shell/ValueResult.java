/*
 * Copyright 2017 the original author or authors.
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

import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.MethodParameter;

/**
 * A result for a successful resolve operation.
 *
 * @author Camilo Gonzalez
 */
public class ValueResult {

	private final MethodParameter methodParameter;

	private final Object resolvedValue;

	private final BitSet wordsUsed;

	private final BitSet wordsUsedForValue;

	public ValueResult(MethodParameter methodParameter, Object resolvedValue) {
		this(methodParameter, resolvedValue, new BitSet(), new BitSet());
	}

	public ValueResult(MethodParameter methodParameter, Object resolvedValue, BitSet wordsUsed,
			BitSet wordsUsedForValue) {

		this.methodParameter = methodParameter;
		this.resolvedValue = resolvedValue;
		this.wordsUsed = wordsUsed == null ? new BitSet() : wordsUsed;
		this.wordsUsedForValue = wordsUsedForValue == null ? new BitSet() : wordsUsedForValue;
	}

	/**
	 * The {@link MethodParameter} that was the target of the resolve operation.
	 */
	public MethodParameter methodParameter() {
		return methodParameter;
	}

	/**
	 * Represents the resolved value for the {@link MethodParameter} associated with this result.
	 */
	public Object resolvedValue() {
		return resolvedValue;
	}

	/**
	 * Represents the full set of words used to resolve the {@link MethodParameter}. This includes
	 * any tags/keys consumed from the input.
	 */
	public BitSet wordsUsed() {
		return wordsUsed;
	}

	/**
	 * Represents the full set of words used to resolve the value of this {@link MethodParameter}.
	 */
	public BitSet wordsUsedForValue() {
		return wordsUsedForValue;
	}

	public List<String> wordsUsed(List<String> words) {
		return wordsUsed.stream().mapToObj(index -> words.get(index)).collect(Collectors.toList());
	}

	public List<String> wordsUsedForValue(List<String> words) {
		return wordsUsedForValue.stream().mapToObj(index -> words.get(index)).collect(Collectors.toList());
	}

}
