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

import org.springframework.shell.component.ConfirmationInput.ConfirmationInputContext;
import org.springframework.shell.component.flow.ComponentFlow.BaseBuilder;
import org.springframework.shell.component.flow.ComponentFlow.Builder;

/**
 * Base impl for {@link ConfirmationInputSpec}.
 *
 * @author Janne Valkealahti
 */
public abstract class BaseConfirmationInput extends BaseInput<ConfirmationInputSpec> implements ConfirmationInputSpec {

	private String name;
	private Boolean defaultValue;
	private Boolean resultValue;
	private ResultMode resultMode;
	private Function<ConfirmationInputContext, List<AttributedString>> renderer;
	private List<Consumer<ConfirmationInputContext>> preHandlers = new ArrayList<>();
	private List<Consumer<ConfirmationInputContext>> postHandlers = new ArrayList<>();
	private boolean storeResult = true;
	private String templateLocation;
	private Function<ConfirmationInputContext, String> next;

	public BaseConfirmationInput(BaseBuilder builder, String id) {
		super(builder, id);
	}

	@Override
	public ConfirmationInputSpec name(String name) {
		this.name = name;
		return this;
	}

	@Override
	public ConfirmationInputSpec resultValue(Boolean resultValue) {
		this.resultValue = resultValue;
		return this;
	}

	@Override
	public ConfirmationInputSpec resultMode(ResultMode resultMode) {
		this.resultMode = resultMode;
		return this;
	}

	@Override
	public ConfirmationInputSpec defaultValue(Boolean defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	@Override
	public ConfirmationInputSpec renderer(Function<ConfirmationInputContext, List<AttributedString>> renderer) {
		this.renderer = renderer;
		return this;
	}

	@Override
	public ConfirmationInputSpec template(String location) {
		this.templateLocation = location;
		return this;
	}

	@Override
	public ConfirmationInputSpec preHandler(Consumer<ConfirmationInputContext> handler) {
		this.preHandlers.add(handler);
		return this;
	}

	@Override
	public ConfirmationInputSpec postHandler(Consumer<ConfirmationInputContext> handler) {
		this.postHandlers.add(handler);
		return this;
	}

	@Override
	public ConfirmationInputSpec storeResult(boolean store) {
		this.storeResult = store;
		return this;
	}

	@Override
	public ConfirmationInputSpec next(Function<ConfirmationInputContext, String> next) {
		this.next = next;
		return this;
	}

	@Override
	public Builder and() {
		getBuilder().addConfirmationInput(this);
		return getBuilder();
	}

	@Override
	public ConfirmationInputSpec getThis() {
		return this;
	}

	public String getName() {
		return name;
	}

	public boolean getDefaultValue() {
		return defaultValue != null ? defaultValue : true;
	}

	public Boolean getResultValue() {
		return resultValue;
	}

	public ResultMode getResultMode() {
		return resultMode;
	}

	public Function<ConfirmationInputContext, List<AttributedString>> getRenderer() {
		return renderer;
	}

	public String getTemplateLocation() {
		return templateLocation;
	}

	public List<Consumer<ConfirmationInputContext>> getPreHandlers() {
		return preHandlers;
	}

	public List<Consumer<ConfirmationInputContext>> getPostHandlers() {
		return postHandlers;
	}

	public boolean isStoreResult() {
		return storeResult;
	}

	public Function<ConfirmationInputContext, String> getNext() {
		return next;
	}
}