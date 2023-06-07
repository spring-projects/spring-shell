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
package org.springframework.shell;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ProxyHints;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.ResourceHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeHint;
import org.springframework.aot.hint.TypeReference;

public class JnaRuntimeHints implements RuntimeHintsRegistrar {

	@Override
	public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
		ResourceHints resource = hints.resources();
		ProxyHints proxy = hints.proxies();
		ReflectionHints reflection = hints.reflection();
		ReflectionHints jni = hints.jni();
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

		registerForMostReflection(reflection, "org.jline.terminal.impl.jna.linux.CLibrary$termios",
				"org.jline.terminal.impl.jna.linux.CLibrary$winsize");

		registerJni(jni);
	}

	private void registerResources(ResourceHints resource) {
		resource.registerPattern("com/sun/jna/win32-x86-64/jnidispatch.dll");
		resource.registerPattern("com/sun/jna/linux-x86-64/libjnidispatch.so");
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

	private List<TypeReference> typeReferences(String... classNames) {
		return Stream.of(classNames).map(TypeReference::of).collect(Collectors.toList());
	}

	private void registerJni(ReflectionHints jni) {
		TypeReference reachableType = TypeReference.of("org.jline.terminal.impl.jna.win.Kernel32");

		registerForJni(jni, reachableType, "com.sun.jna.Callback",
			Methods.of(),
			Fields.of());

		registerForJni(jni, reachableType, "com.sun.jna.CallbackReference",
			Methods.of(
				Method.of("getCallback", "java.lang.Class", "com.sun.jna.Pointer", "boolean"),
				Method.of("getFunctionPointer", "com.sun.jna.Callback", "boolean"),
				Method.of("getNativeString", "java.lang.Object", "boolean"),
				Method.of("initializeThread", "com.sun.jna.Callback",
						"com.sun.jna.CallbackReference$AttachOptions")),
			Fields.of());

		registerForJni(jni, reachableType, "com.sun.jna.CallbackReference$AttachOptions",
			Methods.of(),
			Fields.of());

		registerForJni(jni, reachableType, "com.sun.jna.FromNativeConverter",
			Methods.of(
				Method.of("nativeType")
			),
			Fields.of());

		registerForJni(jni, reachableType, "com.sun.jna.IntegerType",
			Methods.of(),
			Fields.of("value"));

		registerForJni(jni, reachableType, "com.sun.jna.JNIEnv",
			Methods.of(),
			Fields.of());

		registerForJni(jni, reachableType, "com.sun.jna.Native",
			Methods.of(
				Method.of("dispose"),
				Method.of("fromNative", "com.sun.jna.FromNativeConverter", "java.lang.Object",
						"java.lang.reflect.Method"),
				Method.of("fromNative", "java.lang.Class", "java.lang.Object"),
				Method.of("fromNative", "java.lang.reflect.Method", "java.lang.Object"),
				Method.of("nativeType", "java.lang.Class"),
				Method.of("toNative", "com.sun.jna.ToNativeConverter","java.lang.Object")
			),
			Fields.of());

		registerForJni(jni, reachableType, "com.sun.jna.Native$ffi_callback",
			Methods.of(
				Method.of("invoke", "long", "long", "long")
			),
			Fields.of());

		registerForJni(jni, reachableType, "com.sun.jna.NativeMapped",
			Methods.of(
				Method.of("toNative")
			),
			Fields.of());

		registerForJni(jni, reachableType, "com.sun.jna.Pointer",
			Methods.of(
				Method.of("<init>", "long")
			),
			Fields.of("peer"));

		registerForJni(jni, reachableType, "com.sun.jna.PointerType",
			Methods.of(),
			Fields.of("pointer"));

		registerForJni(jni, reachableType, "com.sun.jna.Structure",
			Methods.of(
				Method.of("autoRead"),
				Method.of("autoWrite"),
				Method.of("getTypeInfo"),
				Method.of("newInstance", "java.lang.Class", "long")
			),
			Fields.of("memory", "typeInfo"));

		registerForJni(jni, reachableType, "com.sun.jna.Structure$ByValue",
			Methods.of(),
			Fields.of());

		registerForJni(jni, reachableType, "com.sun.jna.Structure$FFIType$FFITypes",
			Methods.of(),
			Fields.of("ffi_type_double", "ffi_type_float", "ffi_type_longdouble", "ffi_type_pointer",
					"ffi_type_sint16", "ffi_type_sint32", "ffi_type_sint64", "ffi_type_sint8", "ffi_type_uint16",
					"ffi_type_uint32", "ffi_type_uint64", "ffi_type_uint8",
					"ffi_type_void"));

		registerForJni(jni, reachableType, "com.sun.jna.WString",
			Methods.of(
				Method.of("<init>", "java.lang.String")
			),
			Fields.of());

		registerForJni(jni, reachableType, "java.lang.Boolean",
			Methods.of(
				Method.of("<init>", "boolean")
			),
			Fields.of("TYPE", "value"));

		registerForJni(jni, reachableType, "java.lang.Byte",
			Methods.of(
				Method.of("<init>", "byte")
			),
			Fields.of("TYPE", "value"));

		registerForJni(jni, reachableType, "java.lang.Character",
			Methods.of(
				Method.of("<init>", "char")
			),
			Fields.of("TYPE", "value"));

		registerForJni(jni, reachableType, "java.lang.Class",
			Methods.of(
				Method.of("getComponentType")
			),
			Fields.of());

		registerForJni(jni, reachableType, "java.lang.Double",
			Methods.of(
				Method.of("<init>", "double")
			),
			Fields.of("TYPE", "value"));

		registerForJni(jni, reachableType, "java.lang.Float",
			Methods.of(
				Method.of("<init>", "float")
			),
			Fields.of("TYPE", "value"));

		registerForJni(jni, reachableType, "java.lang.Integer",
			Methods.of(
				Method.of("<init>", "int")
			),
			Fields.of("TYPE", "value"));

		registerForJni(jni, reachableType, "java.lang.Long",
			Methods.of(
				Method.of("<init>", "long")
			),
			Fields.of("TYPE", "value"));

		registerForJni(jni, reachableType, "java.lang.Object",
			Methods.of(
				Method.of("toString")
			),
			Fields.of());

		registerForJni(jni, reachableType, "java.lang.Short",
			Methods.of(
				Method.of("<init>", "short")
			),
			Fields.of("TYPE", "value"));

		registerForJni(jni, reachableType, "java.lang.String",
			Methods.of(
				Method.of("<init>", "byte[]"),
				Method.of("<init>", "byte[]", "java.lang.String"),
				Method.of("getBytes"),
				Method.of("getBytes", "java.lang.String"),
				Method.of("toCharArray")
			),
			Fields.of());

		registerForJni(jni, reachableType, "java.lang.System",
			Methods.of(
				Method.of("getProperty", "java.lang.String")
			),
			Fields.of());

		registerForJni(jni, reachableType, "java.lang.UnsatisfiedLinkError",
			Methods.of(
				Method.of("<init>","java.lang.String")
			),
			Fields.of());

		registerForJni(jni, reachableType, "java.lang.Void",
			Methods.of(),
			Fields.of("TYPE"));

		registerForJni(jni, reachableType, "java.lang.reflect.Method",
			Methods.of(
				Method.of("getParameterTypes"),
				Method.of("getReturnType")
			),
			Fields.of());

		registerForJni(jni, reachableType, "java.nio.Buffer",
			Methods.of(
				Method.of("position")
			),
			Fields.of());

		registerForJni(jni, reachableType, "java.nio.ByteBuffer",
			Methods.of(
				Method.of("array"),
				Method.of("arrayOffset")
			),
			Fields.of());

		registerForJni(jni, reachableType, "java.nio.CharBuffer",
			Methods.of(
				Method.of("array"),
				Method.of("arrayOffset")
			),
			Fields.of());

		registerForJni(jni, reachableType, "java.nio.DoubleBuffer",
			Methods.of(
				Method.of("array"),
				Method.of("arrayOffset")
			),
			Fields.of());

		registerForJni(jni, reachableType, "java.nio.FloatBuffer",
			Methods.of(
				Method.of("array"),
				Method.of("arrayOffset")
			),
			Fields.of());

		registerForJni(jni, reachableType, "java.nio.IntBuffer",
			Methods.of(
				Method.of("array"),
				Method.of("arrayOffset")
			),
			Fields.of());

		registerForJni(jni, reachableType, "java.nio.LongBuffer",
			Methods.of(
				Method.of("array"),
				Method.of("arrayOffset")
			),
			Fields.of());

		registerForJni(jni, reachableType, "java.nio.ShortBuffer",
			Methods.of(
				Method.of("array"),
				Method.of("arrayOffset")
			),
			Fields.of());

	}

	private void registerForJni(ReflectionHints jni, TypeReference reachableType, String type,
			Methods methods, Fields fields) {
		jni.registerType(TypeReference.of(type), hint -> {
			hint.onReachableType(reachableType);
			methods.withHints(hint);
			fields.withHints(hint);
		});
	}

	record Fields(String... names) {

		static Fields of(String... names) {
			return new Fields(names);
		}

		void withHints(TypeHint.Builder hint) {
			Stream.of(names()).forEach(f -> hint.withField(f));
		}
	}

	record Method(String name, String... parameterTypes) {

		static Method of(String name, String... parameterTypes) {
			return new Method(name, parameterTypes);
		}

		private List<TypeReference> asTypeReferences() {
			return Stream.of(parameterTypes).map(TypeReference::of).collect(Collectors.toList());
		}

		void withHints(TypeHint.Builder hint) {
			hint.withMethod(name, asTypeReferences(), ExecutableMode.INVOKE);
		}
	}

	record Methods(Method... methods) {

		static Methods of(Method... methods) {
			return new Methods(methods);
		}

		void withHints(TypeHint.Builder hint) {
			Stream.of(methods()).forEach(m -> m.withHints(hint));
		}
	}
}
