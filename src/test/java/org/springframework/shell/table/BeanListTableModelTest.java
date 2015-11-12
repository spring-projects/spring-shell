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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Test;

/**
 * Test for BeanListTableModel.
 *
 * @author Eric Bottard
 */
public class BeanListTableModelTest extends AbstractTestWithSample {

	@Test
	public void testSimpleConstructor() throws IOException {

		List<Person> data = data();

		Table table = new TableBuilder(new BeanListTableModel<Person>(Person.class, data)).build();
		String result = table.render(80);
		assertThat(result, equalTo(sample()));

	}

	@Test
	public void testExplicitPropertyNames() throws IOException {

		List<Person> data = data();

		Table table = new TableBuilder(new BeanListTableModel<Person>(data, "lastName", "firstName")).build();
		String result = table.render(80);
		assertThat(result, equalTo(sample()));

	}

	@Test
	public void testHeaderRow() throws IOException {

		List<Person> data = data();

		LinkedHashMap<String, Object> header = new LinkedHashMap<String, Object>();
		header.put("lastName", "Last Name");
		header.put("firstName", "First Name");

		Table table = new TableBuilder(new BeanListTableModel<Person>(data, header)).build();
		String result = table.render(80);
		assertThat(result, equalTo(sample()));

	}

	private List<Person> data() {
		List<Person> data = new ArrayList<Person>();
		data.add(new Person("Alice", "Clark", 12));
		data.add(new Person("Bob", "Smith", 42));
		data.add(new Person("Sarah", "Connor", 38));
		return data;
	}

	public static class Person {
		private int age;

		private String firstName;

		private String lastName;

		public Person(String firstName, String lastName, int age) {
			this.age = age;
			this.firstName = firstName;
			this.lastName = lastName;
		}

		public int getAge() {
			return age;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}
	}

}