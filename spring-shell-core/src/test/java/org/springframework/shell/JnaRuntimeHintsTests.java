package org.springframework.shell;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.TypeHint;
import org.springframework.aot.hint.TypeReference;

import static org.assertj.core.api.Assertions.assertThat;

public class JnaRuntimeHintsTests {

	@Test
	void test() {
		String[] classNames = Arrays.asList("com.sun.jna.CallbackReference", "com.sun.jna.Native",
				"com.sun.jna.NativeLong", "com.sun.jna.Pointer", "com.sun.jna.Structure",
				"com.sun.jna.ptr.IntByReference", "com.sun.jna.ptr.PointerByReference", "com.sun.jna.Klass",
				"com.sun.jna.Structure$FFIType", "com.sun.jna.Structure$FFIType$size_t").toArray(new String[0]);
		ReflectionHints hints = registerHints();
		typeReferences(classNames).forEach(typeReference -> {
			TypeHint typeHint = hints.getTypeHint(typeReference);
			assertThat(typeHint).withFailMessage(() -> "No hints found for typeReference " + typeReference).isNotNull();
			Set<MemberCategory> memberCategories = typeHint.getMemberCategories();
			assertThat(memberCategories).containsExactlyInAnyOrder(MemberCategory.DECLARED_CLASSES,
					MemberCategory.DECLARED_FIELDS, MemberCategory.PUBLIC_CLASSES, MemberCategory.PUBLIC_FIELDS,
					MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS);
		});
	}

	private ReflectionHints registerHints() {
		RuntimeHints hints = new RuntimeHints();
		new JnaRuntimeHints().registerHints(hints, getClass().getClassLoader());
		return hints.reflection();
	}

	private Stream<TypeReference> typeReferences(String... classNames) {
		return Stream.of(classNames).map(TypeReference::of);
	}
}
