package org.springframework.roo.support.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

/**
 * Unit tests for {@link StringUtils}.
 *
 * @author Alan Stewart
 * @since 1.1.3
 */
public class StringUtilsTest {

	@Test
	public void testPadRight1() {
		assertEquals("9999", StringUtils.padRight("9", 4, '9'));
	}

	@Test
	public void testPadRight2() {
		assertEquals("Foo999", StringUtils.padRight("Foo", 6, '9'));
	}

	@Test
	public void testPadLeft1() {
		assertEquals("999", StringUtils.padLeft("9", 3, '9'));
	}

	@Test
	public void testPadLeft2() {
		assertEquals("99Foo", StringUtils.padLeft("Foo", 5, '9'));
	}

	@Test
	public void testHasText1() {
		assertTrue(StringUtils.hasText("11111"));
	}

	@Test
	public void testHasText2() {
		assertFalse(StringUtils.hasText("     "));
	}

	@Test
	public void testCountOccurrences() {
		assertEquals(4, StringUtils.countOccurrencesOf("Alan Keith Stewart - Triathlete", " "));
	}
	
	@Test
	public void testCountOccurrencesNull() {
		assertEquals(0, StringUtils.countOccurrencesOf("Alan Keith Stewart - Triathlete", null));
	}

	@Test
	public void testRepeatNull() {
		assertNull(StringUtils.repeat(null, 27));
	}

	@Test
	public void testRepeatEmptyString() {
		assertEquals("", StringUtils.repeat("", 42));
	}

	@Test
	public void testRepeatSpace() {
		assertEquals("    ", StringUtils.repeat(" ", 4));
	}

	@Test
	public void testRepeatSingleCharacter() {
		assertEquals("qqq", StringUtils.repeat("q", 3));
	}

	@Test
	public void testRepeatMultipleCharacters() {
		assertEquals("xyzxyzxyzxyz", StringUtils.repeat("xyz", 4));
	}
	
	@Test
	public void testPrefixNullWithNull() {
		assertNull(StringUtils.prefix(null, null));
	}
	
	@Test
	public void testPrefixNullWithEmpty() {
		assertNull(StringUtils.prefix(null, ""));
	}
	
	@Test
	public void testPrefixNullWithNonEmpty() {
		assertNull(StringUtils.prefix(null, "anything"));
	}
	
	@Test
	public void testPrefixEmptyWithNull() {
		assertEquals("", StringUtils.prefix("", null));
	}
	
	@Test
	public void testPrefixEmptyWithEmpty() {
		assertEquals("", StringUtils.prefix("", ""));
	}
	
	@Test
	public void testPrefixEmptyWithNonEmpty() {
		assertEquals("x", StringUtils.prefix("", "x"));
	}
	
	@Test
	public void testPrefixNonEmptyWithNewPrefix() {
		assertEquals("pre-old", StringUtils.prefix("old", "pre-"));
	}
	
	@Test
	public void testPrefixNonEmptyWithExistingPrefix() {
		assertEquals("pre-old", StringUtils.prefix("pre-old", "pre-"));
	}
	
	@Test
	public void testRemoveNullSuffixFromNullString() {
		assertNull(StringUtils.removeSuffix(null, null));
	}
	
	@Test
	public void testRemoveEmptySuffixFromNullString() {
		assertNull(StringUtils.removeSuffix(null, ""));
	}
	
	@Test
	public void testRemoveNonEmptySuffixFromNullString() {
		assertNull(StringUtils.removeSuffix(null, "anything"));
	}
	
	@Test
	public void testRemoveNullSuffixFromEmptyString() {
		assertEquals("", StringUtils.removeSuffix("", null));
	}
	
	@Test
	public void testRemoveEmptySuffixFromEmptyString() {
		assertEquals("", StringUtils.removeSuffix("", ""));
	}
	
	@Test
	public void testRemoveNonEmptySuffixFromEmptyString() {
		assertEquals("", StringUtils.removeSuffix("", "anything"));
	}
	
