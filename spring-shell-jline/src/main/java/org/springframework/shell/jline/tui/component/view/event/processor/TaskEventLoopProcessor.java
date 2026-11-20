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
package org.springframework.shell.jline.tui.component.view.event.processor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.shell.jline.tui.component.TerminalEvent;
import org.springframework.shell.jline.tui.component.view.event.EventLoop;
import org.springframework.shell.jline.tui.component.view.event.EventLoop.EventLoopProcessor;
import org.springframework.shell.jline.tui.component.view.event.KeyBindingConsumerArgs;
import org.springframework.shell.jline.tui.component.view.event.MouseBindingConsumerArgs;

/**
 * @author Piotr Olaszewski
 */
public class TaskEventLoopProcessor implements EventLoopProcessor {

	@Override
	public boolean canProcess(TerminalEvent<?> terminalEvent) {
		if (EventLoop.Type.TASK == terminalEvent.type()) {
			Object payload = terminalEvent.payload();
			if (payload instanceof Runnable) {
				return true;
			}
			else if (payload instanceof KeyBindingConsumerArgs) {
				return true;
			}
			else if (payload instanceof MouseBindingConsumerArgs) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Flux<? extends TerminalEvent<?>> process(TerminalEvent<?> terminalEvent) {
		Object payload = terminalEvent.payload();
		if (payload instanceof Runnable) {
			return processRunnable(terminalEvent);
		}
		else if (payload instanceof KeyBindingConsumerArgs) {
			return processKeyConsumer(terminalEvent);
		}
		else if (payload instanceof MouseBindingConsumerArgs) {
			return processMouseConsumer(terminalEvent);
		}
		// should not happen
		throw new IllegalArgumentException();
	}

	private Flux<? extends TerminalEvent<?>> processRunnable(TerminalEvent<?> terminalEvent) {
		return Mono.just(terminalEvent.payload())
			.ofType(Runnable.class)
			.flatMap(Mono::fromRunnable)
			.then(Mono.just(new TerminalEvent<>(new Object(), terminalEvent.type())))
			.flux();
	}

	private Flux<? extends TerminalEvent<?>> processMouseConsumer(TerminalEvent<?> terminalEvent) {
		return Mono.just(terminalEvent.payload())
			.ofType(MouseBindingConsumerArgs.class)
			.flatMap(args -> Mono.fromRunnable(() -> args.consumer().accept(args.event())))
			.then(Mono.just(new TerminalEvent<>(new Object(), terminalEvent.type())))
			.flux();
	}

	private Flux<? extends TerminalEvent<?>> processKeyConsumer(TerminalEvent<?> terminalEvent) {
		return Mono.just(terminalEvent.payload())
			.ofType(KeyBindingConsumerArgs.class)
			.flatMap(args -> Mono.fromRunnable(() -> args.consumer().accept(args.event())))
			.then(Mono.just(new TerminalEvent<>(new Object(), terminalEvent.type())))
			.flux();
	}

}
