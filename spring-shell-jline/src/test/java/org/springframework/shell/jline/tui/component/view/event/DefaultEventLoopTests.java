/*
 * Copyright 2023-present the original author or authors.
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
package org.springframework.shell.jline.tui.component.view.event;

import java.time.Duration;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.shell.jline.tui.component.TerminalEvent;
import org.springframework.shell.jline.tui.component.view.event.EventLoop.EventLoopProcessor;
import static org.assertj.core.api.Assertions.*;

class DefaultEventLoopTests {

	private static final Log log = LogFactory.getLog(DefaultEventLoopTests.class);

	private DefaultEventLoop loop;

	@AfterEach
	void clean() {
		if (loop != null) {
			// TODO: gh898
			try {
				loop.destroy();
			}
			catch (Exception e) {
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
		TerminalEvent<String> terminalEvent = new TerminalEvent<>("TEST", EventLoop.Type.SIGNAL);

		StepVerifier verifier1 = StepVerifier.create(loop.events()).expectNextCount(1).thenCancel().verifyLater();

		loop.dispatch(terminalEvent);
		verifier1.verify(Duration.ofSeconds(1));
	}

	@Test
	void eventsGetIntoMultipleSubscriber() {
		initDefault();
		TerminalEvent<String> terminalEvent = new TerminalEvent<>("TEST", EventLoop.Type.SIGNAL);

		StepVerifier verifier1 = StepVerifier.create(loop.events()).expectNextCount(1).thenCancel().verifyLater();

		StepVerifier verifier2 = StepVerifier.create(loop.events()).expectNextCount(1).thenCancel().verifyLater();

		loop.dispatch(terminalEvent);
		verifier1.verify(Duration.ofSeconds(1));
		verifier2.verify(Duration.ofSeconds(1));
	}

	@Test
	void canDispatchFlux() {
		initDefault();
		TerminalEvent<String> terminalEvent = new TerminalEvent<>("TEST", EventLoop.Type.SIGNAL);
		Flux<TerminalEvent<String>> flux = Flux.just(terminalEvent);

		StepVerifier verifier1 = StepVerifier.create(loop.events()).expectNextCount(1).thenCancel().verifyLater();

		loop.dispatch(flux);
		verifier1.verify(Duration.ofSeconds(1));
	}

	@Test
	void canDispatchMono() {
		initDefault();
		TerminalEvent<String> terminalEvent = new TerminalEvent<>("TEST", EventLoop.Type.SIGNAL);
		Mono<TerminalEvent<String>> mono = Mono.just(terminalEvent);

		StepVerifier verifier1 = StepVerifier.create(loop.events()).expectNextCount(1).thenCancel().verifyLater();

		loop.dispatch(mono);
		verifier1.verify(Duration.ofSeconds(1));
	}

	@Test
	void dispatchNoSubscribersDoesNotError() {
		initDefault();
		TerminalEvent<String> terminalEvent = new TerminalEvent<>("TEST", EventLoop.Type.SIGNAL);

		loop.dispatch(terminalEvent);
	}

	@Test
	void subscriptionCompletesWhenLoopDestroyed() {
		initDefault();
		StepVerifier verifier1 = StepVerifier.create(loop.events()).expectComplete().verifyLater();

		loop.destroy();
		verifier1.verify(Duration.ofSeconds(1));
	}

	static class TestEventLoopProcessor implements EventLoopProcessor {

		int count;

		@Override
		public boolean canProcess(TerminalEvent<?> terminalEvent) {
			return true;
		}

		@Override
		public Flux<? extends TerminalEvent<?>> process(TerminalEvent<?> terminalEvent) {
			return Flux.just(new TerminalEvent<>(terminalEvent.payload(), EventLoop.Type.SIGNAL, null,
					Map.of("count", count++)));
		}

	}

	@Test
	void processorCreatesSameMessagesForAll() {
		TestEventLoopProcessor processor = new TestEventLoopProcessor();
		loop = new DefaultEventLoop(List.of(processor));

		StepVerifier verifier1 = StepVerifier.create(loop.events()).assertNext(m -> {
			Integer count = (Integer) m.attributes().get("count");
			assertThat(count).isZero();
		}).thenCancel().verifyLater();

		StepVerifier verifier2 = StepVerifier.create(loop.events()).assertNext(m -> {
			Integer count = (Integer) m.attributes().get("count");
			assertThat(count).isZero();
		}).thenCancel().verifyLater();

		TerminalEvent<String> terminalEvent = new TerminalEvent<>("TEST", EventLoop.Type.SIGNAL);
		loop.dispatch(terminalEvent);
		verifier1.verify(Duration.ofSeconds(1));
		verifier2.verify(Duration.ofSeconds(1));
	}

	@Test
	void taskRunnableShouldExecute() {
		initDefault();
		TestRunnable task = new TestRunnable();
		TerminalEvent<TestRunnable> terminalEvent = new TerminalEvent<>(task, EventLoop.Type.TASK);
		StepVerifier verifier1 = StepVerifier.create(loop.events()).expectNextCount(1).thenCancel().verifyLater();
		loop.dispatch(terminalEvent);
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
		TerminalEvent<KeyEvent> terminalEvent = new TerminalEvent<>(event, EventLoop.Type.KEY);

		StepVerifier verifier1 = StepVerifier.create(loop.keyEvents()).expectNextCount(1).thenCancel().verifyLater();

		loop.dispatch(terminalEvent);
		verifier1.verify(Duration.ofSeconds(1));
	}

	@Test
	void mouseEvents() {
		initDefault();

		org.jline.terminal.MouseEvent jlineMouseEvent = new org.jline.terminal.MouseEvent(
				org.jline.terminal.MouseEvent.Type.Released, org.jline.terminal.MouseEvent.Button.Button1,
				EnumSet.noneOf(org.jline.terminal.MouseEvent.Modifier.class), 0, 0);
		MouseEvent event = MouseEvent.of(jlineMouseEvent);
		TerminalEvent<MouseEvent> terminalEvent = new TerminalEvent<>(event, EventLoop.Type.MOUSE);

		StepVerifier verifier1 = StepVerifier.create(loop.mouseEvents()).expectNextCount(1).thenCancel().verifyLater();

		loop.dispatch(terminalEvent);
		verifier1.verify(Duration.ofSeconds(1));
	}

	@Test
	void systemEvents() {
		initDefault();

		TerminalEvent<String> terminalEvent = new TerminalEvent<>("redraw", EventLoop.Type.SYSTEM);

		StepVerifier verifier1 = StepVerifier.create(loop.systemEvents()).expectNextCount(1).thenCancel().verifyLater();

		loop.dispatch(terminalEvent);
		verifier1.verify(Duration.ofSeconds(1));
	}

	@Test
	void signalEvents() {
		initDefault();

		TerminalEvent<String> terminalEvent = new TerminalEvent<>("WINCH", EventLoop.Type.SIGNAL);

		StepVerifier verifier1 = StepVerifier.create(loop.signalEvents()).expectNextCount(1).thenCancel().verifyLater();

		loop.dispatch(terminalEvent);
		verifier1.verify(Duration.ofSeconds(1));
	}

}