	@Test
	public void testRemoveMatchingSuffixFromString() {
		assertEquals("a", StringUtils.removeSuffix("abc", "bc"));
	}	
	
	@Test
	public void testRemoveNonMatchingSuffixFromString() {
		assertEquals("abc", StringUtils.removeSuffix("abc", "BC"));
	}
	
	@Test
	public void testRemoveNullPrefixFromNullString() {
		assertNull(StringUtils.removePrefix(null, null));
	}
	
	@Test
	public void testRemoveEmptyPrefixFromNullString() {
		assertNull(StringUtils.removePrefix(null, ""));
	}
	
	@Test
	public void testRemoveNonEmptyPrefixFromNullString() {
		assertNull(StringUtils.removePrefix(null, "anything"));
	}
	
	@Test
	public void testRemoveNullPrefixFromEmptyString() {
		assertEquals("", StringUtils.removePrefix("", null));
	}
	
	@Test
	public void testRemoveEmptyPrefixFromEmptyString() {
		assertEquals("", StringUtils.removePrefix("", ""));
	}
	
	@Test
	public void testRemoveNonEmptyPrefixFromEmptyString() {
		assertEquals("", StringUtils.removePrefix("", "anything"));
	}
	
	@Test
	public void testRemoveMatchingPrefixFromString() {
		assertEquals("c", StringUtils.removePrefix("abc", "ab"));
	}	
	
	@Test
	public void testRemoveNonMatchingPrefixFromString() {
		assertEquals("abc", StringUtils.removePrefix("abc", "AB"));
	}

	@Test
	public void testSuffixNullWithNull() {
		assertNull(StringUtils.suffix(null, null));
	}
	
	@Test
	public void testSuffixNullWithEmpty() {
		assertNull(StringUtils.suffix(null, ""));
	}
	
	@Test
	public void testSuffixNullWithNonEmpty() {
		assertNull(StringUtils.suffix(null, "anything"));
	}
	
	@Test
	public void testSuffixEmptyWithNull() {
		assertEquals("", StringUtils.suffix("", null));
	}
	
	@Test
	public void testSuffixEmptyWithEmpty() {
		assertEquals("", StringUtils.suffix("", ""));
	}
	
	@Test
	public void testSuffixEmptyWithNonEmpty() {
		assertEquals("x", StringUtils.suffix("", "x"));
	}
	
	@Test
	public void testSuffixNonEmptyWithNewSuffix() {
		assertEquals("old-suf", StringUtils.suffix("old", "-suf"));
	}
	
	@Test
	public void testSuffixNonEmptyWithExistingSuffix() {
		assertEquals("old-suf", StringUtils.suffix("old-suf", "-suf"));
	}
	
	@Test
	public void testNullEqualsNull() {
		assertTrue(StringUtils.equals(null, null));
	}
	
	@Test
	public void testEmptyDoesNotEqualNull() {
		assertFalse(StringUtils.equals("", null));
	}
	
	@Test
	public void testNullDoesNotEqualEmpty() {
		assertFalse(StringUtils.equals(null, ""));
	}
	
	@Test
	public void testUpperDoesNotEqualLower() {
		assertFalse(StringUtils.equals("E", "e"));
	}
	
	@Test
	public void testStringEqualsItself() {
		assertTrue(StringUtils.equals("a", "a"));
	}
	
	@Test
	public void testNullCollectionToDelimitedString() {
		assertEquals("", StringUtils.collectionToDelimitedString(null, "anything"));
	}
	
	@Test
	public void testEmptyCollectionToDelimitedString() {
		assertEquals("", StringUtils.collectionToDelimitedString(Collections.emptySet(), "anything"));
	}
	
	@Test
	public void testSingletonCollectionToDelimitedString() {
		assertEquals("foo", StringUtils.collectionToDelimitedString(Collections.singleton("foo"), "anything"));
	}
	
