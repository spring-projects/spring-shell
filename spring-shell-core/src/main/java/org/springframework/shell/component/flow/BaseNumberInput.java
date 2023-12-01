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
import org.springframework.shell.component.NumberInput.NumberInputContext;
import org.springframework.shell.component.flow.ComponentFlow.BaseBuilder;
import org.springframework.shell.component.flow.ComponentFlow.Builder;

/**
 * Base impl for {@link NumberInputSpec}.
 *
 * @author Nicola Di Falco
 */
public abstract class BaseNumberInput extends BaseInput<NumberInputSpec> implements NumberInputSpec {

	private String name;
	private Number resultValue;
	private ResultMode resultMode;
	private Number defaultValue;
	private Class<? extends Number> clazz = Integer.class;
	private boolean required = false;
	private Function<NumberInputContext, List<AttributedString>> renderer;
	private final List<Consumer<NumberInputContext>> preHandlers = new ArrayList<>();
	private final List<Consumer<NumberInputContext>> postHandlers = new ArrayList<>();
	private boolean storeResult = true;
	private String templateLocation;
	private Function<NumberInputContext, String> next;

	protected BaseNumberInput(BaseBuilder builder, String id) {
		super(builder, id);
	}

	@Override
	public NumberInputSpec name(String name) {
		this.name = name;
		return this;
	}

	@Override
	public NumberInputSpec resultValue(Number resultValue) {
		this.resultValue = resultValue;
		return this;
	}

	@Override
	public NumberInputSpec resultMode(ResultMode resultMode) {
		this.resultMode = resultMode;
		return this;
	}

	@Override
	public NumberInputSpec defaultValue(Number defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	@Override
	public NumberInputSpec numberClass(Class<? extends Number> clazz) {
		this.clazz = clazz;
		return this;
	}

	@Override
	public NumberInputSpec required() {
		this.required = true;
		return this;
	}

	@Override
	public NumberInputSpec renderer(Function<NumberInputContext, List<AttributedString>> renderer) {
		this.renderer = renderer;
		return this;
	}

	@Override
	public NumberInputSpec template(String location) {
		this.templateLocation = location;
		return this;
	}

	@Override
	public NumberInputSpec preHandler(Consumer<NumberInputContext> handler) {
		this.preHandlers.add(handler);
		return this;
	}

	@Override
	public NumberInputSpec postHandler(Consumer<NumberInputContext> handler) {
		this.postHandlers.add(handler);
		return this;
	}

	@Override
	public NumberInputSpec storeResult(boolean store) {
		this.storeResult = store;
		return this;
	}

	@Override
	public NumberInputSpec next(Function<NumberInputContext, String> next) {
		this.next = next;
		return this;
	}

	@Override
	public Builder and() {
		getBuilder().addNumberInput(this);
		return getBuilder();
	}

	@Override
	public NumberInputSpec getThis() {
		return this;
	}

	public String getName() {
		return name;
	}

	public Number getResultValue() {
		return resultValue;
	}

	public ResultMode getResultMode() {
		return resultMode;
	}

	public Number getDefaultValue() {
		return defaultValue;
	}

	public Class<? extends Number> getNumberClass() {
		return clazz;
	}

	public boolean isRequired() {
		return required;
	}

	public Function<NumberInputContext, List<AttributedString>> getRenderer() {
		return renderer;
	}

	public String getTemplateLocation() {
		return templateLocation;
	}

	public List<Consumer<NumberInputContext>> getPreHandlers() {
		return preHandlers;
	}

	public List<Consumer<NumberInputContext>> getPostHandlers() {
		return postHandlers;
	}

	public boolean isStoreResult() {
		return storeResult;
	}

	public Function<NumberInputContext, String> getNext() {
		return next;
	}
}
