package org.springframework.shell.core.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author David Pilar
 */
class VersionTests {

	private StringWriter writer;

	private CommandContext context;

	@BeforeEach
	void before() {
		writer = new StringWriter();
		context = mock(CommandContext.class);
		when(context.outputWriter()).thenReturn(new PrintWriter(writer));
	}

	@ParameterizedTest
	@MethodSource("versionWithoutDetailsInfoData")
	void testVersionWithoutDetailsInfo(Version.BuildProperties buildProperties, Version.GitProperties gitProperties)
			throws Exception {
		// given
		Version version = new Version();
		version.setBuildProperties(buildProperties);
		version.setGitProperties(gitProperties);

		// when
		version.execute(context);
		String result = writer.toString();

		// then
		assertTrue(result.contains("Version:"));
		assertFalse(result.contains("Build Version:"));
		assertFalse(result.contains("Build Group:"));
		assertFalse(result.contains("Build Artifact:"));
		assertFalse(result.contains("Build Name:"));
		assertFalse(result.contains("Build Time:"));
		assertFalse(result.contains("Git Short Commit ID:"));
		assertFalse(result.contains("Git Commit ID:"));
		assertFalse(result.contains("Git Branch:"));
		assertFalse(result.contains("Git Commit Time:"));
	}

	static Stream<Arguments> versionWithoutDetailsInfoData() {
		return Stream.of(Arguments.of(null, null),
				Arguments.of(new Version.BuildProperties(null, null, null, null, null),
						new Version.GitProperties(null, null, null, null)));
	}

	@Test
	void testVersionWithDetailsInfo() throws Exception {
		// given
		Version.BuildProperties buildProperties = new Version.BuildProperties("group", "artifact", "name", "1.0.0",
				Instant.now());
		Version.GitProperties gitProperties = new Version.GitProperties("branch", "commitId", "shortCommitId",
				Instant.now());

		Version version = new Version();
		version.setBuildProperties(buildProperties);
		version.setGitProperties(gitProperties);

		// when
		version.execute(context);
		String result = writer.toString();

		// then
		assertTrue(result.contains("Version: " + buildProperties.version()));
		assertTrue(result.contains("Build Group: " + buildProperties.group()));
		assertTrue(result.contains("Build Artifact: " + buildProperties.artifact()));
		assertTrue(result.contains("Build Name: " + buildProperties.name()));
		assertTrue(result.contains("Build Time: " + buildProperties.time()));
		assertTrue(result.contains("Git Short Commit ID: " + gitProperties.shortCommitId()));
		assertTrue(result.contains("Git Commit ID: " + gitProperties.commitId()));
		assertTrue(result.contains("Git Branch: " + gitProperties.branch()));
		assertTrue(result.contains("Git Commit Time: " + gitProperties.commitTime()));
	}

}