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

import org.reactivestreams.Publisher;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.Message;
import org.springframework.shell.component.view.control.View;
import org.springframework.shell.component.view.control.ViewEvent;

/**
 * {@code EventLoop} is a central place where all eventing will be orchestrated
 * for a lifecycle of a component. Orchestration is usually needed around timings
 * of redraws and and component state updates.
 *
 * Generic message type is a Spring {@link Message} and it's up to an {@code EventLoop}
 * implementation how those are processed.
 *
 * @author Janne Valkealahti
 */
public interface EventLoop {

	/**
	 * Return a {@link Flux} of {@link Message} events. When subscribed events will
	 * be received until disposed or {@code EventLoop} terminates.
	 *
	 * @return the events from an event loop
	 */
	Flux<? extends Message<?>> events();

	/**
	 * Specialisation of {@link #events()} which returns type safe
	 * {@link KeyEvent}s.
	 *
	 * @return the key events from an event loop
	 */
	Flux<KeyEvent> keyEvents();

	/**
	 * Specialisation of {@link #events()} which returns type safe
	 * {@link MouseEvent}s.
	 *
	 * @return the mouse events from an event loop
	 */
	Flux<MouseEvent> mouseEvents();

	/**
	 * Specialisation of {@link #events()} which returns type safe
	 * {code system} events.
	 *
	 * @return the system events from an event loop
	 */
	Flux<String> systemEvents();

	/**
	 * Specialisation of {@link #events()} which returns type safe
	 * {code signal} events.
	 *
	 * @return the signal events from an event loop
	 */
	Flux<String> signalEvents();

	/**
	 * Specialisation of {@link #events()} which returns type safe {@link ViewEvent}s.
	 *
	 * @param <T> the type to expect
	 * @param clazz the type class to filter
	 * @return the filtered events from an event loop
	 */
	<T extends ViewEvent> Flux<T> viewEvents(Class<T> clazz);

	/**
	 * Specialisation of {@link #events()} which returns type safe {@link ViewEvent}s.
	 *
	 * @param <T> the type to expect
	 * @param typeRef the parameterized type to filter
	 * @return the filtered events from an event loop
	 */
	<T extends ViewEvent> Flux<T> viewEvents(ParameterizedTypeReference<T> typeRef);

	/**
	 * Specialisation of {@link #events()} which returns type safe {@link ViewEvent}s.
	 *
	 * @param <T> the type to expect
	 * @param clazz the type class to filter
	 * @param filterBy the view to filter
	 * @return the filtered events from an event loop
	 */
	<T extends ViewEvent> Flux<T> viewEvents(Class<T> clazz, View filterBy);

	/**
	 * Specialisation of {@link #events()} which returns type safe {@link ViewEvent}s.
	 *
	 * @param <T> the type to expect
	 * @param typeRef the parameterized type to filter
	 * @param filterBy the view to filter
	 * @return the filtered events from an event loop
	 */
	<T extends ViewEvent> Flux<T> viewEvents(ParameterizedTypeReference<T> typeRef, View filterBy);

	/**
	 * Specialisation of {@link #events()} which returns type safe
	 * stream filtered by given eventloop message type and message
	 * payload class type.
	 *
	 * @param <T> the type to expect
	 * @param type the eventloop message type to filter
	 * @param clazz the type class to filter
	 * @return the filtered events from an event loop
	 */
	<T> Flux<T> events(EventLoop.Type type, Class<T> clazz);

	/**
	 * Specialisation of {@link #events()} which returns type safe
	 * stream filtered by given eventloop message type and message
	 * payload class type.
	 *
	 * @param <T> the type to expect
	 * @param type the eventloop message type to filter
	 * @param typeRef the parameterized type to filter
	 * @return the filtered events from an event loop
	 */
	<T> Flux<T> events(EventLoop.Type type, ParameterizedTypeReference<T> typeRef);

	/**
	 * Dispatch {@link Message}s into an {@code EventLoop} from a {@link Publisher}.
	 * Usually type is either {@link Mono} or {@link Flux}.
	 *
	 * @param messages the messages to dispatch
	 */
	void dispatch(Publisher<? extends Message<?>> messages);

	/**
	 * Dispatch a {@link Message} into an {@code EventLoop}.
	 *
	 * @param message the message to dispatch
	 */
	void dispatch(Message<?> message);

	/**
	 * Register {@link Disposable} to get disposed when event loop terminates.
	 *
	 * @param disposable a disposable to dispose
	 */
	void onDestroy(Disposable disposable);

	/**
	 * Type of an events handled by an {@code EventLoop}.
	 */
	enum Type {

		/**
		 * Signals dispatched from a terminal.
		 */
		SIGNAL,

		/**
		 * Key bindings from a terminal.
		 */
		KEY,

		/**
		 * Mouse bindings from a terminal.
		 */
		MOUSE,

		/**
		 * System bindinds like redraw.
		 */
		SYSTEM,

		/**
		 * View bindinds from views.
		 */
		VIEW,

		/**
		 * User bindinds for custom events.
		 */
		USER,

		TASK
	}

	/**
	 * Contract to process event loop messages, possibly translating an event into
	 * some other type of event or events.
	 */
	interface EventLoopProcessor {

		/**
		 * Checks if this processor can process an event. If this method returns {@code true}
		 * it's quaranteed that {@link #process(Message)} is called to resolve translation
		 * of a message.
		 *
		 * @param message the message
		 * @return true if processor can process an event
		 */
		boolean canProcess(Message<?> message);

		/**
		 * Process a message and transform it into a new {@link Flux} of {@link Message}
		 * instances.
		 *
		 * @param message the message to process
		 * @return a flux of messages
		 */
		Flux<? extends Message<?>> process(Message<?> message);
	}
}
