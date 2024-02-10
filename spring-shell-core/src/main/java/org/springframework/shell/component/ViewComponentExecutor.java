/*
 * Copyright 2024 the original author or authors.
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
package org.springframework.shell.component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.shell.component.ViewComponent.ViewComponentRun;

/**
 * Executor for {@code ViewComponent}. Purpose of this executor is to run
 * component in a thread so that it doesn't need to block from a command.
 *
 * @author Janne Valkealahti
 */
public class ViewComponentExecutor implements AutoCloseable {

	private final Logger log = LoggerFactory.getLogger(ViewComponentExecutor.class);
	private final SimpleAsyncTaskExecutor executor;
	private Future<?> future;

	public ViewComponentExecutor() {
		this.executor = new SimpleAsyncTaskExecutor();
	}

	@Override
	public void close() throws Exception {
		this.executor.close();
	}

	private static class FutureViewComponentRun implements ViewComponentRun {

		private Future<?> future;

		private FutureViewComponentRun(Future<?> future) {
			this.future = future;
		}

		@Override
		public void await() {
			try {
				this.future.get();
			} catch (InterruptedException e) {
			} catch (ExecutionException e) {
			}
		}

		@Override
		public void cancel() {
			this.future.cancel(true);
		}

		@Override
		public boolean isDone() {
			return this.future.isDone();
		}

	}

	/**
	 * Execute runnable and return state which can be used for further operations.
	 *
	 * @param runnable the runnable
	 * @return run state
	 */
	public ViewComponentRun start(Runnable runnable) {
		if (future != null && !future.isDone()) {
			throw new IllegalStateException("Can run component as there is existing one in non stopped state");
		}
		future = executor.submit(() -> {
			log.debug("About to run component");
			runnable.run();
			log.debug("Finished run component");
		});
		return new FutureViewComponentRun(future);
	}

	/**
	 * Stop a {@code ViewComponent} which has been previously started with this
	 * executor.
	 */
	public void stop() {
		if (future != null) {
			future.cancel(true);
		}
		future = null;
	}

}
