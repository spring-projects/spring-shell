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
package org.springframework.shell.samples.objects.data;

import org.springframework.shell.core.ObjectResolver;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;

public class FooResolver
	implements ObjectResolver<Foo>
{
	@Autowired
	private FooRepository fooRepository;
	
	public FooResolver() {
	}
	
	public void setFooRepository(FooRepository fooRepository) {
		this.fooRepository = fooRepository;
	}

	public Foo resolveObject(String value) {
		return fooRepository.retrieve(value);
	}
	
	public Collection<String> getAllValidValues() {
		return fooRepository.retrieveAllIds();
	}
	
	public Class<? extends Foo> getTargetClass() {
		return Foo.class;
	}

}
