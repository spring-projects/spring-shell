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
package org.springframework.shell.component.view.event.processor;

import java.time.Duration;

import reactor.core.publisher.Flux;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.shell.component.message.ShellMessageHeaderAccessor;
import org.springframework.shell.component.message.StaticShellMessageHeaderAccessor;
import org.springframework.shell.component.view.event.EventLoop;
import org.springframework.shell.component.view.event.EventLoop.EventLoopProcessor;

/**
 * {@link EventLoopProcessor} converting incoming message into animation tick
 * messages.
 *
 * @author Janne Valkealahti
 */
public class AnimationEventLoopProcessor implements EventLoopProcessor {

	@Override
	public boolean canProcess(Message<?> message) {
		if (EventLoop.Type.SYSTEM.equals(StaticShellMessageHeaderAccessor.getEventType(message))) {
			if (message.getHeaders().containsKey("animationstart")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Flux<? extends Message<?>> process(Message<?> message) {
		return Flux.range(0, 40)
			.delayElements(Duration.ofMillis(100))
			.map(i -> {
				return MessageBuilder
					.withPayload(i)
					.setHeader(ShellMessageHeaderAccessor.EVENT_TYPE, EventLoop.Type.SYSTEM)
					.setHeader("animationtick", true)
					.setHeader("animationfrom", 0)
					.setHeader("animationto", 9)
					.build();
			});
	}
}
