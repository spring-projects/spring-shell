/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.shell.table;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * A table model that is backed by a list of beans.
 *
 * <p>One can control which properties are exposed (and their order). There is also
 * a convenience constructor for adding a special header row.</p>
 *
 * @author Eric Bottard
 */
public class BeanListTableModel<T> extends TableModel {

	private final List<BeanWrapper> data;

	private final List<String> propertyNames;

	private final List<Object> headerRow;

	public BeanListTableModel(Class<T> clazz, Iterable<T> list) {
		this.data = new ArrayList<BeanWrapper>();
		for (T bean : list) {
			this.data.add(new BeanWrapperImpl(bean));
		}
		this.headerRow = null;
		propertyNames = new ArrayList<String>();
		for (PropertyDescriptor propertyName : BeanUtils.getPropertyDescriptors(clazz)) {
			if ("class".equals(propertyName.getName())) {
				continue;
			}
			propertyNames.add(propertyName.getName());
		}
	}

	public BeanListTableModel(Iterable<T> list, String... propertyNames) {
		this.data = new ArrayList<BeanWrapper>();
		for (T bean : list) {
			this.data.add(new BeanWrapperImpl(bean));
		}
		this.headerRow = null;
		this.propertyNames = Arrays.asList(propertyNames);
	}

	public BeanListTableModel(Iterable<T> list, LinkedHashMap<String, Object> header) {
		this.data = new ArrayList<BeanWrapper>();
		for (T bean : list) {
			this.data.add(new BeanWrapperImpl(bean));
		}
		this.headerRow = new ArrayList<Object>(header.values());
		propertyNames = new ArrayList<String>(header.keySet());
	}

	@Override
	public int getRowCount() {
		return headerRow == null ? data.size() : 1 + data.size();
	}

	@Override
	public int getColumnCount() {
		return propertyNames.size();
	}

	@Override
	public Object getValue(int row, int column) {
		if (headerRow != null && row == 0) {
			return headerRow.get(column);
		}
		else {
			int rowToUse = headerRow == null ? row : row - 1;
			String propertyName = propertyNames.get(column);
			return data.get(rowToUse).getPropertyValue(propertyName);
		}
	}
}
