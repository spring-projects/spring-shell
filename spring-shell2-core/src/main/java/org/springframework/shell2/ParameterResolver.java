/*
 * Copyright 2015 the original author or authors.
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

import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.MethodParameter;

/**
 * Implementations of this interface are responsible, once the command has been identified, of transforming the textual
 * input to an actual parameter object.
 */
public interface ParameterResolver {

	/**
	 * Should return true if this resolver recognizes the given method parameter (<em>e.g.</em> it
	 * has the correct annotation or the correct type).
	 */
	boolean supports(MethodParameter parameter);

	/**
	 * Turn the given textual input into an actual object, maybe using some conversion or lookup mechanism.
	 */
	ValueResult resolve(MethodParameter methodParameter, List<String> words);

	/**
	 * Describe a supported parameter, so that integrated help can be generated.
	 * <p>Typical implementations will return a one element stream result, but some may return several (for
	 * example if binding several words to a POJO).</p>
	 */
	Stream<ParameterDescription> describe(MethodParameter parameter);

	/**
	 * Invoked during TAB completion. If the {@link CompletionContext} can be interpreted as the start
	 * of a supported {@link MethodParameter} value, one or several proposals should be returned.
	 */
	List<CompletionProposal> complete(MethodParameter parameter, CompletionContext context);

}
