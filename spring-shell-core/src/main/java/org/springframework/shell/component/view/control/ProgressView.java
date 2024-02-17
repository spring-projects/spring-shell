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
package org.springframework.shell.component.view.control;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Flux;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.shell.component.message.ShellMessageBuilder;
import org.springframework.shell.component.message.ShellMessageHeaderAccessor;
import org.springframework.shell.component.message.StaticShellMessageHeaderAccessor;
import org.springframework.shell.component.view.control.cell.TextCell;
import org.springframework.shell.component.view.event.EventLoop;
import org.springframework.shell.component.view.screen.Screen;
import org.springframework.shell.geom.HorizontalAlign;
import org.springframework.shell.geom.Rectangle;
import org.springframework.shell.style.SpinnerSettings;
import org.springframework.shell.style.ThemeResolver;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * {@code ProgressView} is used to show a progress indicator.
 *
 * Defaults to <textItem> <spinnerItem> <percentItem>
 *
 * @author Janne Valkealahti
 */
public class ProgressView extends BoxView {

	private final static Logger log = LoggerFactory.getLogger(ProgressView.class);
	private final int tickStart;
	private final int tickEnd;
	private int tickValue;
	private boolean running = false;
	private String description;
	private Spinner spinner;
	private List<ProgressViewItem> items;
	private GridView grid;
	private long startTime;
	private long updateTime;
	private List<TextCell<ProgressContext>> cells = new ArrayList<>();

	private final static Function<ProgressContext, TextCell<ProgressContext>> DEFAULT_DESCRIPTION_FACTORY =
			(item) -> TextCell.of(item, ctx -> {
				return ctx.getDescription();
			});

	private final static Function<ProgressContext, TextCell<ProgressContext>> DEFAULT_PERCENT_FACTORY =
			(item) -> {
				TextCell<ProgressContext> cell = TextCell.of(item, ctx -> {
					ProgressState state = ctx.getState();
					int percentAbs = state.tickEnd() - state.tickStart();
					int relativeValue = state.tickValue() - state.tickStart();
					int percent = (relativeValue * 100) / percentAbs;
					return String.format("%s%%", percent);
				});
				return cell;
			};

	private final static Function<ProgressContext, TextCell<ProgressContext>> DEFAULT_SPINNER_FACTORY =
			item -> {
				TextCell<ProgressContext> cell = TextCell.of(item, ctx -> {
					int frame = 0;

					// via view setting, then via theming, then fallback default
					Spinner spin = ctx.resolveThemeSpinner(SpinnerSettings.TAG_DOT, ctx.getSpinner(),
							Spinner.of(Spinner.LINE1, 130));

					if (ctx.getState().running()) {
						// we know start time and current update time,
						// calculate elapsed time "frame" to pick rolling
						// spinner frame.
						int interval = spin.getInterval();
						long startTime = ctx.getState().startTime();
						long updateTime = ctx.getState().updateTime();
						long elapsedTime = updateTime - startTime;
						long elapsedFrame = elapsedTime / interval;
						frame = (int) elapsedFrame % spin.getFrames().length;
						log.debug("Calculate frame {} {} {}", interval, elapsedTime, elapsedFrame);
					}
					log.debug("Drawing frame {}", frame);

					return String.format("%s", spin.getFrames()[frame]);
				});
				return cell;
			};

	/**
	 * Construct view with {@code tickStart 0} and {@code tickEnd 100}. Uses default
	 * {@link ProgressViewItem}s.
	 */
	public ProgressView() {
		this(0, 100);
	}

	/**
	 * Construct view with {@code tickStart 0} and {@code tickEnd 100}. Uses default
	 * {@link ProgressViewItem}s.
	 *
	 * @param tickStart the tick start
	 * @param tickEnd   the tick end
	 */
	public ProgressView(int tickStart, int tickEnd) {
		this(tickStart, tickEnd, new ProgressViewItem[] { ProgressViewItem.ofText(), ProgressViewItem.ofSpinner(),
				ProgressViewItem.ofPercent() });
	}