	@Test
	public void testDoubletonCollectionToDelimitedString() {
		assertEquals("foo:bar", StringUtils.collectionToDelimitedString(Arrays.asList("foo", "bar"), ":"));
	}
	
	@Test
	public void testNullIsBlank() {
		assertTrue(StringUtils.isBlank(null));
	}
	
	@Test
	public void testEmptyStringIsBlank() {
		assertTrue(StringUtils.isBlank(""));
	}
	
	@Test
	public void testSingleSpaceStringIsBlank() {
		assertTrue(StringUtils.isBlank(" "));
	}
	
	@Test
	public void testWhitespaceIsBlank() {
		assertTrue(StringUtils.isBlank("\n\r\t "));
	}
	
	@Test
	public void testNonBlankStringIsNotBlank() {
		assertFalse(StringUtils.isBlank("x"));
	}
	
	@Test
	public void testArrayToDelimitedStringWithNullArray() {
		assertEquals("", StringUtils.arrayToDelimitedString(";", new Object[0]));
	}
	
	@Test
	public void testArrayToDelimitedStringWithEmptyArray() {
		assertEquals("", StringUtils.arrayToDelimitedString(";"));
	}
	
	@Test
	public void testArrayToDelimitedStringWithSingleElementArray() {
		assertEquals("foo", StringUtils.arrayToDelimitedString(";", "foo"));
	}
	
	@Test
	public void testArrayToDelimitedStringWithMultiElementArray() {
		assertEquals("foo;27", StringUtils.arrayToDelimitedString(";", "foo", 27));
	}
	
	@Test
	public void testDefaultIfEmptyWhenValueIsNullAndNoDefaults() {
		assertNull(StringUtils.defaultIfEmpty(null));
	}
	
	@Test
	public void testDefaultIfEmptyWhenValueIsEmptyAndNoDefaults() {
		assertEquals("", StringUtils.defaultIfEmpty(""));
	}
	
	@Test
	public void testDefaultIfEmptyWhenValueIsNullAndOneDefault() {
		assertEquals("x", StringUtils.defaultIfEmpty(null, "x"));
	}
	
	@Test
	public void testDefaultIfEmptyWhenValueIsEmptyAndOneDefault() {
		assertEquals("x", StringUtils.defaultIfEmpty("", "x"));
	}
	
	@Test
	public void testDefaultIfEmptyWhenAllValuesAreBlank() {
		assertEquals("", StringUtils.defaultIfEmpty(null, null, null, ""));
	}
	
	@Test
	public void testDefaultIfEmptyWhenValueIsEmptyAndTwoDefaults() {
		assertEquals("x", StringUtils.defaultIfEmpty("", null, "x"));
	}
	
	@Test
	public void testReplaceAllWhenNoArgumentsAreBlank() {
		// Deliberately chose characters with special meaning to regexs
		assertEquals("[a[b[c[", StringUtils.replace(".a.b.c.", ".", "["));
	}
	
	@Test
	public void testReplaceAllWhenReplacementIsNull() {
		assertEquals(" ", StringUtils.replace(" ", " ", null));
	}
	
	@Test
	public void testReplaceAllWhenReplacementIsEmpty() {
		assertEquals(" a  ", StringUtils.replace(" a b ", "b", ""));
	}
	
	@Test
	public void testReplaceAllWhenReplacementIsWhitespace() {
		assertEquals(" a   ", StringUtils.replace(" a b ", "b", " "));
	}
	
	@Test
	public void testReplaceAllWhenToReplaceIsNull() {
		assertEquals(" ", StringUtils.replace(" ", null, "x"));
	}
	
	@Test
	public void testReplaceAllWhenToReplaceIsEmpty() {
		assertEquals(" ", StringUtils.replace(" ", "", "x"));
	}
	
	@Test
	public void testReplaceAllWhenToReplaceIsWhiteSpace() {
		assertEquals("x", StringUtils.replace(" ", " ", "x"));
	}
	
