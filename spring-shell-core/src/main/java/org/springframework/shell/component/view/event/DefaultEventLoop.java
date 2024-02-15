/*
 * Copyright 2023-2024 the original author or authors.
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.shell.component.message.ShellMessageHeaderAccessor;
import org.springframework.shell.component.message.StaticShellMessageHeaderAccessor;
import org.springframework.shell.component.view.control.View;
import org.springframework.shell.component.view.control.ViewEvent;
import org.springframework.shell.component.view.event.processor.AnimationEventLoopProcessor;
import org.springframework.shell.component.view.event.processor.TaskEventLoopProcessor;

/**
 * Default implementation of an {@link EventLoop}.
 *
 * @author Janne Valkealahti
 */
public class DefaultEventLoop implements EventLoop {

	private final static Logger log = LoggerFactory.getLogger(DefaultEventLoop.class);
	private final Queue<Message<?>> messageQueue = new PriorityQueue<>(MessageComparator.comparingPriority());
	private final Many<Message<?>> many = Sinks.many().unicast().onBackpressureBuffer(messageQueue);
	private Flux<Message<?>> sink;
	// private final Sinks.Many<Boolean> subscribedSignal = Sinks.many().replay().limit(1);
	private final Disposable.Composite disposables = Disposables.composite();
	private final Scheduler scheduler = Schedulers.boundedElastic();
	private volatile boolean active = true;
	private final List<EventLoopProcessor> processors;

	public DefaultEventLoop() {
		this(null);
	}

	public DefaultEventLoop(List<EventLoopProcessor> processors) {
		this.processors = new ArrayList<>();
		if (processors != null) {
			this.processors.addAll(processors);
		}
		this.processors.add(new AnimationEventLoopProcessor());
		this.processors.add(new TaskEventLoopProcessor());
		init();
	}

	private void init() {
		sink = many.asFlux()
			.flatMap(m -> {
				Flux<? extends Message<?>> pm = null;
				for (EventLoopProcessor processor : processors) {
					if (processor.canProcess(m)) {
						pm = processor.process(m);
						break;
					}
				}
				if (pm != null) {
					return pm;
				}
				return Mono.just(m);
			})
			.share();
	}

	@Override
	public void dispatch(Message<?> message) {
		log.debug("dispatch {}", message);
		if (!doSend(message, 1000)) {
			log.warn("Failed to send message: {}", message);
		}
	}

	@Override
	public void dispatch(Publisher<? extends Message<?>> messages) {
		subscribeTo(messages);
	}

	@Override
	public Flux<Message<?>> events() {
		return sink
			// .doFinally((s) -> subscribedSignal.tryEmitNext(sink.currentSubscriberCount() > 0))
		;
	}

