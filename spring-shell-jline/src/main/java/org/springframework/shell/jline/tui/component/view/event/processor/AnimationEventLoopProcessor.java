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

import java.time.Duration;

import reactor.core.publisher.Flux;

import org.springframework.shell.jline.tui.component.TerminalEvent;
import org.springframework.shell.jline.tui.component.view.event.EventLoop;
import org.springframework.shell.jline.tui.component.view.event.EventLoop.EventLoopProcessor;

/**
 * {@link EventLoopProcessor} converting incoming message into animation tick messages.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 */
public class AnimationEventLoopProcessor implements EventLoopProcessor {

	@Override
	public boolean canProcess(TerminalEvent<?> terminalEvent) {
		if (EventLoop.Type.SYSTEM == terminalEvent.type()) {
			return true;
		}
		return false;
	}

	@Override
	public Flux<? extends TerminalEvent<?>> process(TerminalEvent<?> terminalEvent) {
		return Flux.range(0, 40).delayElements(Duration.ofMillis(100)).map(i -> terminalEvent);
	}

}
