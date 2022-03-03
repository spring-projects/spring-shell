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
package org.springframework.shell.component.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jline.utils.AttributedString;

import org.springframework.shell.component.PathInput.PathInputContext;
import org.springframework.shell.component.flow.ComponentFlow.BaseBuilder;
import org.springframework.shell.component.flow.ComponentFlow.Builder;

/**
 * Base impl for {@link PathInputSpec}.
 *
 * @author Janne Valkealahti
 */
public abstract class BasePathInput extends BaseInput<PathInputSpec> implements PathInputSpec {

	private String name;
	private String resultValue;
	private ResultMode resultMode;
	private String defaultValue;
	private Function<PathInputContext, List<AttributedString>> renderer;
	private List<Consumer<PathInputContext>> preHandlers = new ArrayList<>();
	private List<Consumer<PathInputContext>> postHandlers = new ArrayList<>();
	private boolean storeResult = true;
	private String templateLocation;
	private Function<PathInputContext, String> next;

	public BasePathInput(BaseBuilder builder, String id) {
		super(builder, id);
	}

	@Override
	public PathInputSpec name(String name) {
		this.name = name;
		return this;
	}

	@Override
	public PathInputSpec resultValue(String resultValue) {
		this.resultValue = resultValue;
		return this;
	}

	@Override
	public PathInputSpec resultMode(ResultMode resultMode) {
		this.resultMode = resultMode;
		return this;
	}

	@Override
	public PathInputSpec defaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	@Override
	public PathInputSpec renderer(Function<PathInputContext, List<AttributedString>> renderer) {
		this.renderer = renderer;
		return this;
	}

	@Override
	public PathInputSpec template(String location) {
		this.templateLocation = location;
		return this;
	}

	@Override
	public PathInputSpec preHandler(Consumer<PathInputContext> handler) {
		this.preHandlers.add(handler);
		return this;
	}

	@Override
	public PathInputSpec postHandler(Consumer<PathInputContext> handler) {
		this.postHandlers.add(handler);
		return this;
	}

	@Override
	public PathInputSpec storeResult(boolean store) {
		this.storeResult = store;
		return this;
	}

	@Override
	public PathInputSpec next(Function<PathInputContext, String> next) {
		this.next = next;
		return this;
	}

	@Override
	public Builder and() {
		getBuilder().addPathInput(this);
		return getBuilder();
	}

	@Override
	public PathInputSpec getThis() {
		return this;
	}

	public String getName() {
		return name;
	}

	public String getResultValue() {
		return resultValue;
	}

	public ResultMode getResultMode() {
		return resultMode;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public Function<PathInputContext, List<AttributedString>> getRenderer() {
		return renderer;
	}

	public String getTemplateLocation() {
		return templateLocation;
	}

	public List<Consumer<PathInputContext>> getPreHandlers() {
		return preHandlers;
	}

	public List<Consumer<PathInputContext>> getPostHandlers() {
		return postHandlers;
	}

	public boolean isStoreResult() {
		return storeResult;
	}

	public Function<PathInputContext, String> getNext() {
		return next;
	}
}