	@Test
	public void testReplaceAllWhenOriginalIsNull() {
		assertNull(StringUtils.replace(null, "x", "y"));
	}
	
	@Test
	public void testReplaceAllWhenOriginalIsEmpty() {
		assertEquals("", StringUtils.replace("", "x", "y"));
	}
	
	@Test
	public void testReplaceFirstWhenOriginalIsNull() {
		assertNull(StringUtils.replaceFirst(null, "x", "y"));
	}
	
	@Test
	public void testReplaceFirstWhenOriginalIsEmpty() {
		assertEquals("", StringUtils.replaceFirst("", "x", "y"));
	}
	
	@Test
	public void testReplaceFirstWhenOriginalIsWhitespace() {
		assertEquals("[ ", StringUtils.replaceFirst("  ", " ", "["));
	}
	
	@Test
	public void testReplaceFirstWhenToReplaceIsNull() {
		assertEquals(" ", StringUtils.replaceFirst(" ", null, "x"));
	}
	
	@Test
	public void testReplaceFirstWhenToReplaceIsEmpty() {
		assertEquals(" ", StringUtils.replaceFirst(" ", "", "x"));
	}
	
	@Test
	public void testReplaceFirstWhenToReplaceIsWhitespace() {
		assertEquals("x", StringUtils.replaceFirst("x", " ", "y"));
	}
	
	@Test
	public void testReplaceFirstWhenReplacementIsNull() {
		assertEquals("x", StringUtils.replaceFirst("x", "x", null));
	}
	
	@Test
	public void testReplaceFirstWhenReplacementIsEmpty() {
		assertEquals("x", StringUtils.replaceFirst("xx", "x", ""));
	}
	
	@Test
	public void testReplaceFirstWhenReplacementIsWhitespace() {
		assertEquals("x  ", StringUtils.replaceFirst("x y", "y", " "));
	}
	
	@Test
	public void testReplaceFirstWhenNoArgumentsAreBlank() {
		assertEquals("x-yz", StringUtils.replaceFirst("xyyz", "y", "-"));
	}
	
	private static final String[][] SUBSTRING_AFTER_LAST_SCENARIOS = {
		// 0 = original, 1 = separator, 2 = expected result
		{null, "anything", null},
		{"", "anything", ""},
		{"anything", "", ""},
		{"anything", null, ""},
		{"abc", "a", "bc"},
		{"abcba", "b", "a"},
		{"abc", "c", ""},
		{"a", "a", ""},
		{"a", "z", ""}
	};
	
	@Test
	public void testSubstringAfterLast() {
		for (final String[] scenario : SUBSTRING_AFTER_LAST_SCENARIOS) {
			assertEquals(scenario[2], StringUtils.substringAfterLast(scenario[0], scenario[1]));
		}
	}
	
	private static final String[][] CONTAINS_SCENARIOS = {
		{null, "anything", "false"},
		{"anything", null, "false"},
		{"", "", "true"},
		{"abc", "", "true"},
		{"abc", "a", "true"},
		{"abc", "b", "true"},
		{"abc", "c", "true"},
		{"abc", "z", "false"}
	};
	
	@Test
	public void testContains() {
		for (final String[] scenario : CONTAINS_SCENARIOS) {
			assertEquals("Failed on scenario " + Arrays.toString(scenario), Boolean.valueOf(scenario[2]), StringUtils.contains(scenario[0], scenario[1]));
		}
	}
	
	@Test
	public void testTrimToEmpty() {
		String path = " ";
		assertEquals(" roo>", StringUtils.trimToEmpty(path) + " roo>");
	}
	
	@Test
	public void testTrimToEmpty2() {
		String path = null;
		assertEquals(" roo>", StringUtils.trimToEmpty(path) + " roo>");
	}

	@Test
	public void testTrimToEmpty3() {
		String path = "core";
		assertEquals("core roo>", StringUtils.trimToEmpty(path) + " roo>");
	}
}