	/**
	 * Construct view with given {@link ProgressViewItem}s using {@code tickStart 0}
	 * and {@code tickEnd 100}.
	 *
	 * @param items the progress view items
	 */
	public ProgressView(ProgressViewItem... items) {
		this(0, 100, items);
	}

	/**
	 * Construct view with given bounds for {@code tickStart} and {@code tickEnd}.
	 * {@code tickStart} needs to be equal or more than zero. {@code tickEnd} needs
	 * to be higher than {@code tickStart}. Uses defined progress view items.
	 *
	 * @param tickStart the tick start
	 * @param tickEnd the tick end
	 * @param items the progress view items
	 */
	public ProgressView(int tickStart, int tickEnd, ProgressViewItem... items) {
		Assert.isTrue(tickStart >= 0, "Start tick value must be greater or equal than zero");
		Assert.isTrue(tickEnd > 0, "End tick value must be greater than zero");
		Assert.isTrue(tickEnd > tickStart, "End tick value must be greater than start tick value");
		this.tickStart = tickStart;
		this.tickEnd = tickEnd;
		this.tickValue = tickStart;
		this.items = Arrays.asList(items);
		initLayout();
	}

	/**
	 * Defines an item within a progress view. Allows to set item's factory, size
	 * and horizontal alignment.
	 */
	public static class ProgressViewItem {

		private final Function<ProgressContext, TextCell<ProgressContext>> factory;
		private final int size;
		private final HorizontalAlign align;

		public ProgressViewItem(Function<ProgressContext, TextCell<ProgressContext>> factory, int size, HorizontalAlign align) {
			this.factory = factory;
			this.size = size;
			this.align = align;
		}

		public static ProgressViewItem ofText() {
			return ofText(0, HorizontalAlign.CENTER);
		}

		public static ProgressViewItem ofText(int size, HorizontalAlign hAligh) {
			return new ProgressViewItem(DEFAULT_DESCRIPTION_FACTORY, size, hAligh);
		}

		public static ProgressViewItem ofSpinner() {
			return ofSpinner(0, HorizontalAlign.CENTER);
		}

		public static ProgressViewItem ofSpinner(int size, HorizontalAlign hAligh) {
			return new ProgressViewItem(DEFAULT_SPINNER_FACTORY, size, hAligh);
		}

		public static ProgressViewItem ofPercent() {
			return ofPercent(0, HorizontalAlign.CENTER);
		}

		public static ProgressViewItem ofPercent(int size, HorizontalAlign hAligh) {
			return new ProgressViewItem(DEFAULT_PERCENT_FACTORY, size, hAligh);
		}
	}

	/**
	 * Gets a progress description.
	 *
	 * @return a progress description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets a progress description. Used in items as a text item.
	 *
	 * @param description the progress description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets an explicit spinner to use.
	 *
	 * @param spinner the spinner to use
	 */
	public void setSpinner(Spinner spinner) {
		this.spinner = spinner;
	}

	/**
	 * Starts a runtime logic. Call to already started progress has no effect.
	 */
	public void start() {
		if (running) {
			return;
		}
		running = true;
		startTime = System.currentTimeMillis();
		ProgressState state = getState();
		scheduleTicks();
		dispatch(ShellMessageBuilder.ofView(this, ProgressViewStartEvent.of(this, state)));
	}

	private Disposable.Composite disposables;
	private final String TAG_KEY = "ProgressView";
	private final String TAG_VALUE = UUID.randomUUID().toString();

	private void scheduleTicks() {
		if (disposables != null) {
			return;
		}
		EventLoop eventLoop = getEventLoop();
		if (eventLoop == null) {
			return;
		}
		Flux<Message<?>> ticks = Flux.interval(Duration.ofMillis(50)).map(l -> {
			Message<Long> message = MessageBuilder
				.withPayload(l)
				.setHeader(ShellMessageHeaderAccessor.EVENT_TYPE, EventLoop.Type.USER)
				.setHeader(TAG_KEY, TAG_VALUE)
				.build();
			return message;
		});
		Disposable ticksDisposable = ticks.subscribe(m -> {
			eventLoop.dispatch(m);
		});
		Disposable eventsDisposable = eventLoop.events()
			.filter(m -> EventLoop.Type.USER.equals(StaticShellMessageHeaderAccessor.getEventType(m)))
			.filter(m -> ObjectUtils.nullSafeEquals(m.getHeaders().get(TAG_KEY), TAG_VALUE))
			.subscribe(m -> {
				requestRedraw();
			});
		disposables = Disposables.composite();
		disposables.add(eventsDisposable);
		disposables.add(ticksDisposable);
	}

