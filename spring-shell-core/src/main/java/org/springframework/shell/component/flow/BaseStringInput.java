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
import org.springframework.shell.component.StringInput.StringInputContext;
import org.springframework.shell.component.flow.ComponentFlow.BaseBuilder;
import org.springframework.shell.component.flow.ComponentFlow.Builder;

/**
 * Base impl for {@link StringInputSpec}.
 *
 * @author Janne Valkealahti
 */
public abstract class BaseStringInput extends BaseInput<StringInputSpec> implements StringInputSpec {

	private String name;
	private String resultValue;
	private ResultMode resultMode;
	private String defaultValue;
	private Character maskCharacter;
	private boolean required = false;
	private Function<StringInputContext, List<AttributedString>> renderer;
	private List<Consumer<StringInputContext>> preHandlers = new ArrayList<>();
	private List<Consumer<StringInputContext>> postHandlers = new ArrayList<>();
	private boolean storeResult = true;
	private String templateLocation;
	private Function<StringInputContext, String> next;

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
	public StringInputSpec required() {
		this.required = true;
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

	public Character getMaskCharacter() {
		return maskCharacter;
	}

	public boolean isRequired() {
		return required;
	}

	public Function<StringInputContext, List<AttributedString>> getRenderer() {
		return renderer;
	}

	public String getTemplateLocation() {
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

	public Function<StringInputContext, String> getNext() {
		return next;
	}
}
