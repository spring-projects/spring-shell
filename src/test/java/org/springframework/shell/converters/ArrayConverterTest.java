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
import static org.junit.Assert.assertFalse;
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
 * Unit test of {@link ArrayConverter}
 *
 * @author Joern Huxhorn
 * @since 1.1.0
 */
public class ArrayConverterTest {

	private FooArrayConverter instance;
	
	@Before
	public void setUp() {
		FooResolver resolver = new FooResolver();
		resolver.setFooRepository(new FooRepository());
		instance = new FooArrayConverter(resolver);
	}

	@Test
	public void supports() {
		assertTrue(instance.supports(Foo[].class, null));
		assertTrue(instance.supports(Foo[].class, "anything"));
	}
	
	@Test
	public void convertFromTextDefault() {
		Foo[] result;
		
		result = instance.convertFromText("Do", Foo[].class, null);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("Do", result[0].getName());
		
		result = instance.convertFromText("Re", Foo[].class, null);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("Re", result[0].getName());
		
		result = instance.convertFromText("RE", Foo[].class, null);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("Re", result[0].getName());
	}
	
	@Test
	public void convertFromTextCaseSensitive() {
		Foo[] result;
		
		result = instance.convertFromText("Do", Foo[].class, ArrayConverter.CASE_SENSITIVE);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("Do", result[0].getName());
		
		result = instance.convertFromText("Re", Foo[].class, ArrayConverter.CASE_SENSITIVE);
		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("Re", result[0].getName());
		
		result = instance.convertFromText("RE", Foo[].class, ArrayConverter.CASE_SENSITIVE);
		assertNull(result);
	}

	@Test
	public void convertFromTextDefaultMulti() {
		Foo[] result;
		
		result = instance.convertFromText("Do,Re,Mi,Do,Fa", Foo[].class, null);
		assertNotNull(result);
		assertEquals(5, result.length);
		assertEquals("Do", result[0].getName());
		assertEquals("Re", result[1].getName());
		assertEquals("Mi", result[2].getName());
		assertEquals("Do", result[3].getName());
		assertEquals("Fa", result[4].getName());
		
		result = instance.convertFromText("Do,RE,Mi,Do,Fa", Foo[].class, null);
		assertNotNull(result);
		assertEquals(5, result.length);
		assertEquals("Do", result[0].getName());
		assertEquals("Re", result[1].getName());
		assertEquals("Mi", result[2].getName());
		assertEquals("Do", result[3].getName());
		assertEquals("Fa", result[4].getName());
		
		result = instance.convertFromText("Do,XX,Mi,Do,Fa", Foo[].class, null);
		assertNull(result);
	}

	@Test
	public void convertFromTextUniqueMulti() {
		Foo[] result;
		
		result = instance.convertFromText("Do,Re,Mi,Do,Fa", Foo[].class, ArrayConverter.UNIQUE_VALUES);
		assertNotNull(result);
		assertEquals(4, result.length);
		assertEquals("Do", result[0].getName());
		assertEquals("Re", result[1].getName());
		assertEquals("Mi", result[2].getName());
		assertEquals("Fa", result[3].getName());
		
		result = instance.convertFromText("Do,RE,Mi,Do,Fa", Foo[].class, ArrayConverter.UNIQUE_VALUES);
		assertNotNull(result);
		assertEquals(4, result.length);
		assertEquals("Do", result[0].getName());
		assertEquals("Re", result[1].getName());
		assertEquals("Mi", result[2].getName());
		assertEquals("Fa", result[3].getName());
		
		result = instance.convertFromText("Do,XX,Mi,Do,Fa", Foo[].class, ArrayConverter.UNIQUE_VALUES);
		assertNull(result);
	}

	@Test
	public void convertFromTextUniqueCaseSensitiveMulti() {
		Foo[] result;
		
		result = instance.convertFromText("Do,Re,Mi,Do,Fa", Foo[].class, ArrayConverter.UNIQUE_VALUES + ArrayConverter.CASE_SENSITIVE);
		assertNotNull(result);
		assertEquals(4, result.length);
		assertEquals("Do", result[0].getName());
		assertEquals("Re", result[1].getName());
		assertEquals("Mi", result[2].getName());
		assertEquals("Fa", result[3].getName());
		
		result = instance.convertFromText("Do,RE,Mi,Do,Fa", Foo[].class, ArrayConverter.UNIQUE_VALUES + ArrayConverter.CASE_SENSITIVE);
		assertNull(result);
		
		result = instance.convertFromText("Do,XX,Mi,Do,Fa", Foo[].class, ArrayConverter.UNIQUE_VALUES + ArrayConverter.CASE_SENSITIVE);
		assertNull(result);
	}

	@Test
	public void convertFromTextCaseSensitiveMulti() {
		Foo[] result;
		
		result = instance.convertFromText("Do,Re,Mi,Do,Fa", Foo[].class, ArrayConverter.CASE_SENSITIVE);
		assertNotNull(result);
		assertEquals(5, result.length);
		assertEquals("Do", result[0].getName());
		assertEquals("Re", result[1].getName());
		assertEquals("Mi", result[2].getName());
		assertEquals("Do", result[3].getName());
		assertEquals("Fa", result[4].getName());
		
		result = instance.convertFromText("Do,RE,Mi,Do,Fa", Foo[].class, ArrayConverter.CASE_SENSITIVE);
		assertNull(result);
		
		result = instance.convertFromText("Do,XX,Mi,Do,Fa", Foo[].class, ArrayConverter.CASE_SENSITIVE);
		assertNull(result);
	}