	private void requestRedraw() {
		EventLoop eventLoop = getEventLoop();
		if (eventLoop != null) {
			eventLoop.dispatch(ShellMessageBuilder.ofRedraw());
		}
	}

	/**
	 * Stops a runtime logic.
	 */
	public void stop() {
		if (!running) {
			return;
		}
		if (disposables != null) {
			disposables.dispose();
			disposables = null;
		}
		running = false;
		ProgressState state = getState();
		dispatch(ShellMessageBuilder.ofView(this, ProgressViewEndEvent.of(this, state)));
	}

	private static class BoxWrapper extends BoxView {
		TextCell<ProgressContext> delegate;
		BoxWrapper(TextCell<ProgressContext> delegate) {
			this.delegate = delegate;
		}
		@Override
		protected void drawInternal(Screen screen) {
			Rectangle rect = getRect();
			delegate.setRect(rect.x(), rect.y(), rect.width(), rect.height());
			delegate.draw(screen);
			super.drawInternal(screen);
		}
	}

	private void initLayout() {
		grid = new GridView();
		int[] columnSizes = new int[items.size()];
		int index = 0;
		for (ProgressViewItem item : items) {
			columnSizes[index] = item.size;
			TextCell<ProgressContext> cell = item.factory.apply(buildContext());
			cells.add(cell);
			cell.setHorizontalAlign(item.align);
			grid.addItem(new BoxWrapper(cell), 0, index, 1, 1, 0, 0);
			index++;
		}
		grid.setRowSize(0);
		grid.setColumnSize(columnSizes);

	}

	@Override
	protected void drawInternal(Screen screen) {
		long current = System.currentTimeMillis();

		updateTime = current;
		ProgressContext context = buildContext();
		for (TextCell<ProgressContext> cell : cells) {
			cell.setItem(context);
		}

		Rectangle rect = getRect();
		grid.setRect(rect.x(), rect.y(), rect.width(), rect.height());
		grid.draw(screen);

		super.drawInternal(screen);
	}

	/**
	 * Advance {@code tickValue} by a given count. Note that negative count
	 * will advance backwards.
	 *
	 * @param count the count to advance tick value
	 */
	public void tickAdvance(int count) {
		setTickValue(tickValue + count);
	}

	/**
	 * Sets a tick value. If value is lower or higher than {@code tickStart} or
	 * {@code tickEnd} respectively {@code tickValue} will be set to low/high
	 * bounds. This means {@code tickValue} is always kept within range inclusively.
	 *
	 * @param value the new tick value to set
	 */
	public void setTickValue(int value) {
		boolean changed = false;
		if (value > tickEnd) {
			changed = tickValue != tickEnd;
			tickValue = tickEnd;
		}
		else if (value < tickStart) {
			changed = tickValue != tickStart;
			tickValue = tickStart;
		}
		else {
			changed = tickValue != value;
			tickValue = value;
		}
		if (changed) {
			ProgressState state = getState();
			dispatch(ShellMessageBuilder.ofView(this, ProgressViewStateChangeEvent.of(this, state)));
			requestRedraw();
		}
	}

	/**
	 * Gets a state of this {@code ProgressView}.
	 *
	 * @return a view progress state
	 */
	public ProgressState getState() {
		return ProgressState.of(tickStart, tickEnd, tickValue, running, startTime, updateTime);
	}

