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
package org.springframework.shell.command.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.shell.command.annotation.support.CommandScanRegistrar;

/**
 * Configures the base packages used when scanning for {@link Command @Comamnd}
 * classes. One of {@link #basePackageClasses()}, {@link #basePackages()} or its
 * alias {@link #value()} may be specified to define specific packages to scan.
 * If specific packages are not defined scanning will occur from the package of
 * the class with this annotation.
 *
 * @author Janne Valkealahti
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CommandScanRegistrar.class)
@EnableCommand
public @interface CommandScan {

	/**
	 * Alias for the {@link #basePackages()} attribute. Allows for more concise
	 * annotation declarations e.g.: {@code @CommandScan("org.my.pkg")} instead of
	 * {@code @CommandScan(basePackages="org.my.pkg")}.
	 *
	 * @return the base packages to scan
	 */
	@AliasFor("basePackages")
	String[] value() default {};

	/**
	 * Base packages to scan for commands. {@link #value()} is an alias for (and
	 * mutually exclusive with) this attribute.
	 * <p>
	 * Use {@link #basePackageClasses()} for a type-safe alternative to String-based
	 * package names.
	 *
	 * @return the base packages to scan
	 */
	@AliasFor("value")
	String[] basePackages() default {};

	/**
	 * Type-safe alternative to {@link #basePackages()} for specifying the packages
	 * to scan for commands. The package of each class specified will be scanned.
	 * <p>
	 * Consider creating a special no-op marker class or interface in each package
	 * that serves no purpose other than being referenced by this attribute.
	 *
	 * @return classes from the base packages to scan
	 */
	Class<?>[] basePackageClasses() default {};
}
