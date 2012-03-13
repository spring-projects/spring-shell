package org.springframework.roo.support.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * Miscellaneous {@link String} utility methods.
 *
 * <p>Mainly for internal use within the framework; consider
 * <a href="http://jakarta.apache.org/commons/lang/">Jakarta's Commons Lang</a>
 * for a more comprehensive suite of String utilities.
 *
 * <p>This class delivers some simple functionality that should really
 * be provided by the core Java <code>String</code> and {@link StringBuilder}
 * classes, such as the ability to {@link #replace} all occurrences of a given
 * substring in a target string. It also provides easy-to-use methods to convert
 * between delimited strings, such as CSV strings, and collections and arrays.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Keith Donald
 * @author Rob Harrop
 * @author Rick Evans
 * @author Arjen Poutsma
 * @since 16 April 2001
 * @see org.apache.commons.lang.StringUtils
 */
public final class StringUtils {

	// Constants
	private static final String FOLDER_SEPARATOR = "/";
	private static final String WINDOWS_FOLDER_SEPARATOR = "\\";
	private static final String TOP_PATH = "..";
	private static final String CURRENT_PATH = ".";
	private static final char EXTENSION_SEPARATOR = '.';

	/**
	 * The platform-specific line separator.
	 *
	 * @since 1.2.0
	 */
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	//---------------------------------------------------------------------
	// General convenience methods for working with Strings
	//---------------------------------------------------------------------

	/**
	 * Check that the given CharSequence is neither <code>null</code> nor of length 0.
	 * Note: Will return <code>true</code> for a CharSequence that purely consists of whitespace.
	 * <p><pre>
	 * StringUtils.hasLength(null) = false
	 * StringUtils.hasLength("") = false
	 * StringUtils.hasLength(" ") = true
	 * StringUtils.hasLength("Hello") = true
	 * </pre>
	 * @param str the CharSequence to check (may be <code>null</code>)
	 * @return <code>true</code> if the CharSequence is not null and has length
	 * @see #hasText(String)
	 */
	public static boolean hasLength(final CharSequence str) {
		return (str != null && str.length() > 0);
	}

	/**
	 * Check that the given String is neither <code>null</code> nor of length 0.
	 * Note: Will return <code>true</code> for a String that purely consists of whitespace.
	 * @param str the String to check (may be <code>null</code>)
	 * @return <code>true</code> if the String is not null and has length
	 * @see #hasLength(CharSequence)
	 */
	public static boolean hasLength(final String str) {
		return hasLength((CharSequence) str);
	}

	/**
	 * Check whether the given CharSequence has actual text.
	 * More specifically, returns <code>true</code> if the string not <code>null</code>,
	 * its length is greater than 0, and it contains at least one non-whitespace character.
	 * <p><pre>
	 * StringUtils.hasText(null) = false
	 * StringUtils.hasText("") = false
	 * StringUtils.hasText(" ") = false
	 * StringUtils.hasText("12345") = true
	 * StringUtils.hasText(" 12345 ") = true
	 * </pre>
	 * @param str the CharSequence to check (may be <code>null</code>)
	 * @return <code>true</code> if the CharSequence is not <code>null</code>,
	 * its length is greater than 0, and it does not contain whitespace only
	 * @see java.lang.Character#isWhitespace
	 */
	public static boolean hasText(final CharSequence str) {
		if (!hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the given String has actual text.
	 * More specifically, returns <code>true</code> if the string not <code>null</code>,
	 * its length is greater than 0, and it contains at least one non-whitespace character.
	 * @param str the String to check (may be <code>null</code>)
	 * @return <code>true</code> if the String is not <code>null</code>, its length is
	 * greater than 0, and it does not contain whitespace only
	 * @see #hasText(CharSequence)
	 */
	public static boolean hasText(final String str) {
		return hasText((CharSequence) str);
	}
	
	/**
	 * Indicates whether the given substring occurs within the given string.
	 * Inspired by the eponymous method in commons-lang.
	 * <pre>
		StringUtils.contains(null, *)     = false
		StringUtils.contains(*, null)     = false
		StringUtils.contains("", "")      = true
		StringUtils.contains("abc", "")   = true
		StringUtils.contains("abc", "a")  = true
		StringUtils.contains("abc", "z")  = false</pre>
	 * 
	 * @param str the string to look within (can be <code>null</code>)
	 * @param substr the string to look for (can be <code>null</code>)
	 * @return see above
	 * @since 1.2.0
	 */
	public static boolean contains(final String str, final String substr) {
		if (str == null || substr == null) {
			return false;
		}
		return str.contains(substr);
	}

	/**
	 * Check whether the given CharSequence contains any whitespace characters.
	 * @param str the CharSequence to check (may be <code>null</code>)
	 * @return <code>true</code> if the CharSequence is not empty and
	 * contains at least 1 whitespace character
	 * @see java.lang.Character#isWhitespace
	 */
	public static boolean containsWhitespace(final CharSequence str) {
		if (!hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0, n = strLen; i < n; i++) {
			if (Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the given String contains any whitespace characters.
	 * @param str the String to check (may be <code>null</code>)
	 * @return <code>true</code> if the String is not empty and
	 * contains at least 1 whitespace character
	 * @see #containsWhitespace(CharSequence)
	 */
	public static boolean containsWhitespace(final String str) {
		return containsWhitespace((CharSequence) str);
	}

	/**
	 * Trim leading and trailing whitespace from the given String.
	 * @param str the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimWhitespace(final String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
			sb.deleteCharAt(0);
		}
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * Trim <i>all</i> whitespace from the given String:
	 * leading, trailing, and inbetween characters.
	 * @param str the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimAllWhitespace(final String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		int index = 0;
		while (sb.length() > index) {
			if (Character.isWhitespace(sb.charAt(index))) {
				sb.deleteCharAt(index);
			}
			else {
				index++;
			}
		}
		return sb.toString();
	}

	/**
	 * Trim leading whitespace from the given String.
	 * @param str the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimLeadingWhitespace(final String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	/**
	 * Trim trailing whitespace from the given String.
	 * @param str the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimTrailingWhitespace(final String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * Trim all occurences of the supplied leading character from the given String.
	 * @param str the String to check
	 * @param leadingCharacter the leading character to be trimmed
	 * @return the trimmed String
	 */
	public static String trimLeadingCharacter(final String str, final char leadingCharacter) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && sb.charAt(0) == leadingCharacter) {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	/**
	 * Trim all occurences of the supplied trailing character from the given String.
	 * @param str the String to check
	 * @param trailingCharacter the trailing character to be trimmed
	 * @return the trimmed String
	 */
	public static String trimTrailingCharacter(final String str, final char trailingCharacter) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && sb.charAt(sb.length() - 1) == trailingCharacter) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * Test if the given String starts with the specified prefix,
	 * ignoring upper/lower case.
	 * @param str the String to check
	 * @param prefix the prefix to look for
	 * @see java.lang.String#startsWith
	 */
	public static boolean startsWithIgnoreCase(final String str, final String prefix) {
		if (str == null || prefix == null) {
			return false;
		}
		if (str.startsWith(prefix)) {
			return true;
		}
		if (str.length() < prefix.length()) {
			return false;
		}
		String lcStr = str.substring(0, prefix.length()).toLowerCase();
		String lcPrefix = prefix.toLowerCase();
		return lcStr.equals(lcPrefix);
	}

	/**
	 * Test if the given String ends with the specified suffix,
	 * ignoring upper/lower case.
	 * @param str the String to check
	 * @param suffix the suffix to look for
	 * @see java.lang.String#endsWith
	 */
	public static boolean endsWithIgnoreCase(final String str, final String suffix) {
		if (str == null || suffix == null) {
			return false;
		}
		if (str.endsWith(suffix)) {
			return true;
		}
		if (str.length() < suffix.length()) {
			return false;
		}

		String lcStr = str.substring(str.length() - suffix.length()).toLowerCase();
		String lcSuffix = suffix.toLowerCase();
		return lcStr.equals(lcSuffix);
	}

	/**
	 * Test whether the given string matches the given substring
	 * at the given index.
	 * @param str the original string (or StringBuilder)
	 * @param index the index in the original string to start matching against
	 * @param substring the substring to match at the given index
	 */
	public static boolean substringMatch(final CharSequence str, final int index, final CharSequence substring) {
		for (int j = 0, n = substring.length(); j < n; j++) {
			int i = index + j;
			if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Count the occurrences of the substring in string s.
	 * @param str string to search in. Return 0 if this is null.
	 * @param sub string to search for. Return 0 if this is null.
	 */
	public static int countOccurrencesOf(final String str, final String sub) {
		if (!hasLength(str) || !hasLength(sub)) {
			return 0;
		}
		int count = 0, pos = 0, idx = 0;
		while ((idx = str.indexOf(sub, pos)) != -1) {
			++count;
			pos = idx + sub.length();
		}
		return count;
	}

	/**
	 * Returns the given string repeated the given number of times
	 *
	 * @param str the string to repeat (can be null or empty)
	 * @param times the number of times to repeat it
	 * @return <code>null</code> if <code>null</code> is given
	 */
	public static String repeat(final String str, final int times) {
		if (!hasLength(str)) {
			return str;
		}
		final StringBuilder sb = new StringBuilder(str.length() * times);
		for (int i = 0; i < times; i++) {
			sb.append(str);
		}
		return sb.toString();
	}

	/**
	 * Replaces all occurrences of one string within another.
	 *
	 * @param original the string to modify (can be zero length to do nothing)
	 * @param toReplace the string to replace (can be blank to do nothing)
	 * @param replacement the string to replace it with (can be <code>null</code> to do nothing)
	 * @return the original string, modified as necessary
	 */
	public static String replace(final String original, final String toReplace, final String replacement) {
		String result = original;
		String previousResult;
		do {
			previousResult = result;
			result = replaceFirst(previousResult, toReplace, replacement);
		} while (!equals(previousResult, result));
		return result;
	}

	/**
	 * Delete all occurrences of the given substring.
	 * @param inString the original String
	 * @param pattern the pattern to delete all occurrences of
	 * @return the resulting String
	 */
	public static String delete(final String inString, final String pattern) {
		return replace(inString, pattern, "");
	}

	/**
	 * Delete any character in a given String.
	 * @param inString the original String
	 * @param charsToDelete a set of characters to delete.
	 * E.g. "az\n" will delete 'a's, 'z's and new lines.
	 * @return the resulting String
	 */
	public static String deleteAny(final String inString, final String charsToDelete) {
		if (!hasLength(inString) || !hasLength(charsToDelete)) {
			return inString;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0, n = inString.length(); i < n; i++) {
			char c = inString.charAt(i);
			if (charsToDelete.indexOf(c) == -1) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	//---------------------------------------------------------------------
	// Convenience methods for working with formatted Strings
	//---------------------------------------------------------------------

	/**
	 * Quote the given String with single quotes.
	 * @param str the input String (e.g. "myString")
	 * @return the quoted String (e.g. "'myString'"),
	 * or <code>null<code> if the input was <code>null</code>
	 */
	public static String quote(final String str) {
		return (str != null ? "'" + str + "'" : null);
	}

	/**
	 * Turn the given Object into a String with single quotes
	 * if it is a String; keeping the Object as-is else.
	 * @param obj the input Object (e.g. "myString")
	 * @return the quoted String (e.g. "'myString'"),
	 * or the input object as-is if not a String
	 */
	public static Object quoteIfString(final Object obj) {
		return (obj instanceof String ? quote((String) obj) : obj);
	}

	/**
	 * Unqualify a string qualified by a '.' dot character. For example,
	 * "this.name.is.qualified", returns "qualified".
	 * @param qualifiedName the qualified name
	 */
	public static String unqualify(final String qualifiedName) {
		return unqualify(qualifiedName, '.');
	}

	/**
	 * Unqualify a string qualified by a separator character. For example,
	 * "this:name:is:qualified" returns "qualified" if using a ':' separator.
	 * @param qualifiedName the qualified name
	 * @param separator the separator
	 */
	public static String unqualify(final String qualifiedName, final char separator) {
		return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
	}

	/**
	 * Capitalize a <code>String</code>, changing the first letter to
	 * upper case as per {@link Character#toUpperCase(char)}.
	 * No other letters are changed.
	 * @param str the String to capitalize, may be <code>null</code>
	 * @return the capitalized String, <code>null</code> if null
	 */
	public static String capitalize(final String str) {
		return changeFirstCharacterCase(str, true);
	}

	/**
	 * Uncapitalize a <code>String</code>, changing the first letter to
	 * lower case as per {@link Character#toLowerCase(char)}.
	 * No other letters are changed.
	 * @param str the String to uncapitalize, may be <code>null</code>
	 * @return the uncapitalized String, <code>null</code> if null
	 */
	public static String uncapitalize(final String str) {
		return changeFirstCharacterCase(str, false);
	}

	private static String changeFirstCharacterCase(final String str, final boolean capitalize) {
		if (!hasText(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str.length());
		if (capitalize) {
			sb.append(Character.toUpperCase(str.charAt(0)));
		} else {
			sb.append(Character.toLowerCase(str.charAt(0)));
		}
		sb.append(str.substring(1));
		return sb.toString();
	}

	/**
	 * Extract the filename from the given path,
	 * e.g. "mypath/myfile.txt" -> "myfile.txt".
	 * @param path the file path (may be <code>null</code>)
	 * @return the extracted filename, or <code>null</code> if none
	 */
	public static String getFilename(final String path) {
		if (path == null) {
			return null;
		}
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
	}

	/**
	 * Extract the filename extension from the given path,
	 * e.g. "mypath/myfile.txt" -> "txt".
	 * @param path the file path (may be <code>null</code>)
	 * @return the extracted filename extension, or <code>null</code> if none
	 */
	public static String getFilenameExtension(final String path) {
		if (path == null) {
			return null;
		}
		int sepIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
		return (sepIndex != -1 ? path.substring(sepIndex + 1) : null);
	}

	/**
	 * Strip the filename extension from the given path,
	 * e.g. "mypath/myfile.txt" -> "mypath/myfile".
	 * @param path the file path (may be <code>null</code>)
	 * @return the path with stripped filename extension,
	 * or <code>null</code> if none
	 */
	public static String stripFilenameExtension(final String path) {
		if (path == null) {
			return null;
		}
		int sepIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
		return (sepIndex != -1 ? path.substring(0, sepIndex) : path);
	}

	/**
	 * Apply the given relative path to the given path,
	 * assuming standard Java folder separation (i.e. "/" separators);
	 * @param path the path to start from (usually a full file path)
	 * @param relativePath the relative path to apply
	 * (relative to the full file path above)
	 * @return the full file path that results from applying the relative path
	 */
	public static String applyRelativePath(final String path, final String relativePath) {
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		if (separatorIndex != -1) {
			String newPath = path.substring(0, separatorIndex);
			if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
				newPath += FOLDER_SEPARATOR;
			}
			return newPath + relativePath;
		}
		return relativePath;
	}

	/**
	 * Normalize the path by suppressing sequences like "path/.." and
	 * inner simple dots.
	 * <p>The result is convenient for path comparison. For other uses,
	 * notice that Windows separators ("\") are replaced by simple slashes.
	 * @param path the original path
	 * @return the normalized path
	 */
	public static String cleanPath(final String path) {
		if (path == null) {
			return null;
		}
		String pathToUse = replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);

		// Strip prefix from path to analyze, to not treat it as part of the
		// first path element. This is necessary to correctly parse paths like
		// "file:core/../core/io/Resource.class", where the ".." should just
		// strip the first "core" directory while keeping the "file:" prefix.
		int prefixIndex = pathToUse.indexOf(":");
		String prefix = "";
		if (prefixIndex != -1) {
			prefix = pathToUse.substring(0, prefixIndex + 1);
			pathToUse = pathToUse.substring(prefixIndex + 1);
		}
		if (pathToUse.startsWith(FOLDER_SEPARATOR)) {
			prefix = prefix + FOLDER_SEPARATOR;
			pathToUse = pathToUse.substring(1);
		}

		String[] pathArray = delimitedListToStringArray(pathToUse, FOLDER_SEPARATOR);
		List<String> pathElements = new ArrayList<String>();
		int tops = 0;

		for (int i = pathArray.length - 1; i >= 0; i--) {
			String element = pathArray[i];
			if (CURRENT_PATH.equals(element)) {
				// Points to current directory - drop it.
			} else if (TOP_PATH.equals(element)) {
				// Registering top path found.
				tops++;
			} else {
				if (tops > 0) {
					// Merging path element with element corresponding to top path.
					tops--;
				}
				else {
					// Normal path element found.
					pathElements.add(0, element);
				}
			}
		}

		// Remaining top paths need to be retained.
		for (int i = 0; i < tops; i++) {
			pathElements.add(0, TOP_PATH);
		}

		return prefix + collectionToDelimitedString(pathElements, FOLDER_SEPARATOR);
	}

	/**
	 * Compare two paths after normalization of them.
	 * @param path1 first path for comparison
	 * @param path2 second path for comparison
	 * @return whether the two paths are equivalent after normalization
	 */
	public static boolean pathEquals(final String path1, final String path2) {
		return cleanPath(path1).equals(cleanPath(path2));
	}

	/**
	 * Parse the given <code>localeString</code> into a {@link Locale}.
	 * <p>This is the inverse operation of {@link Locale#toString Locale's toString}.
	 * @param localeString the locale string, following <code>Locale's</code>
	 * <code>toString()</code> format ("en", "en_UK", etc);
	 * also accepts spaces as separators, as an alternative to underscores
	 * @return a corresponding <code>Locale</code> instance
	 */
	public static Locale parseLocaleString(final String localeString) {
		String[] parts = tokenizeToStringArray(localeString, "_ ", false, false);
		String language = (parts.length > 0 ? parts[0] : "");
		String country = (parts.length > 1 ? parts[1] : "");
		String variant = "";
		if (parts.length >= 2) {
			// There is definitely a variant, and it is everything after the country
			// code sans the separator between the country code and the variant.
			int endIndexOfCountryCode = localeString.indexOf(country) + country.length();
			// Strip off any leading '_' and whitespace, what's left is the variant.
			variant = trimLeadingWhitespace(localeString.substring(endIndexOfCountryCode));
			if (variant.startsWith("_")) {
				variant = trimLeadingCharacter(variant, '_');
			}
		}
		return (language.length() > 0 ? new Locale(language, country, variant) : null);
	}

	/**
	 * Determine the RFC 3066 compliant language tag,
	 * as used for the HTTP "Accept-Language" header.
	 * @param locale the Locale to transform to a language tag
	 * @return the RFC 3066 compliant language tag as String
	 */
	public static String toLanguageTag(final Locale locale) {
		return locale.getLanguage() + (hasText(locale.getCountry()) ? "-" + locale.getCountry() : "");
	}

	//---------------------------------------------------------------------
	// Convenience methods for working with String arrays
	//---------------------------------------------------------------------

	/**
	 * Append the given String to the given String array, returning a new array
	 * consisting of the input array contents plus the given String.
	 * @param arr the array to append to (can be <code>null</code>)
	 * @param str the String to append
	 * @return the new array (never <code>null</code>)
	 */
	public static String[] addStringToArray(final String[] arr, final String str) {
		if (ObjectUtils.isEmpty(arr)) {
			return new String[] {str};
		}
		String[] newArr = new String[arr.length + 1];
		System.arraycopy(arr, 0, newArr, 0, arr.length);
		newArr[arr.length] = str;
		return newArr;
	}

	/**
	 * Concatenate the given String arrays into one,
	 * with overlapping array elements included twice.
	 * <p>The order of elements in the original arrays is preserved.
	 * @param arr1 the first array (can be <code>null</code>)
	 * @param arr2 the second array (can be <code>null</code>)
	 * @return the new array (<code>null</code> if both given arrays were <code>null</code>)
	 */
	public static String[] concatenateStringArrays(final String[] arr1, final String[] arr2) {
		if (ObjectUtils.isEmpty(arr1)) {
			return arr2;
		}
		if (ObjectUtils.isEmpty(arr2)) {
			return arr1;
		}
		String[] newArr = new String[arr1.length + arr2.length];
		System.arraycopy(arr1, 0, newArr, 0, arr1.length);
		System.arraycopy(arr2, 0, newArr, arr1.length, arr2.length);
		return newArr;
	}

	/**
	 * Merge the given String arrays into one, with overlapping
	 * array elements only included once.
	 * <p>The order of elements in the original arrays is preserved
	 * (with the exception of overlapping elements, which are only
	 * included on their first occurrence).
	 * @param arr1 the first array (can be <code>null</code>)
	 * @param arr2 the second array (can be <code>null</code>)
	 * @return the new array (<code>null</code> if both given arrays were <code>null</code>)
	 */
	public static String[] mergeStringArrays(final String[] arr1, final String[] arr2) {
		if (ObjectUtils.isEmpty(arr1)) {
			return arr2;
		}
		if (ObjectUtils.isEmpty(arr2)) {
			return arr1;
		}
		List<String> result = new ArrayList<String>();
		result.addAll(Arrays.asList(arr1));
		for (int i = 0, n = arr2.length; i < n; i++) {
			String str = arr2[i];
			if (!result.contains(str)) {
				result.add(str);
			}
		}
		return toStringArray(result);
	}

	/**
	 * Turn given source String array into sorted array.
	 * @param arr the source array
	 * @return the sorted array (never <code>null</code>)
	 */
	public static String[] sortStringArray(final String[] arr) {
		if (ObjectUtils.isEmpty(arr)) {
			return new String[0];
		}
		Arrays.sort(arr);
		return arr;
	}

	/**
	 * Copy the given Collection into a String array.
	 * The Collection must contain String elements only.
	 * @param coll the Collection to copy
	 * @return the String array (<code>null</code> if the passed-in
	 * Collection was <code>null</code>)
	 */
	public static String[] toStringArray(final Collection<String> coll) {
		if (coll == null) {
			return null;
		}
		return coll.toArray(new String[coll.size()]);
	}

	/**
	 * Copy the given Enumeration into a String array.
	 * The Enumeration must contain String elements only.
	 * @param enumeration the Enumeration to copy
	 * @return the String array (<code>null</code> if the passed-in
	 * Enumeration was <code>null</code>)
	 */
	public static String[] toStringArray(final Enumeration<String> enumeration) {
		if (enumeration == null) {
			return null;
		}
		List<String> list = Collections.list(enumeration);
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Trim the elements of the given String array,
	 * calling <code>String.trim()</code> on each of them.
	 * @param arr the original String array
	 * @return the resulting array (of the same size) with trimmed elements
	 */
	public static String[] trimArrayElements(final String[] arr) {
		if (ObjectUtils.isEmpty(arr)) {
			return new String[0];
		}
		String[] result = new String[arr.length];
		for (int i = 0, n = arr.length; i < n; i++) {
			String element = arr[i];
			result[i] = (element != null ? element.trim() : null);
		}
		return result;
	}

	/**
	 * Remove duplicate Strings from the given array.
	 * Also sorts the array, as it uses a TreeSet.
	 * @param arr the String array
	 * @return an array without duplicates, in natural sort order
	 */
	public static String[] removeDuplicateStrings(final String[] arr) {
		if (ObjectUtils.isEmpty(arr)) {
			return arr;
		}
		Set<String> set = new TreeSet<String>();
		for (int i = 0, n = arr.length; i < n; i++) {
			set.add(arr[i]);
		}
		return toStringArray(set);
	}

	/**
	 * Split a String at the first occurrence of the delimiter.
	 * Does not include the delimiter in the result.
	 * @param toSplit the string to split
	 * @param delim to split the string up with
	 * @return a two element array with index 0 being before the delimiter, and
	 * index 1 being after the delimiter (neither element includes the delimiter);
	 * or <code>null</code> if the delimiter wasn't found in the given input String
	 */
	public static String[] split(final String toSplit, final String delim) {
		if (!hasLength(toSplit) || !hasLength(delim)) {
			return null;
		}
		int offset = toSplit.indexOf(delim);
		if (offset < 0) {
			return null;
		}
		String beforeDelimiter = toSplit.substring(0, offset);
		String afterDelimiter = toSplit.substring(offset + delim.length());
		return new String[] {beforeDelimiter, afterDelimiter};
	}

	/**
	 * Take an array Strings and split each element based on the given delimiter.
	 * A <code>Properties</code> instance is then generated, with the left of the
	 * delimiter providing the key, and the right of the delimiter providing the value.
	 * <p>Will trim both the key and value before adding them to the
	 * <code>Properties</code> instance.
	 * @param arr the array to process
	 * @param delim to split each element using (typically the equals symbol)
	 * @return a <code>Properties</code> instance representing the array contents,
	 * or <code>null</code> if the array to process was null or empty
	 */
	public static Properties splitArrayElementsIntoProperties(final String[] arr, final String delim) {
		return splitArrayElementsIntoProperties(arr, delim, null);
	}

	/**
	 * Take an array Strings and split each element based on the given delimiter.
	 * A <code>Properties</code> instance is then generated, with the left of the
	 * delimiter providing the key, and the right of the delimiter providing the value.
	 * <p>Will trim both the key and value before adding them to the
	 * <code>Properties</code> instance.
	 * 
	 * @param arr the array to process
	 * @param delim to split each element using (typically the equals symbol)
	 * @param charsToDelete one or more characters to remove from each element
	 * prior to attempting the split operation (typically the quotation mark
	 * symbol), or <code>null</code> if no removal should occur
	 * @return a <code>Properties</code> instance representing the array contents,
	 * or <code>null</code> if the array to process was <code>null</code> or empty
	 */
	public static Properties splitArrayElementsIntoProperties(final String[] arr, final String delim, final String charsToDelete) {
		if (ObjectUtils.isEmpty(arr)) {
			return null;
		}

		Properties result = new Properties();
		for (int i = 0, n = arr.length; i < n; i++) {
			String element = arr[i];
			if (charsToDelete != null) {
				element = deleteAny(arr[i], charsToDelete);
			}
			String[] splittedElement = split(element, delim);
			if (splittedElement == null) {
				continue;
			}
			result.setProperty(splittedElement[0].trim(), splittedElement[1].trim());
		}
		return result;
	}

	/**
	 * Tokenize the given String into a String array via a StringTokenizer.
	 * Trims tokens and omits empty tokens.
	 * <p>The given delimiters string is supposed to consist of any number of
	 * delimiter characters. Each of those characters can be used to separate
	 * tokens. A delimiter is always a single character; for multi-character
	 * delimiters, consider using <code>delimitedListToStringArray</code>
	 * 
	 * @param str the String to tokenize
	 * @param delimiters the delimiter characters, assembled as String
	 * (each of those characters is individually considered as delimiter).
	 * @return an array of the tokens
	 * @see java.util.StringTokenizer
	 * @see java.lang.String#trim()
	 * @see #delimitedListToStringArray
	 */
	public static String[] tokenizeToStringArray(final String str, final String delimiters) {
		return tokenizeToStringArray(str, delimiters, true, true);
	}

	/**
	 * Tokenize the given String into a String array via a StringTokenizer.
	 * <p>The given delimiters string is supposed to consist of any number of
	 * delimiter characters. Each of those characters can be used to separate
	 * tokens. A delimiter is always a single character; for multi-character
	 * delimiters, consider using <code>delimitedListToStringArray</code>
	 * @param str the String to tokenize
	 * @param delimiters the delimiter characters, assembled as String
	 * (each of those characters is individually considered as delimiter)
	 * @param trimTokens trim the tokens via String's <code>trim</code>
	 * @param ignoreEmptyTokens omit empty tokens from the result array
	 * (only applies to tokens that are empty after trimming; StringTokenizer
	 * will not consider subsequent delimiters as token in the first place).
	 * @return an array of the tokens (<code>null</code> if the input String
	 * was <code>null</code>)
	 * @see java.util.StringTokenizer
	 * @see java.lang.String#trim()
	 * @see #delimitedListToStringArray
	 */
	public static String[] tokenizeToStringArray(final String str, final String delimiters, final boolean trimTokens, final boolean ignoreEmptyTokens) {
		if (str == null) {
			return null;
		}

		StringTokenizer st = new StringTokenizer(str, delimiters);
		List<String> tokens = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || token.length() > 0) {
				tokens.add(token);
			}
		}
		return toStringArray(tokens);
	}

	/**
	 * Take a String which is a delimited list and convert it to a String array.
	 * <p>A single delimiter can consists of more than one character: It will still
	 * be considered as single delimiter string, rather than as bunch of potential
	 * delimiter characters - in contrast to <code>tokenizeToStringArray</code>.
	 * @param str the input String
	 * @param delim the delimiter between elements (this is a single delimiter,
	 * rather than a bunch individual delimiter characters)
	 * @return an array of the tokens in the list
	 * @see #tokenizeToStringArray
	 */
	public static String[] delimitedListToStringArray(final String str, final String delim) {
		return delimitedListToStringArray(str, delim, null);
	}

	/**
	 * Take a String which is a delimited list and convert it to a String array.
	 * <p>A single delimiter can consists of more than one character: It will still
	 * be considered as single delimiter string, rather than as bunch of potential
	 * delimiter characters - in contrast to <code>tokenizeToStringArray</code>.
	 * @param str the input String
	 * @param delimiter the delimiter between elements (this is a single delimiter,
	 * rather than a bunch individual delimiter characters)
	 * @param charsToDelete a set of characters to delete. Useful for deleting unwanted
	 * line breaks: e.g. "\r\n\f" will delete all new lines and line feeds in a String.
	 * @return an array of the tokens in the list
	 * @see #tokenizeToStringArray
	 */
	public static String[] delimitedListToStringArray(final String str, final String delimiter, final String charsToDelete) {
		if (str == null) {
			return new String[0];
		}
		if (delimiter == null) {
			return new String[] {str};
		}
		List<String> result = new ArrayList<String>();
		if ("".equals(delimiter)) {
			for (int i = 0; i < str.length(); i++) {
				result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
			}
		}
		else {
			int pos = 0;
			int delPos = 0;
			while ((delPos = str.indexOf(delimiter, pos)) != -1) {
				result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
				pos = delPos + delimiter.length();
			}
			if (str.length() > 0 && pos <= str.length()) {
				// Add rest of String, but not in case of empty input.
				result.add(deleteAny(str.substring(pos), charsToDelete));
			}
		}
		return toStringArray(result);
	}

	/**
	 * Convert a CSV list into an array of Strings.
	 * @param str the input String
	 * @return an array of Strings, or the empty array in case of empty input
	 */
	public static String[] commaDelimitedListToStringArray(final String str) {
		return delimitedListToStringArray(str, ",");
	}

	/**
	 * Convenience method to convert a CSV string list to a set.
	 * Note that this will suppress duplicates.
	 * @param str the input String
	 * @return a Set of String entries in the list
	 */
	public static Set<String> commaDelimitedListToSet(final String str) {
		Set<String> set = new TreeSet<String>();
		String[] tokens = commaDelimitedListToStringArray(str);
		for (int i = 0; i < tokens.length; i++) {
			set.add(tokens[i]);
		}
		return set;
	}

	/**
	 * Convenience method to return a Collection as a delimited (e.g. CSV)
	 * String. E.g. useful for <code>toString()</code> implementations.
	 * @param coll the Collection to display
	 * @param delim the delimiter to use (probably a ",")
	 * @param prefix the String to start each element with
	 * @param suffix the String to end each element with
	 * @return the delimited String (never <code>null</code>)
	 */
	public static String collectionToDelimitedString(final Collection<?> coll, final String delim, final String prefix, final String suffix) {
		if (CollectionUtils.isEmpty(coll)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		Iterator<?> it = coll.iterator();
		while (it.hasNext()) {
			sb.append(prefix).append(it.next()).append(suffix);
			if (it.hasNext() && delim != null) {
				sb.append(delim);
			}
		}
		return sb.toString();
	}

	/**
	 * Concatenates the given collection using the given delimiter between each item.
	 * 
	 * @param coll the collection to display (can be null or empty)
	 * @param delim the delimiter to use (can be null or empty for none)
	 * @return the delimited String (never <code>null</code>)
	 */
	public static String collectionToDelimitedString(final Collection<?> coll, final String delim) {
		return collectionToDelimitedString(coll, delim, "", "");
	}

	/**
	 * Convenience method to return a Collection as a CSV String.
	 * E.g. useful for <code>toString()</code> implementations.
	 * @param coll the Collection to display
	 * @return the delimited String
	 */
	public static String collectionToCommaDelimitedString(final Collection<?> coll) {
		return collectionToDelimitedString(coll, ",");
	}

	/**
	 * Convenience method to return a String array as a delimited (e.g. CSV)
	 * String. E.g. useful for <code>toString()</code> implementations.
	 * 
	 * @param arr the array to display
	 * @param delim the delimiter to use (probably a ",")
	 * @return the delimited String
	 * @since 1.2.0
	 */
	public static String arrayToDelimitedString(final String delim, final Object... arr) {
		return arrayToDelimitedString(arr, delim);
	}
	
	/**
	 * Convenience method to return a String array as a delimited (e.g. CSV)
	 * String. E.g. useful for <code>toString()</code> implementations.
	 * 
	 * @param arr the array to display
	 * @param delim the delimiter to use (probably a ",")
	 * @return the delimited String
	 */
	public static String arrayToDelimitedString(final Object[] arr, final String delim) {
		if (ObjectUtils.isEmpty(arr)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				sb.append(delim);
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	/**
	 * Convenience method to return a String array as a CSV String.
	 * E.g. useful for <code>toString()</code> implementations.
	 * @param arr the array to display
	 * @return the delimited String
	 */
	public static String arrayToCommaDelimitedString(final Object[] arr) {
		return arrayToDelimitedString(arr, ",");
	}

	/**
	 * Converts the given String to uppercase.
	 *
	 * @param str the input String (may be <code>null</code>)
	 * @return the String in uppercase, otherwise null
	 */
	public static String toUpperCase(final String str) {
		return str == null ? null : str.toUpperCase();
	}

	/**
	 * Converts the given String to lowercase.
	 *
	 * @param str the input String (may be <code>null</code>)
	 * @return the String in lowercase, otherwise null
	 */
	public static String toLowerCase(final String str) {
		return str == null ? null : str.toLowerCase();
	}

	/**
	 * <p>
	 * Removes leading and trailing whitespace from both ends of this String returning <code>null</code> if the String is empty ("") after the trim or if it is <code>null</code>.
	 *
	 * <pre>
	 * StringUtils.trimToNull(null) = null
	 * StringUtils.trimToNull("") = null
	 * StringUtils.trimToNull(" ") = null
	 * StringUtils.trimToNull("abc") = "abc"
	 * StringUtils.trimToNull(" abc ") = "abc"
	 * </pre>
	 *
	 * @param str the String to be trimmed, may be null
	 * @return the trimmed String, <code>null</code> if only chars &lt;= 32, empty or null String input
	 * @since 1.1
	 */
	public static String trimToNull(final String str) {
		String ts = trimWhitespace(str);
		return !hasText(ts) ? null : ts;
	}

	/**
	 * <p>
	 * Removes leading and trailing whitespace from both ends of this String returning an empty String ("") if the String is empty after the trim or if it is <code>null</code>.
	 *
	 * <pre>
	 * StringUtils.trimToNull(null) = ""
	 * StringUtils.trimToNull("") = ""
	 * StringUtils.trimToNull(" ") = ""
	 * StringUtils.trimToNull("abc") = "abc"
	 * StringUtils.trimToNull(" abc ") = "abc"
	 * </pre>
	 *
	 * @param str the String to be trimmed, may be null
	 * @return the trimmed String, an empty String("") if only chars &lt;= 32, empty or null String input
	 * @since 1.1
	 */
	public static String trimToEmpty(final String str) {
		String ts = trimWhitespace(str);
		return !hasText(ts) ? "" : ts;
	}

	/**
	 * Returns either the passed in String, or if it's blank, the first of the
	 * given default values that is not blank. If all the given Strings are
	 * blank, returns the last of them.
	 *
	 * <ul>
	 * <li><code>StringUtils.defaultIfEmpty(null, "NULL") = "NULL"</code></li>
	 * <li><code>StringUtils.defaultIfEmpty("", "NULL") = "NULL"</code></li>
	 * <li><code>StringUtils.defaultIfEmpty("bat", "NULL") = "bat"</code></li>
	 * <li><code>StringUtils.defaultIfEmpty(null, "", "bat") = "bat"</code></li>
	 * <li><code>StringUtils.defaultIfEmpty(null, null, "") = ""</code></li>
	 * </ul>
	 *
	 * @param str the String to check, may be null
	 * @param defaultStr the default String to return if the input is empty ("")
	 * or null, may be null; note that if this is an expression, it will be
	 * evaluated before this method is called regardless of whether the first
	 * string is empty, so if this evaluation is expensive and performance is
	 * critical, check the first string for emptiness yourself rather than using
	 * this method
	 * @return the passed in String, or the default
	 */
	public static String defaultIfEmpty(final String str, final String... defaultValues) {
		if (hasText(str) || ObjectUtils.isEmpty(defaultValues)) {
			return str;
		}
		for (final String defaultValue : defaultValues) {
			if (hasText(defaultValue)) {
				return defaultValue;
			}
		}
		return defaultValues[defaultValues.length - 1];
	}

	/**
	 * Right pads the presented string with the delim character.
	 *
	 * @param str the string to pad
	 * @param size the size to pad to
	 * @param padChar the padding character
	 * @return the right padded string
	 */
	public static String padRight(final String str, final int size, final char padChar) {
		if (str == null) {
			return null;
		}
		int pads = size - str.length();
		if (pads <= 0) {
			return str;
		}
		return str.concat(padding(pads, padChar));
	}

	/**
	 * Left pads the presented string with the delim character.
	 *
	 * @param str the string to pad
	 * @param size the size to pad to
	 * @param padChar the padding character
	 * @return the left padded string
	 */
	public static String padLeft(final String str, final int size, final char padChar) {
		if (str == null) {
			return null;
		}
		int pads = size - str.length();
		if (pads <= 0) {
			return str;
		}
		return padding(pads, padChar).concat(str);
	}

	private static String padding(final int repeat, final char padChar) throws IndexOutOfBoundsException {
		if (repeat < 0) {
			throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
		}
		final char[] buf = new char[repeat];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = padChar;
		}
		return new String(buf);
	}
	
	/**
	 * Prefixes the given string with the given prefix, if it's not already.
	 * 
	 * @param str the string to prefix (can be blank)
	 * @param prefix the prefix to apply (can be blank to do nothing)
	 * @return <code>null</code> if a <code>null</code> string was given,
	 * otherwise the prefixed string
	 * @since 1.2.0
	 */
	public static String prefix(final String str, final String prefix) {
		if (str == null || prefix == null || str.startsWith(prefix)) {
			return str;
		}
		return prefix + str;
	}
	
	/**
	 * Removes the given prefix from the given string, if it exists
	 * 
	 * @param str the string to modify (can be blank to do nothing)
	 * @param prefix the prefix to remove (can be blank to do nothing)
	 * @return <code>null</code> if a <code>null</code> string was given
	 * @since 1.2.0
	 */
	public static String removePrefix(final String str, final String prefix) {
		if (!hasText(str) || !hasText(prefix) || !str.startsWith(prefix)) {
			return str;
		}
		return str.substring(prefix.length());
	}
	
	/**
	 * Removes the given suffix from the given string, if it exists
	 * 
	 * @param str the string to modify (can be blank to do nothing)
	 * @param suffix the suffix to remove (can be blank to do nothing)
	 * @return <code>null</code> if a <code>null</code> string was given
	 * @since 1.2.0
	 */
	public static String removeSuffix(final String str, final String suffix) {
		if (!hasText(str) || !hasText(suffix) || !str.endsWith(suffix)) {
			return str;
		}
		return str.substring(0, str.length() - suffix.length());
	}

	/**
	 * Appends the given suffix to the given string, if not already present
	 * 
	 * @param str the string to modify (can be blank to do nothing)
	 * @param suffix the suffix to append (can be blank to do nothing)
	 * @return <code>null</code> if a <code>null</code> string was given
	 * @since 1.2.0
	 */
	public static String suffix(final String str, final String suffix) {
		if (str == null || suffix == null || str.endsWith(suffix)) {
			return str;
		}
		return str + suffix;
	}
	
	/**
	 * Indicates whether the two given strings are equal, including case, where
	 * <code>null</code> is (only) equal to <code>null</code>.
	 * 
	 * @param str1 the first string to compare (can be <code>null</code>)
	 * @param str2 the second string to compare (can be <code>null</code>)
	 * @return see above
	 * @since 1.2.0
	 */
	public static boolean equals(final String str1, final String str2) {
		if (str1 == null) {
			return str2 == null;
		}
		return str1.equals(str2);
	}
	
	/**
	 * Indicates whether the given text is blank. More fluent than calling
	 * <code>StringUtils.isBlank(blah)</code>.
	 * 
	 * @param str the text to check (can be blank)
	 * @return the opposite of {@link #hasText(String)}
	 * @since 1.2.0
	 */
	public static boolean isBlank(final String str) {
		return !hasText(str);
	}
	
	/**
	 * Replaces the first occurrence of the given substring in the given string.
	 * <p>
	 * Use in preference to {@link String#replaceFirst(String, String)} when
	 * <code>toReplace</code> is not a regular expression (e.g. some part of a
	 * file path, which on Windows will contain backslashes, which have special
	 * meaning to regexs).
	 * 
	 * @param original the string to modify (can be zero length to do nothing)
	 * @param toReplace the string to replace (can be blank to do nothing)
	 * @param replacement the string to replace it with (can be <code>null</code> to do nothing)
	 * @return the original string, modified as necessary
	 * @since 1.2.0
	 */
	public static String replaceFirst(final String original, final String toReplace, final String replacement) {
		if (!hasLength(original) || !hasLength(toReplace) || replacement == null || !original.contains(toReplace)) {
			return original;
		}
		final int startOfOld = original.indexOf(toReplace);
		final int endOfOld = startOfOld + toReplace.length();
		return arrayToDelimitedString("", original.substring(0, startOfOld), replacement, original.substring(endOfOld));
	}
	
	/**
	 * Returns the substring after the last occurrence of a separator. The separator is not returned.
	 * <p>A null string input will return null. An empty ("") string input will return the empty string.
	 * An empty or null separator will return the empty string if the input string is not null.
	 * <p>If nothing is found, the empty string is returned.
	 * <pre>
	     StringUtils.substringAfterLast(null, *)      = null
	     StringUtils.substringAfterLast("", *)        = ""
	     StringUtils.substringAfterLast(*, "")        = ""
	     StringUtils.substringAfterLast(*, null)      = ""
	     StringUtils.substringAfterLast("abc", "a")   = "bc"
	     StringUtils.substringAfterLast("abcba", "b") = "a"
	     StringUtils.substringAfterLast("abc", "c")   = ""
	     StringUtils.substringAfterLast("a", "a")     = ""
	     StringUtils.substringAfterLast("a", "z")     = ""</pre>
	 *
	 * @param original the String to get a substring from, may be <code>null</code>
	 * @param separator the String to search for, may be <code>null</code>
	 * @return the substring after the last occurrence of the separator,
	 * <code>null</code> if <code>null</code> String input
	 * @since 1.2.0
	 */
	public static String substringAfterLast(final String original, final String separator) {
		if (!hasLength(original)) {
			return original;
		}
		if (!hasLength(separator)) {
			return "";
		}
		final int separatorStart = original.lastIndexOf(separator);
		if (separatorStart == -1) {
			return "";
		}
		return original.substring(separatorStart + separator.length());
	}
	
	/**
	 * Constructor is private to prevent instantiation
	 */
	private StringUtils() {}
}