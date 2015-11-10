package org.springframework.shell.converters;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;

/**
 * Tests for ArrayConverter.
 *
 * @author Eric Bottard
 */
public class ArrayConverterTest {

	private ArrayConverter arrayConverter;

	@Before
	public void setUp() throws Exception {
		FileConverter fileConverter = new FileConverter() {

			@Override
			protected File getWorkingDirectory() {
				return new File(".");
			}
		};
		arrayConverter = new ArrayConverter();
		Set<Converter<?>> allConverters = new HashSet<Converter<?>>();
		allConverters.add(fileConverter);
		allConverters.add(arrayConverter);
		allConverters.add(new IntegerConverter());
		arrayConverter.setConverters(allConverters);
	}

	@Test
	public void testInferredFileSeparator() throws IOException {

		String raw = "src/main" + File.pathSeparator + "src/main/java";
		File main = new File("src/main").getCanonicalFile();
		File src = new File("src/main/java").getCanonicalFile();


		assertThat(arrayConverter.supports(File[].class, ""), equalTo(true));
		File[] result = (File[]) arrayConverter.convertFromText(raw, File[].class, "");
		assertThat(result, arrayContaining(main, src));

	}

	@Test
	public void testDefaultDelimiter() {
		assertThat(arrayConverter.supports(Integer[].class, ""), equalTo(true));
		Integer[] result = (Integer[]) arrayConverter.convertFromText("1,2,3,4,5", Integer[].class, "");
		assertThat(result, arrayContaining(1, 2, 3, 4, 5));
	}

	@Test
	public void testOverriddenDelimiter() {
		assertThat(arrayConverter.supports(Integer[].class, "splittingRegex=;"), equalTo(true));
		Integer[] result = (Integer[]) arrayConverter.convertFromText("1;2;3;4;5", Integer[].class, "splittingRegex=;");
		assertThat(result, arrayContaining(1, 2, 3, 4, 5));
	}

	@Test
	public void testUnsupportedType() {
		assertThat(arrayConverter.supports(Integer.class, ""), equalTo(false));
		assertThat(arrayConverter.supports(Float[].class, ""), equalTo(false));
	}

	@Test
	public void testOptOut() {
		assertThat(arrayConverter.supports(Integer[].class, "disable-array-converter"), equalTo(false));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCompletions() throws IOException {
		String raw = ("src/test/java/" + File.pathSeparator + "src/main/").replace('/', File.separatorChar);
		String match1 = ("src/test/java/" + File.pathSeparator + "src/main/java/").replace('/', File.separatorChar);
		String match2 = ("src/test/java/" + File.pathSeparator + "src/main/resources/").replace('/', File.separatorChar);

		List<Completion> completions = new ArrayList<Completion>();
		arrayConverter.getAllPossibleValues(completions, File[].class, raw, "", null);
		assertThat(completions, containsInAnyOrder(
				completionWhoseValue(equalTo(match1)),
				completionWhoseValue(equalTo(match2))));
	}

	private Matcher<Completion> completionWhoseValue(final Matcher<String> matcher) {
		return new DiagnosingMatcher<Completion>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("a completion that ").appendDescriptionOf(matcher);
			}

			@Override
			protected boolean matches(Object item, Description mismatchDescription) {
				Completion completion = (Completion) item;
				boolean match = matcher.matches(completion.getValue());
				matcher.describeMismatch(completion.getValue(), mismatchDescription);
				return match;
			}
		};
	}

}