/*
 * Copyright 2026-present the original author or authors.
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
package org.springframework.shell.core.command.annotation.support;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.shell.core.command.annotation.Command;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link EnableCommandRegistrar} profile filtering support.
 *
 * @author David Pilar
 */
class EnableCommandRegistrarTests {

	@Test
	void commandWithoutProfileShouldAlwaysBeActive() throws Exception {
		Method method = TestCommands.class.getDeclaredMethod("noProfileCommand");
		Profile profile = AnnotatedElementUtils.findMergedAnnotation(method, Profile.class);
		assertNull(profile, "Method without @Profile should not have a Profile annotation");
	}

	@Test
	void commandWithProfileShouldBeActiveWhenProfileMatches() throws Exception {
		MockEnvironment environment = new MockEnvironment();
		environment.setActiveProfiles("greetings");

		Method method = TestCommands.class.getDeclaredMethod("greetingsProfileCommand");
		Profile profile = AnnotatedElementUtils.findMergedAnnotation(method, Profile.class);

		assertNotNull(profile, "Method should have @Profile annotation");
		assertTrue(environment.acceptsProfiles(Profiles.of(profile.value())),
				"Command should be active when 'greetings' profile is active");
	}

	@Test
	void commandWithProfileShouldNotBeActiveWhenProfileDoesNotMatch() throws Exception {
		MockEnvironment environment = new MockEnvironment();
		// no active profiles

		Method method = TestCommands.class.getDeclaredMethod("greetingsProfileCommand");
		Profile profile = AnnotatedElementUtils.findMergedAnnotation(method, Profile.class);

		assertNotNull(profile, "Method should have @Profile annotation");
		assertFalse(environment.acceptsProfiles(Profiles.of(profile.value())),
				"Command should not be active when 'greetings' profile is not active");
	}

	@Test
	void commandWithNegatedProfileShouldBeActiveWhenProfileIsNotSet() throws Exception {
		MockEnvironment environment = new MockEnvironment();
		// no active profiles

		Method method = TestCommands.class.getDeclaredMethod("notProductionCommand");
		Profile profile = AnnotatedElementUtils.findMergedAnnotation(method, Profile.class);

		assertNotNull(profile, "Method should have @Profile annotation");
		assertTrue(environment.acceptsProfiles(Profiles.of(profile.value())),
				"Command with '!production' profile should be active when 'production' is not active");
	}

	@Test
	void commandWithNegatedProfileShouldNotBeActiveWhenProfileIsSet() throws Exception {
		MockEnvironment environment = new MockEnvironment();
		environment.setActiveProfiles("production");

		Method method = TestCommands.class.getDeclaredMethod("notProductionCommand");
		Profile profile = AnnotatedElementUtils.findMergedAnnotation(method, Profile.class);

		assertNotNull(profile, "Method should have @Profile annotation");
		assertFalse(environment.acceptsProfiles(Profiles.of(profile.value())),
				"Command with '!production' profile should not be active when 'production' is active");
	}

	static class TestCommands {

		@Command(name = "no-profile")
		public void noProfileCommand() {
		}

		@Profile("greetings")
		@Command(name = "hello")
		public void greetingsProfileCommand() {
		}

		@Profile("!production")
		@Command(name = "debug-info")
		public void notProductionCommand() {
		}

	}

}
