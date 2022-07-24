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
package org.springframework.shell.samples.config;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ProxyHints;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.ResourceHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.util.ClassUtils;

public class SamplesRuntimeHints implements RuntimeHintsRegistrar {

	@Override
	public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
		ResourceHints resource = hints.resources();
		ProxyHints proxy = hints.proxies();
		ReflectionHints reflection = hints.reflection();
		// should go graalvm-reachability-metadata
		registerResources(resource);
		// should go graalvm-reachability-metadata
		registerProxies(proxy, "com.sun.jna.Library", "com.sun.jna.Callback",
				"org.jline.terminal.impl.jna.win.Kernel32", "org.jline.terminal.impl.jna.linux.CLibrary");
		// should go graalvm-reachability-metadata
		registerForMostReflection(reflection, "com.sun.jna.CallbackReference", "com.sun.jna.Native",
				"com.sun.jna.NativeLong", "com.sun.jna.Pointer", "com.sun.jna.Structure",
				"com.sun.jna.ptr.IntByReference", "com.sun.jna.ptr.PointerByReference", "com.sun.jna.Klass",
				"com.sun.jna.Structure$FFIType", "com.sun.jna.Structure$FFIType$size_t");
		// should go graalvm-reachability-metadata
		registerForMostReflection(reflection, "org.jline.terminal.impl.jna.win.Kernel32$CHAR_INFO",
				"org.jline.terminal.impl.jna.win.Kernel32$CONSOLE_CURSOR_INFO",
				"org.jline.terminal.impl.jna.win.Kernel32$CONSOLE_SCREEN_BUFFER_INFO",
				"org.jline.terminal.impl.jna.win.Kernel32$COORD",
				"org.jline.terminal.impl.jna.win.Kernel32$INPUT_RECORD",
				"org.jline.terminal.impl.jna.win.Kernel32$INPUT_RECORD$EventUnion",
				"org.jline.terminal.impl.jna.win.Kernel32$KEY_EVENT_RECORD",
				"org.jline.terminal.impl.jna.win.Kernel32$MOUSE_EVENT_RECORD",
				"org.jline.terminal.impl.jna.win.Kernel32$WINDOW_BUFFER_SIZE_RECORD",
				"org.jline.terminal.impl.jna.win.Kernel32$MENU_EVENT_RECORD",
				"org.jline.terminal.impl.jna.win.Kernel32$FOCUS_EVENT_RECORD",
				"org.jline.terminal.impl.jna.win.Kernel32$SMALL_RECT",
				"org.jline.terminal.impl.jna.win.Kernel32$UnionChar");
		// from spring-native sb-3.0.x branch
		registerHibernateValidatorHints(hints, classLoader);
	}

	private void registerHibernateValidatorHints(RuntimeHints hints, ClassLoader classLoader) {
		if (!ClassUtils.isPresent("org.hibernate.validator.HibernateValidator", classLoader)) {
			return;
		}
		hints.reflection().registerType(TypeReference.of("org.hibernate.validator.internal.util.logging.Log_$logger"),
			hint -> hint.withMembers(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS));
		hints.reflection().registerType(
			TypeReference.of("org.hibernate.validator.internal.util.logging.Messages_$bundle"),
				hint -> hint.withField("INSTANCE", fieldHint -> {
			}));
	}

	private void registerResources(ResourceHints resource) {
		resource.registerPattern("com/sun/jna/win32-x86-64/jnidispatch.dll");
	}

	private void registerProxies(ProxyHints proxy, String... classNames) {
		typeReferences(classNames)
			.forEach(tr -> proxy.registerJdkProxy(jdkProxyHint -> jdkProxyHint.proxiedInterfaces(tr)));
	}

	private void registerForMostReflection(ReflectionHints reflection, String... classNames) {
		reflection.registerTypes(typeReferences(classNames),
			hint -> {
				hint.withMembers(MemberCategory.DECLARED_CLASSES, MemberCategory.DECLARED_FIELDS,
					MemberCategory.PUBLIC_CLASSES, MemberCategory.PUBLIC_FIELDS,
					MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS);
			});
	}

	private Iterable<TypeReference> typeReferences(String... classNames) {
		return Stream.of(classNames).map(TypeReference::of).collect(Collectors.toList());
	}

}
