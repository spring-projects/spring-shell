/*
 * Copyright 2023 the original author or authors.
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
package org.springframework.shell.command.annotation.support;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.shell.command.annotation.EnableCommand;

/**
 * {@link ImportBeanDefinitionRegistrar} for {@link EnableCommand @EnableCommands}.
 *
 * @author Janne Valkealahti
 */
public final class EnableCommandRegistrar implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		CommandRegistrationBeanRegistrar beanRegistrar = new CommandRegistrationBeanRegistrar(registry);
		getTypes(metadata).forEach(beanRegistrar::register);
	}

	private Set<Class<?>> getTypes(AnnotationMetadata metadata) {
		return metadata.getAnnotations()
			.stream(EnableCommand.class)
			.flatMap((annotation) -> Arrays.stream(annotation.getClassArray(MergedAnnotation.VALUE)))
			.filter((type) -> void.class != type)
			.collect(Collectors.toSet());
	}

}