	@Test
	public void getAllPossibleValuesDefault() {
		List<Completion> completions;
		Set<String> values;
		
		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "", null, null));
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
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "D", null, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "d", null, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do", null, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "do", null, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do,", null, null));
		assertEquals(6, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do,Do"));
		assertTrue(values.contains("Do,Re"));
		assertTrue(values.contains("Do,Mi"));
		assertTrue(values.contains("Do,Fa"));
		assertTrue(values.contains("Do,Fu"));
		assertTrue(values.contains("Do,So"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "do,", null, null));
		assertEquals(6, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do,Do"));
		assertTrue(values.contains("Do,Re"));
		assertTrue(values.contains("Do,Mi"));
		assertTrue(values.contains("Do,Fa"));
		assertTrue(values.contains("Do,Fu"));
		assertTrue(values.contains("Do,So"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do,R", null, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do,Re"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "do,R", null, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do,Re"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do,F", null, null));
		assertEquals(2, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do,Fa"));
		assertTrue(values.contains("Do,Fu"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "do,f", null, null));
		assertEquals(2, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do,Fa"));
		assertTrue(values.contains("Do,Fu"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do,X", null, null));
		assertEquals(0, completions.size());
	}
	
	@Test
	public void getAllPossibleValuesCaseSensitive() {
		List<Completion> completions;
		Set<String> values;
		
		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "", ArrayConverter.CASE_SENSITIVE, null));
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
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "D", ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "d", ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(0, completions.size());

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do", ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "do", ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(0, completions.size());

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do,", ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(6, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do,Do"));
		assertTrue(values.contains("Do,Re"));
		assertTrue(values.contains("Do,Mi"));
		assertTrue(values.contains("Do,Fa"));
		assertTrue(values.contains("Do,Fu"));
		assertTrue(values.contains("Do,So"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "do,", ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(0, completions.size());

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do,R", ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do,Re"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "do,R", ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(0, completions.size());

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do,F", ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(2, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do,Fa"));
		assertTrue(values.contains("Do,Fu"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "do,f", ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(0, completions.size());

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do,X", ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(0, completions.size());
	}

	@Test
	public void getAllPossibleValuesUnique() {
		List<Completion> completions;
		Set<String> values;
		
		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "", ArrayConverter.UNIQUE_VALUES, null));
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
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "D", ArrayConverter.UNIQUE_VALUES, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "d", ArrayConverter.UNIQUE_VALUES, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do", ArrayConverter.UNIQUE_VALUES, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "do", ArrayConverter.UNIQUE_VALUES, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do,", ArrayConverter.UNIQUE_VALUES, null));
		assertEquals(5, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do,Re"));
		assertTrue(values.contains("Do,Mi"));
		assertTrue(values.contains("Do,Fa"));
		assertTrue(values.contains("Do,Fu"));
		assertTrue(values.contains("Do,So"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "do,", ArrayConverter.UNIQUE_VALUES, null));
		assertEquals(5, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do,Re"));
		assertTrue(values.contains("Do,Mi"));
		assertTrue(values.contains("Do,Fa"));
		assertTrue(values.contains("Do,Fu"));
		assertTrue(values.contains("Do,So"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do,R", ArrayConverter.UNIQUE_VALUES, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do,Re"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "do,R", ArrayConverter.UNIQUE_VALUES, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do,Re"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do,F", ArrayConverter.UNIQUE_VALUES, null));
		assertEquals(2, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do,Fa"));
		assertTrue(values.contains("Do,Fu"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "do,f", ArrayConverter.UNIQUE_VALUES, null));
		assertEquals(2, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do,Fa"));
		assertTrue(values.contains("Do,Fu"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do,X", ArrayConverter.UNIQUE_VALUES, null));
		assertEquals(0, completions.size());
	}

	@Test
	public void getAllPossibleValuesUniqueCaseSensitive() {
		List<Completion> completions;
		Set<String> values;
		
		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "", ArrayConverter.UNIQUE_VALUES + ArrayConverter.CASE_SENSITIVE, null));
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
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "D", ArrayConverter.UNIQUE_VALUES + ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "d", ArrayConverter.UNIQUE_VALUES + ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(0, completions.size());

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do", ArrayConverter.UNIQUE_VALUES + ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "do", ArrayConverter.UNIQUE_VALUES + ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(0, completions.size());

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do,", ArrayConverter.UNIQUE_VALUES + ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(5, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do,Re"));
		assertTrue(values.contains("Do,Mi"));
		assertTrue(values.contains("Do,Fa"));
		assertTrue(values.contains("Do,Fu"));
		assertTrue(values.contains("Do,So"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "do,", ArrayConverter.UNIQUE_VALUES + ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(0, completions.size());

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do,R", ArrayConverter.UNIQUE_VALUES + ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(1, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do,Re"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "do,R", ArrayConverter.UNIQUE_VALUES + ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(0, completions.size());

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do,F", ArrayConverter.UNIQUE_VALUES + ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(2, completions.size());
		values = new HashSet<String>();
		for(Completion current : completions) {
			values.add(current.getValue());
		}
		assertTrue(values.contains("Do,Fa"));
		assertTrue(values.contains("Do,Fu"));

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "do,f", ArrayConverter.UNIQUE_VALUES + ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(0, completions.size());

		completions = new ArrayList<Completion>();
		assertFalse(instance.getAllPossibleValues(completions, Foo[].class, "Do,X", ArrayConverter.UNIQUE_VALUES + ArrayConverter.CASE_SENSITIVE, null));
		assertEquals(0, completions.size());
	}

	private static class FooArrayConverter extends ArrayConverter<Foo> {
		public FooArrayConverter(FooResolver fooResolver) {
			super(fooResolver);
		}
	}
}
