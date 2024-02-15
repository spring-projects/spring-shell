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

import java.time.Duration;
import java.util.Arrays;
import java.util.EnumSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.view.event.EventLoop.EventLoopProcessor;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultEventLoopTests {

	private final static Logger log = LoggerFactory.getLogger(DefaultEventLoopTests.class);
	private DefaultEventLoop loop;

	@AfterEach
	void clean() {
		if (loop != null) {
			// TODO: gh898
			try {
				loop.destroy();
			} catch (Exception e) {
				log.error("Error calling loop destroy", e);
			}
		}
		loop = null;
	}

	private void initDefault() {
		loop = new DefaultEventLoop();
	}

	@Test
	void eventsGetIntoSingleSubscriber() {
		initDefault();
		Message<String> message = MessageBuilder.withPayload("TEST").build();

		StepVerifier verifier1 = StepVerifier.create(loop.events())
			.expectNextCount(1)
			.thenCancel()
			.verifyLater();

		loop.dispatch(message);
		verifier1.verify(Duration.ofSeconds(1));
	}

	@Test
	void eventsGetIntoMultipleSubscriber() {
		initDefault();
		Message<String> message = MessageBuilder.withPayload("TEST").build();

		StepVerifier verifier1 = StepVerifier.create(loop.events())
			.expectNextCount(1)
			.thenCancel()
			.verifyLater();

		StepVerifier verifier2 = StepVerifier.create(loop.events())
			.expectNextCount(1)
			.thenCancel()
			.verifyLater();

		loop.dispatch(message);
		verifier1.verify(Duration.ofSeconds(1));
		verifier2.verify(Duration.ofSeconds(1));
	}

	@Test
	void canDispatchFlux() {
		initDefault();
		Message<String> message = MessageBuilder.withPayload("TEST").build();
		Flux<Message<String>> flux = Flux.just(message);

		StepVerifier verifier1 = StepVerifier.create(loop.events())
			.expectNextCount(1)
			.thenCancel()
			.verifyLater();

		loop.dispatch(flux);
		verifier1.verify(Duration.ofSeconds(1));
	}

	@Test
	void canDispatchMono() {
		initDefault();
		Message<String> message = MessageBuilder.withPayload("TEST").build();
		Mono<Message<String>> mono = Mono.just(message);

		StepVerifier verifier1 = StepVerifier.create(loop.events())
			.expectNextCount(1)
			.thenCancel()
			.verifyLater();

		loop.dispatch(mono);
		verifier1.verify(Duration.ofSeconds(1));
	}


	@Test
	void dispatchNoSubscribersDoesNotError() {
		initDefault();
		Message<String> message = MessageBuilder.withPayload("TEST").build();

		loop.dispatch(message);
	}

	@Test
	void subsribtionCompletesWhenLoopDestroyed() {
		initDefault();
		StepVerifier verifier1 = StepVerifier.create(loop.events())
			.expectComplete()
			.verifyLater();

		loop.destroy();
		verifier1.verify(Duration.ofSeconds(1));
	}

	static class TestEventLoopProcessor implements EventLoopProcessor {

		int count;

		@Override
		public boolean canProcess(Message<?> message) {
			return true;
		}

		@Override
		public Flux<? extends Message<?>> process(Message<?> message) {
			Message<?> m = MessageBuilder.fromMessage(message)
				.setHeader("count", count++)
				.build();
			return Flux.just(m);
		}
	}

	@Test
	void processorCreatesSameMessagesForAll() {
		TestEventLoopProcessor processor = new TestEventLoopProcessor();
		loop = new DefaultEventLoop(Arrays.asList(processor));

		StepVerifier verifier1 = StepVerifier.create(loop.events())
			.assertNext(m -> {
				Integer count = m.getHeaders().get("count", Integer.class);
				assertThat(count).isEqualTo(0);
			})
			.thenCancel()
			.verifyLater();

		StepVerifier verifier2 = StepVerifier.create(loop.events())
			.assertNext(m -> {
				Integer count = m.getHeaders().get("count", Integer.class);
				assertThat(count).isEqualTo(0);
			})
			.thenCancel()
			.verifyLater();

		Message<String> message = MessageBuilder.withPayload("TEST").build();
		loop.dispatch(message);
		verifier1.verify(Duration.ofSeconds(1));
		verifier2.verify(Duration.ofSeconds(1));
	}

	@Test
	void taskRunnableShouldExecute() {
		initDefault();
		TestRunnable task = new TestRunnable();
		Message<TestRunnable> message = ShellMessageBuilder.withPayload(task).setEventType(EventLoop.Type.TASK).build();
		StepVerifier verifier1 = StepVerifier.create(loop.events())
			.expectNextCount(1)
			.thenCancel()
			.verifyLater();
		loop.dispatch(message);
		verifier1.verify(Duration.ofSeconds(1));
		assertThat(task.count).isEqualTo(1);
	}

	static class TestRunnable implements Runnable {
		int count = 0;

		@Override
		public void run() {
			count++;
		}
	}

	@Test
	void keyEvents() {
		initDefault();

		KeyEvent event = KeyEvent.of(KeyEvent.Key.a);
		Message<KeyEvent> message = ShellMessageBuilder.ofKeyEvent(event);

		StepVerifier verifier1 = StepVerifier.create(loop.keyEvents())
			.expectNextCount(1)
			.thenCancel()
			.verifyLater();

		loop.dispatch(message);
		verifier1.verify(Duration.ofSeconds(1));
	}

	@Test
	void mouseEvents() {
		initDefault();

		org.jline.terminal.MouseEvent jlineMouseEvent = new org.jline.terminal.MouseEvent(
				org.jline.terminal.MouseEvent.Type.Released,
				org.jline.terminal.MouseEvent.Button.Button1,
				EnumSet.noneOf(org.jline.terminal.MouseEvent.Modifier.class), 0, 0);
		MouseEvent event = MouseEvent.of(jlineMouseEvent);
		Message<MouseEvent> message = ShellMessageBuilder.ofMouseEvent(event);

		StepVerifier verifier1 = StepVerifier.create(loop.mouseEvents())
			.expectNextCount(1)
			.thenCancel()
			.verifyLater();

		loop.dispatch(message);
		verifier1.verify(Duration.ofSeconds(1));
	}

	@Test
	void systemEvents() {
		initDefault();

		Message<String> message = ShellMessageBuilder.ofRedraw();

		StepVerifier verifier1 = StepVerifier.create(loop.systemEvents())
			.expectNextCount(1)
			.thenCancel()
			.verifyLater();

		loop.dispatch(message);
		verifier1.verify(Duration.ofSeconds(1));
	}

	@Test
	void signalEvents() {
		initDefault();

		Message<String> message = ShellMessageBuilder.ofSignal("WINCH");

		StepVerifier verifier1 = StepVerifier.create(loop.signalEvents())
			.expectNextCount(1)
			.thenCancel()
			.verifyLater();

		loop.dispatch(message);
		verifier1.verify(Duration.ofSeconds(1));
	}

}