	private ProgressContext buildContext() {
		return new ProgressContext() {

			@Override
			public String getDescription() {
				return ProgressView.this.getDescription();
			}

			@Override
			public ProgressState getState() {
				return ProgressView.this.getState();
			}

			@Override
			public ProgressView getView() {
				return ProgressView.this;
			}

			@Override
			public int resolveThemeStyle(String tag, int defaultStyle) {
				return ProgressView.this.resolveThemeStyle(tag, defaultStyle);
			}

			@Override
			public Spinner resolveThemeSpinner(String tag, Spinner defaultSpinner, Spinner fallbackSpinner) {
				return ProgressView.this.resolveThemeSpinner(tag, defaultSpinner, fallbackSpinner);
			}

			@Override
			public Spinner getSpinner() {
				return ProgressView.this.spinner;
			}
		};
	}

	/**
	 * Context for {@code ProgressView} cell components.
	 */
	public interface ProgressContext {

		/**
		 * Get a {@link ProgressView} description.
		 *
		 * @return a progress description
		 */
		String getDescription();

		/**
		 * Get a state of a {@link ProgressView}.
		 *
		 * @return progress view state
		 */
		ProgressState getState();

		/**
		 * Gets an encapsulating owner view.
		 *
		 * @return an owner view
		 */
		ProgressView getView();

		/**
		 * Gets a {@link Spinner} frames.
		 *
		 * @return spinner frames
		 */
		Spinner getSpinner();

		/**
		 * Resolve style using existing {@link ThemeResolver} and {@code theme name}.
		 * Use {@code defaultStyle} if resolving cannot happen.
		 *
		 * @param tag the style tag to use
		 * @param defaultStyle the default style to use
		 * @return resolved style
		 */
		int resolveThemeStyle(String tag, int defaultStyle);

		Spinner resolveThemeSpinner(String tag, Spinner defaultSpinner, Spinner fallbackSpinner);

	}

	/**
	 * Encapsulates a current running state of a {@link ProgressView}.
	 *
	 * @param tickStart the tick start value, zero or positive
	 * @param tickEnd the tick end value, positive and more than tick start
	 * @param tickValue the current tick value, within inclusive bounds of tick start/end
	 * @param running the running state
	 * @param startTime the running start time
	 * @param updateTime the last running update time
	 */
	public record ProgressState(int tickStart, int tickEnd, int tickValue, boolean running, long startTime,
			long updateTime) {

		public static ProgressState of(int tickStart, int tickEnd, int tickValue, boolean running, long startTime,
				long updateTime) {
			return new ProgressState(tickStart, tickEnd, tickValue, running, startTime, updateTime);
		}
	}

	/**
	 * {@link ViewEventArgs} for events using {@link ProgressState}.
	 *
	 * @param state the progress state
	 */
	public record ProgressViewStateEventArgs(ProgressState state) implements ViewEventArgs {

		public static ProgressViewStateEventArgs of(ProgressState state) {
			return new ProgressViewStateEventArgs(state);
		}
	}

	/**
	 * {@link ViewEvent} indicating that proggress has been started.
	 *
	 * @param view the view sending an event
	 * @param args the event args
	 */
	public record ProgressViewStartEvent(View view, ProgressViewStateEventArgs args) implements ViewEvent {

		public static ProgressViewStartEvent of(View view, ProgressState state) {
			return new ProgressViewStartEvent(view, ProgressViewStateEventArgs.of(state));
		}
	}

	/**
	 * {@link ViewEvent} indicating that proggress has been ended.
	 *
	 * @param view the view sending an event
	 * @param args the event args
	 */
	public record ProgressViewEndEvent(View view, ProgressViewStateEventArgs args) implements ViewEvent {

		public static ProgressViewEndEvent of(View view, ProgressState state) {
			return new ProgressViewEndEvent(view, ProgressViewStateEventArgs.of(state));
		}
	}

	/**
	 * {@link ViewEvent} indicating that proggress state has been changed.
	 *
	 * @param view the view sending an event
	 * @param args the event args
	 */
	public record ProgressViewStateChangeEvent(View view, ProgressViewStateEventArgs args) implements ViewEvent {

		public static ProgressViewStateChangeEvent of(View view, ProgressState state) {
			return new ProgressViewStateChangeEvent(view, ProgressViewStateEventArgs.of(state));
		}
	}

}
