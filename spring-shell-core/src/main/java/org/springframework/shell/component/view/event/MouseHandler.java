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
package org.springframework.shell.component.view.event;

import java.util.function.Predicate;

import org.springframework.lang.Nullable;
import org.springframework.shell.component.view.control.View;

/**
 * Handles mouse events in a form of {@link MouseHandlerArgs} and returns
 * {@link MouseHandlerResult}. Typically used in a {@link View}.
 *
 * {@link MouseHandler} itself don't define any restrictions how it's used.
 *
 * @author Janne Valkealahti
 */
@FunctionalInterface
public interface MouseHandler {

	/**
	 * Handle mouse event wrapped in a {@link MouseHandlerArgs}.
	 *
	 * @param args the mouse handler arguments
	 * @return a handler result
	 */
	MouseHandlerResult handle(MouseHandlerArgs args);

	/**
	 * Returns a composed handler that first handles {@code this} handler and then
	 * handles {@code other} handler if {@code predicate} against result from
	 * {@code this} matches.
	 *
	 * @param other     the handler to handle after this handler
	 * @param predicate the predicate test against results from this
	 * @return a composed handler
	 */
	default MouseHandler thenConditionally(MouseHandler other, Predicate<MouseHandlerResult> predicate) {
		return args -> {
			MouseHandlerResult result = handle(args);
			if (predicate.test(result)) {
				return other.handle(args);
			}
			return result;
		};
    }

	/**
	 * Returns a composed handler that first handles {@code this} handler and then
	 * handles {@code other} if {@code this} consumed an event.
	 *
	 * @param other the handler to handle after this handler
	 * @return a composed handler
	 */
    default MouseHandler thenIfConsumed(MouseHandler other) {
		return thenConditionally(other, result -> result.consumed());
    }

	/**
	 * Returns a composed handler that first handles {@code this} handler and then
	 * handles {@code other} if {@code this} did not consume an event.
	 *
	 * @param other the handler to handle after this handler
	 * @return a composed handler
	 */
    default MouseHandler thenIfNotConsumed(MouseHandler other) {
		return thenConditionally(other, result -> !result.consumed());
    }

	/**
     * Returns a handler that always returns a non-consumed result.
	 *
	 * @return a handler that always returns a non-consumed result
	 */
	static MouseHandler neverConsume() {
		return args -> resultOf(args.event(), false, null, null);
	}

	/**
	 * Construct {@link MouseHandlerArgs} from a {@link MouseEvent}.
	 *
	 * @param event the mouse event
	 * @return a mouse handler args
	 */
	static MouseHandlerArgs argsOf(MouseEvent event) {
		return new MouseHandlerArgs(event);
	}

	/**
	 * Arguments for a {@link MouseHandler}.
	 *
	 * @param event the mouse event
	 */
	record MouseHandlerArgs(MouseEvent event) {
	}

	/**
	 * Construct {@link MouseHandlerResult} from a {@link MouseEvent} and a
	 * {@link View}.
	 *
	 * @param event the mouse event
	 * @param consumed flag telling if event was consumed
	 * @param focus the view which is requesting focus
	 * @param capture the view which captured an event
	 * @return a mouse handler result
	 */
	static MouseHandlerResult resultOf(MouseEvent event, boolean consumed, View focus, View capture) {
		return new MouseHandlerResult(event, consumed, focus, capture);
	}

	/**
	 * Result from a {@link MouseHandler}.
	 *
	 * @param event the mouse event
	 * @param consumed flag telling if event was consumed
	 * @param focus the view which is requesting focus
	 * @param capture the view which captured an event
	 */
	record MouseHandlerResult(@Nullable MouseEvent event, boolean consumed, @Nullable View focus,
			@Nullable View capture) {
	}
}
