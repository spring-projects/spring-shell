/*
 * Copyright 2011-2013 the original author or authors.
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
package org.springframework.shell.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import org.springframework.shell.core.Completion;

import org.springframework.shell.data.Foo;
import org.springframework.shell.data.FooResolver;
import org.springframework.shell.data.FooRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * Unit test of {@link ObjectConverter}
 *
 * @author Joern Huxhorn
 * @since 1.1.0
 */
public class ObjectConverterTest {

	private FooConverter instance;
	
	@Before
	public void setUp() {
		FooResolver resolver = new FooResolver();
		resolver.setFooRepository(new FooRepository());
		instance = new FooConverter(resolver);
	}

	@Test
	public void supports() {
		assertTrue(instance.supports(Foo.class, null));
		assertTrue(instance.supports(Foo.class, "anything"));
	}
	
	@Test
	public void convertFromTextDefault() {
		Foo result;
		result = instance.convertFromText("Do", Foo.class, null);
		assertNotNull(result);
		assertEquals("Do", result.getName());
		result = instance.convertFromText("Re", Foo.class, null);
		assertNotNull(result);
		assertEquals("Re", result.getName());
		result = instance.convertFromText("RE", Foo.class, null);
		assertNotNull(result);
		assertEquals("Re", result.getName());
	}
	
	@Test
	public void convertFromTextCaseSensitive() {
		Foo result;
		result = instance.convertFromText("Do", Foo.class, ObjectConverter.CASE_SENSITIVE);
		assertNotNull(result);
		assertEquals("Do", result.getName());
		result = instance.convertFromText("Re", Foo.class, ObjectConverter.CASE_SENSITIVE);
		assertNotNull(result);
		assertEquals("Re", result.getName());
		result = instance.convertFromText("RE", Foo.class, ObjectConverter.CASE_SENSITIVE);
		assertNull(result);
	}

	@Test
	public void getAllPossibleValuesDefault() {
		List<Completion> completions;
		Set<String> values;

		completions = new ArrayList<Completion>();
		assertTrue(instance.getAllPossibleValues(completions, Foo.class, "", null, null));
		assertEquals(6, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do"));
		assertTrue(values.contains("Re"));
		assertTrue(values.contains("Mi"));
		assertTrue(values.contains("Fa"));
		assertTrue(values.contains("Fu"));
		assertTrue(values.contains("So"));

		completions = new ArrayList<Completion>();
		assertTrue(instance.getAllPossibleValues(completions, Foo.class, "D", null, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do"));

		completions = new ArrayList<Completion>();
		assertTrue(instance.getAllPossibleValues(completions, Foo.class, "d", null, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do"));

		completions = new ArrayList<Completion>();
		assertTrue(instance.getAllPossibleValues(completions, Foo.class, "F", null, null));
		assertEquals(2, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Fa"));
		assertTrue(values.contains("Fu"));

		completions = new ArrayList<Completion>();
		assertTrue(instance.getAllPossibleValues(completions, Foo.class, "f", null, null));
		assertEquals(2, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Fa"));
		assertTrue(values.contains("Fu"));
	}

	@Test
	public void getAllPossibleValuesCaseSensitive() {
		List<Completion> completions;
		Set<String> values;

		completions = new ArrayList<Completion>();
		assertTrue(instance.getAllPossibleValues(completions, Foo.class, "", ObjectConverter.CASE_SENSITIVE, null));
		assertEquals(6, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do"));
		assertTrue(values.contains("Re"));
		assertTrue(values.contains("Mi"));
		assertTrue(values.contains("Fa"));
		assertTrue(values.contains("Fu"));
		assertTrue(values.contains("So"));

		completions = new ArrayList<Completion>();
		assertTrue(instance.getAllPossibleValues(completions, Foo.class, "D", ObjectConverter.CASE_SENSITIVE, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do"));

		completions = new ArrayList<Completion>();
		assertTrue(instance.getAllPossibleValues(completions, Foo.class, "d", ObjectConverter.CASE_SENSITIVE, null));
		assertEquals(0, completions.size());

		completions = new ArrayList<Completion>();
		assertTrue(instance.getAllPossibleValues(completions, Foo.class, "F", ObjectConverter.CASE_SENSITIVE, null));
		assertEquals(2, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Fa"));
		assertTrue(values.contains("Fu"));

		completions = new ArrayList<Completion>();
		assertTrue(instance.getAllPossibleValues(completions, Foo.class, "f", ObjectConverter.CASE_SENSITIVE, null));
		assertEquals(0, completions.size());
	}

	private static class FooConverter extends ObjectConverter<Foo>
	{
		public FooConverter(FooResolver fooResolver) {
			super(fooResolver);
		}
	}
}
