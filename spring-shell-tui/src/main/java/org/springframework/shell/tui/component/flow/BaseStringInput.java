/*
 * Copyright 2022 the original author or authors.
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
package org.springframework.shell.tui.component.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jline.utils.AttributedString;

import org.jspecify.annotations.Nullable;
import org.springframework.shell.tui.component.StringInput.StringInputContext;
import org.springframework.shell.tui.component.flow.ComponentFlow.BaseBuilder;
import org.springframework.shell.tui.component.flow.ComponentFlow.Builder;

/**
 * Base impl for {@link StringInputSpec}.
 *
 * @author Janne Valkealahti
 * @author Piotr Olaszewski
 */
public abstract class BaseStringInput extends BaseInput<StringInputSpec> implements StringInputSpec {

	private @Nullable String name;
	private @Nullable String resultValue;
	private @Nullable ResultMode resultMode;
	private @Nullable String defaultValue;
	private @Nullable Character maskCharacter;
	private @Nullable Function<StringInputContext, List<AttributedString>> renderer;
	private List<Consumer<StringInputContext>> preHandlers = new ArrayList<>();
	private List<Consumer<StringInputContext>> postHandlers = new ArrayList<>();
	private boolean storeResult = true;
	private @Nullable String templateLocation;
	private @Nullable Function<StringInputContext, String> next;

	public BaseStringInput(BaseBuilder builder, String id) {
		super(builder, id);
	}

	@Override
	public StringInputSpec name(String name) {
		this.name = name;
		return this;
	}

	@Override
	public StringInputSpec resultValue(String resultValue) {
		this.resultValue = resultValue;
		return this;
	}

	@Override
	public StringInputSpec resultMode(ResultMode resultMode) {
		this.resultMode = resultMode;
		return this;
	}

	@Override
	public StringInputSpec defaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	@Override
	public StringInputSpec maskCharacter(Character maskCharacter) {
		this.maskCharacter = maskCharacter;
		return this;
	}

	@Override
	public StringInputSpec renderer(Function<StringInputContext, List<AttributedString>> renderer) {
		this.renderer = renderer;
		return this;
	}

	@Override
	public StringInputSpec template(String location) {
		this.templateLocation = location;
		return this;
	}

	@Override
	public StringInputSpec preHandler(Consumer<StringInputContext> handler) {
		this.preHandlers.add(handler);
		return this;
	}

	@Override
	public StringInputSpec postHandler(Consumer<StringInputContext> handler) {
		this.postHandlers.add(handler);
		return this;
	}

	@Override
	public StringInputSpec storeResult(boolean store) {
		this.storeResult = store;
		return this;
	}

	@Override
	public StringInputSpec next(Function<StringInputContext, String> next) {
		this.next = next;
		return this;
	}

	@Override
	public Builder and() {
		getBuilder().addStringInput(this);
		return getBuilder();
	}

	@Override
	public StringInputSpec getThis() {
		return this;
	}

	public @Nullable String getName() {
		return name;
	}

	public @Nullable String getResultValue() {
		return resultValue;
	}

	public @Nullable ResultMode getResultMode() {
		return resultMode;
	}

	public @Nullable String getDefaultValue() {
		return defaultValue;
	}

	public @Nullable Character getMaskCharacter() {
		return maskCharacter;
	}

	public @Nullable Function<StringInputContext, List<AttributedString>> getRenderer() {
		return renderer;
	}

	public @Nullable String getTemplateLocation() {
		return templateLocation;
	}

	public List<Consumer<StringInputContext>> getPreHandlers() {
		return preHandlers;
	}

	public List<Consumer<StringInputContext>> getPostHandlers() {
		return postHandlers;
	}

	public boolean isStoreResult() {
		return storeResult;
	}

	public @Nullable Function<StringInputContext, String> getNext() {
		return next;
	}
}