	@Override
	public <T> Flux<T> events(EventLoop.Type type, Class<T> clazz) {
		return events()
			.filter(m -> type.equals(StaticShellMessageHeaderAccessor.getEventType(m)))
			.map(m -> m.getPayload())
			.ofType(clazz);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Flux<T> events(Type type, ParameterizedTypeReference<T> typeRef) {
		ResolvableType resolvableType = ResolvableType.forType(typeRef);
		return (Flux<T>) events()
			.filter(m -> type.equals(StaticShellMessageHeaderAccessor.getEventType(m)))
			.map(m -> m.getPayload())
			.ofType(resolvableType.getRawClass());
	}

	@Override
	public Flux<KeyEvent> keyEvents() {
		return events()
			.filter(m -> EventLoop.Type.KEY.equals(StaticShellMessageHeaderAccessor.getEventType(m)))
			.map(m -> m.getPayload())
			.ofType(KeyEvent.class);
	}

	@Override
	public Flux<MouseEvent> mouseEvents() {
		return events()
			.filter(m -> EventLoop.Type.MOUSE.equals(StaticShellMessageHeaderAccessor.getEventType(m)))
			.map(m -> m.getPayload())
			.ofType(MouseEvent.class);
	}

	@Override
	public Flux<String> systemEvents() {
		return events()
			.filter(m -> EventLoop.Type.SYSTEM.equals(StaticShellMessageHeaderAccessor.getEventType(m)))
			.map(m -> m.getPayload())
			.ofType(String.class);
	}

	@Override
	public Flux<String> signalEvents() {
		return events()
			.filter(m -> EventLoop.Type.SIGNAL.equals(StaticShellMessageHeaderAccessor.getEventType(m)))
			.map(m -> m.getPayload())
			.ofType(String.class);
	}

	@Override
	public <T extends ViewEvent> Flux<T> viewEvents(Class<T> clazz) {
		return events(EventLoop.Type.VIEW, clazz);
	}

	@Override
	public <T extends ViewEvent> Flux<T> viewEvents(ParameterizedTypeReference<T> typeRef) {
		return events(EventLoop.Type.VIEW, typeRef);
	}

	@Override
	public <T extends ViewEvent> Flux<T> viewEvents(Class<T> clazz, View filterBy) {
		return events(EventLoop.Type.VIEW, clazz).filter(args -> args.view() == filterBy);
	}

	@Override
	public <T extends ViewEvent> Flux<T> viewEvents(ParameterizedTypeReference<T> typeRef, View filterBy) {
		return events(EventLoop.Type.VIEW, typeRef).filter(args -> args.view() == filterBy);
	}

	@Override
	public void onDestroy(Disposable disposable) {
		disposables.add(disposable);
	}


	// @Override
	// public void subcribe(Flux<? extends Message<?>> messages) {
	// 	upstreamSubscriptions.add(
	// 	messages
	// 		// .delaySubscription(subscribedSignal.asFlux().filter(Boolean::booleanValue).next())
	// 		.subscribe()
	// 	);
	// }

	private boolean doSend(Message<?> message, long timeout) {
		if (!this.active || this.many.currentSubscriberCount() == 0) {
			return false;
		}
		// Assert.state(this.active && this.many.currentSubscriberCount() > 0,
		// 		() -> "The [" + this + "] doesn't have subscribers to accept messages");
		long remainingTime = 0;
		if (timeout > 0) {
			remainingTime = timeout;
		}
		long parkTimeout = 10;
		long parkTimeoutNs = TimeUnit.MILLISECONDS.toNanos(parkTimeout);
		while (this.active && !tryEmitMessage(message)) {
			remainingTime -= parkTimeout;
			if (timeout >= 0 && remainingTime <= 0) {
				return false;
			}
			LockSupport.parkNanos(parkTimeoutNs);
		}
		return true;
	}

	private boolean tryEmitMessage(Message<?> message) {
		return switch (many.tryEmitNext(message)) {
			case OK -> true;
			case FAIL_NON_SERIALIZED, FAIL_OVERFLOW -> false;
			case FAIL_ZERO_SUBSCRIBER ->
					throw new IllegalStateException("The [" + this + "] doesn't have subscribers to accept messages");
			case FAIL_TERMINATED, FAIL_CANCELLED ->
					throw new IllegalStateException("Cannot emit messages into the cancelled or terminated sink: " + many);
		};
	}

	// public void subscribe(Subscriber<? super Message<?>> subscriber) {
	// 	sink.asFlux()
	// 		.doFinally((s) -> subscribedSignal.tryEmitNext(sink.currentSubscriberCount() > 0))
	// 		.share()
	// 		.subscribe(subscriber);

	// 	upstreamSubscriptions.add(
	// 		Mono.fromCallable(() -> sink.currentSubscriberCount() > 0)
	// 				.filter(Boolean::booleanValue)
	// 				.doOnNext(subscribedSignal::tryEmitNext)
	// 				.repeatWhenEmpty((repeat) ->
	// 						active ? repeat.delayElements(Duration.ofMillis(100)) : repeat)
	// 				.subscribe());
	// }

	public void subscribeTo(Publisher<? extends Message<?>> publisher) {
		disposables.add(
			Flux.from(publisher)
				// .delaySubscription(subscribedSignal.asFlux().filter(Boolean::booleanValue).next())
				.publishOn(scheduler)
				.flatMap((message) ->
						Mono.just(message)
								.handle((messageToHandle, syncSink) -> sendReactiveMessage(messageToHandle))
								.contextWrite(StaticShellMessageHeaderAccessor.getReactorContext(message))
								)
				.contextCapture()
				.subscribe());
	}

	private void sendReactiveMessage(Message<?> message) {
		Message<?> messageToSend = message;
		// We have just restored Reactor context, so no need in a header anymore.
		if (messageToSend.getHeaders().containsKey(ShellMessageHeaderAccessor.REACTOR_CONTEXT)) {
			messageToSend =
					MessageBuilder.fromMessage(message)
							.removeHeader(ShellMessageHeaderAccessor.REACTOR_CONTEXT)
							.build();
		}
		try {
			dispatch(messageToSend);
			// if (!doSend(messageToSend, 1000)) {
			// 	log.warn("Failed to send message: {}", messageToSend);
			// }
		}
		catch (Exception ex) {
			log.warn("Error during processing event: {}", messageToSend);
		}
	}

	public void destroy() {
		this.active = false;
		this.disposables.dispose();
		// this.subscribedSignal.emitComplete(Sinks.EmitFailureHandler.FAIL_FAST);
		this.many.emitComplete(Sinks.EmitFailureHandler.FAIL_FAST);
		this.scheduler.dispose();
	}

	private static class MessageComparator implements Comparator<Message<?>> {

		@Override
		public int compare(Message<?> left, Message<?> right) {
			Integer l = StaticShellMessageHeaderAccessor.getPriority(left);
			Integer r = StaticShellMessageHeaderAccessor.getPriority(right);
			if (l != null && r != null) {
				return l.compareTo(r);
			}
			return 0;
		}

		static Comparator<Message<?>> comparingPriority() {
			return new MessageComparator();
		}
	}
}
