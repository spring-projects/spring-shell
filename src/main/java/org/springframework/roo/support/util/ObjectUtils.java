package org.springframework.roo.support.util;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Miscellaneous object utility methods. Mainly for internal use within the
 * framework; consider Jakarta's Commons Lang for a more comprehensive suite
 * of object utilities.
 *
 * @author Juergen Hoeller
 * @author Keith Donald
 * @author Rod Johnson
 * @author Rob Harrop
 * @author Alex Ruiz
 * @see org.apache.commons.lang.ObjectUtils
 */
public final class ObjectUtils {

	// Constants
	private static final int INITIAL_HASH = 7;
	private static final int MULTIPLIER = 31;

	private static final String EMPTY_STRING = "";
	private static final String NULL_STRING = "null";
	private static final String ARRAY_START = "{";
	private static final String ARRAY_END = "}";
	private static final String EMPTY_ARRAY = ARRAY_START + ARRAY_END;
	private static final String ARRAY_ELEMENT_SEPARATOR = ", ";

	/**
	 * Return whether the given throwable is a checked exception:
	 * that is, neither a RuntimeException nor an Error.
	 * 
	 * @param ex the throwable to check
	 * @return whether the throwable is a checked exception
	 * @see java.lang.Exception
	 * @see java.lang.RuntimeException
	 * @see java.lang.Error
	 */
	public static boolean isCheckedException(final Throwable ex) {
		return !(ex instanceof RuntimeException || ex instanceof Error);
	}

	/**
	 * Check whether the given exception is compatible with the exceptions
	 * declared in a throws clause.
	 * 
	 * @param ex the exception to checked
	 * @param declaredExceptions the exceptions declared in the throws clause
	 * @return whether the given exception is compatible
	 */
	public static boolean isCompatibleWithThrowsClause(final Throwable ex, final Class<?>... declaredExceptions) {
		if (!isCheckedException(ex)) {
			return true;
		}
		if (declaredExceptions != null) {
			int i = 0;
			while (i < declaredExceptions.length) {
				if (declaredExceptions[i].isAssignableFrom(ex.getClass())) {
					return true;
				}
				i++;
			}
		}
		return false;
	}

	/**
	 * Return whether the given array is empty: that is, <code>null</code>
	 * or of zero length.
	 * 
	 * @param array the array to check
	 * @return whether the given array is empty
	 */
	public static boolean isEmpty(final Object[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * Check whether the given array contains the given element.
	 * 
	 * @param array the array to check (may be <code>null</code>,
	 * in which case the return value will always be <code>false</code>)
	 * @param element the element to check for
	 * @return whether the element has been found in the given array
	 */
	public static boolean containsElement(final Object[] array, final Object element) {
		if (array == null) {
			return false;
		}
		for (Object arrayEle : array) {
			if (nullSafeEquals(arrayEle, element)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Append the given Object to the given array, returning a new array
	 * consisting of the input array contents plus the given Object.
	 * 
	 * @param array the array to append to (can be <code>null</code>)
	 * @param obj the Object to append
	 * @return the new array (of the same component type; never <code>null</code>)
	 */
	public static Object[] addObjectToArray(final Object[] array, final Object obj) {
		Class<?> compType = Object.class;
		if (array != null) {
			compType = array.getClass().getComponentType();
		}
		else if (obj != null) {
			compType = obj.getClass();
		}
		int newArrLength = (array != null ? array.length + 1 : 1);
		Object[] newArr = (Object[]) Array.newInstance(compType, newArrLength);
		if (array != null) {
			System.arraycopy(array, 0, newArr, 0, array.length);
		}
		newArr[newArr.length - 1] = obj;
		return newArr;
	}

	/**
	 * Convert the given array (which may be a primitive array) to an
	 * object array (if necessary of primitive wrapper objects).
	 * <p>A <code>null</code> source value will be converted to an
	 * empty Object array.
	 * 
	 * @param source the (potentially primitive) array
	 * @return the corresponding object array (never <code>null</code>)
	 * @throws IllegalArgumentException if the parameter is not an array
	 */
	public static Object[] toObjectArray(final Object source) {
		if (source instanceof Object[]) {
			return (Object[]) source;
		}
		if (source == null) {
			return new Object[0];
		}
		if (!source.getClass().isArray()) {
			throw new IllegalArgumentException("Source is not an array: " + source);
		}
		int length = Array.getLength(source);
		if (length == 0) {
			return new Object[0];
		}
		Class<?> wrapperType = Array.get(source, 0).getClass();
		Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
		for (int i = 0; i < length; i++) {
			newArray[i] = Array.get(source, i);
		}
		return newArray;
	}

	//---------------------------------------------------------------------
	// Convenience methods for content-based equality/hash-code handling
	//---------------------------------------------------------------------

	/**
	 * Determine if the given objects are equal, returning <code>true</code>
	 * if both are <code>null</code> or <code>false</code> if only one is
	 * <code>null</code>.
	 * <p>Compares arrays with <code>Arrays.equals</code>, performing an equality
	 * check based on the array elements rather than the array reference.
	 * 
	 * @param o1 first Object to compare
	 * @param o2 second Object to compare
	 * @return whether the given objects are equal
	 * @see java.util.Arrays#equals
	 */
	public static boolean nullSafeEquals(final Object o1, final Object o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 == null || o2 == null) {
			return false;
		}
		if (o1.equals(o2)) {
			return true;
		}
		if (o1.getClass().isArray() && o2.getClass().isArray()) {
			if (o1 instanceof Object[] && o2 instanceof Object[]) {
				return Arrays.equals((Object[]) o1, (Object[]) o2);
			}
			if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
				return Arrays.equals((boolean[]) o1, (boolean[]) o2);
			}
			if (o1 instanceof byte[] && o2 instanceof byte[]) {
				return Arrays.equals((byte[]) o1, (byte[]) o2);
			}
			if (o1 instanceof char[] && o2 instanceof char[]) {
				return Arrays.equals((char[]) o1, (char[]) o2);
			}
			if (o1 instanceof double[] && o2 instanceof double[]) {
				return Arrays.equals((double[]) o1, (double[]) o2);
			}
			if (o1 instanceof float[] && o2 instanceof float[]) {
				return Arrays.equals((float[]) o1, (float[]) o2);
			}
			if (o1 instanceof int[] && o2 instanceof int[]) {
				return Arrays.equals((int[]) o1, (int[]) o2);
			}
			if (o1 instanceof long[] && o2 instanceof long[]) {
				return Arrays.equals((long[]) o1, (long[]) o2);
			}
			if (o1 instanceof short[] && o2 instanceof short[]) {
				return Arrays.equals((short[]) o1, (short[]) o2);
			}
		}
		return false;
	}

	/**
	 * Return as hash code for the given object; typically the value of
	 * <code>{@link Object#hashCode()}</code>. If the object is an array,
	 * this method will delegate to any of the <code>nullSafeHashCode</code>
	 * methods for arrays in this class. If the object is <code>null</code>,
	 * this method returns 0.
	 * 
	 * @see #nullSafeHashCode(Object[])
	 * @see #nullSafeHashCode(boolean[])
	 * @see #nullSafeHashCode(byte[])
	 * @see #nullSafeHashCode(char[])
	 * @see #nullSafeHashCode(double[])
	 * @see #nullSafeHashCode(float[])
	 * @see #nullSafeHashCode(int[])
	 * @see #nullSafeHashCode(long[])
	 * @see #nullSafeHashCode(short[])
	 */
	public static int nullSafeHashCode(final Object obj) {
		if (obj == null) {
			return 0;
		}
		if (obj.getClass().isArray()) {
			if (obj instanceof Object[]) {
				return nullSafeHashCode((Object[]) obj);
			}
			if (obj instanceof boolean[]) {
				return nullSafeHashCode((boolean[]) obj);
			}
			if (obj instanceof byte[]) {
				return nullSafeHashCode((byte[]) obj);
			}
			if (obj instanceof char[]) {
				return nullSafeHashCode((char[]) obj);
			}
			if (obj instanceof double[]) {
				return nullSafeHashCode((double[]) obj);
			}
			if (obj instanceof float[]) {
				return nullSafeHashCode((float[]) obj);
			}
			if (obj instanceof int[]) {
				return nullSafeHashCode((int[]) obj);
			}
			if (obj instanceof long[]) {
				return nullSafeHashCode((long[]) obj);
			}
			if (obj instanceof short[]) {
				return nullSafeHashCode((short[]) obj);
			}
		}
		return obj.hashCode();
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * 
	 * @param array the array from whose elements to calculate the hash code (can be <code>null</code>)
	 * @return 0 if the array is <code>null</code>
	 */
	public static int nullSafeHashCode(final Object... array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		for (final Object element : array) {
			hash = MULTIPLIER * hash + nullSafeHashCode(element);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * 
	 * @param array can be <code>null</code>
	 * @return 0 if <code>array</code> is <code>null</code>
	 */
	public static int nullSafeHashCode(final boolean... array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		int arraySize = array.length;
		for (int i = 0; i < arraySize; i++) {
			hash = MULTIPLIER * hash + hashCode(array[i]);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * 
	 * @param array can be <code>null</code>
	 * @return 0 if <code>array</code> is <code>null</code>
	 */
	public static int nullSafeHashCode(final byte... array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		int arraySize = array.length;
		for (int i = 0; i < arraySize; i++) {
			hash = MULTIPLIER * hash + array[i];
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * 
	 * @param array can be <code>null</code>
	 * @return 0 if <code>array</code> is <code>null</code>
	 */
	public static int nullSafeHashCode(final char... array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		int arraySize = array.length;
		for (int i = 0; i < arraySize; i++) {
			hash = MULTIPLIER * hash + array[i];
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * 
	 * @param array can be <code>null</code>
	 * @return 0 if <code>array</code> is <code>null</code>
	 */
	public static int nullSafeHashCode(final double... array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		int arraySize = array.length;
		for (int i = 0; i < arraySize; i++) {
			hash = MULTIPLIER * hash + hashCode(array[i]);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * 
	 * @param array can be <code>null</code>
	 * @return 0 if <code>array</code> is <code>null</code>
	 */
	public static int nullSafeHashCode(final float... array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		int arraySize = array.length;
		for (int i = 0; i < arraySize; i++) {
			hash = MULTIPLIER * hash + hashCode(array[i]);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * 
	 * @param array can be <code>null</code>
	 * @return 0 if <code>array</code> is <code>null</code>
	 */
	public static int nullSafeHashCode(final int... array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		int arraySize = array.length;
		for (int i = 0; i < arraySize; i++) {
			hash = MULTIPLIER * hash + array[i];
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * 
	 * @param array can be <code>null</code>
	 * @return 0 if <code>array</code> is <code>null</code>
	 */
	public static int nullSafeHashCode(final long... array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		int arraySize = array.length;
		for (int i = 0; i < arraySize; i++) {
			hash = MULTIPLIER * hash + hashCode(array[i]);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array.
	 * 
	 * @param array can be <code>null</code>
	 * @return 0 if <code>array</code> is <code>null</code>
	 */
	public static int nullSafeHashCode(final short... array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		int arraySize = array.length;
		for (int i = 0; i < arraySize; i++) {
			hash = MULTIPLIER * hash + array[i];
		}
		return hash;
	}

	/**
	 * Returns the hash code of the given boolean value.
	 * 
	 * @param bool the boolean for which to return the hash code
	 * @return see {@link Boolean#hashCode()}
	 */
	public static int hashCode(final boolean bool) {
		return Boolean.valueOf(bool).hashCode();
	}

	/**
	 * Return the same value as <code>{@link Double#hashCode()}</code>.
	 * 
	 * @see Double#hashCode()
	 */
	public static int hashCode(final double dbl) {
		long bits = Double.doubleToLongBits(dbl);
		return hashCode(bits);
	}

	/**
	 * Return the same value as <code>{@link Float#hashCode()}</code>.
	 * 
	 * @see Float#hashCode()
	 */
	public static int hashCode(final float flt) {
		return Float.floatToIntBits(flt);
	}

	/**
	 * Return the same value as <code>{@link Long#hashCode()}</code>.
	 * 
	 * @see Long#hashCode()
	 */
	public static int hashCode(final long lng) {
		return (int) (lng ^ (lng >>> 32));
	}

	//---------------------------------------------------------------------
	// Convenience methods for toString output
	//---------------------------------------------------------------------

	/**
	 * Return a String representation of an object's overall identity.
	 * 
	 * @param obj the object (may be <code>null</code>)
	 * @return the object's identity as String representation,
	 * or an empty String if the object was <code>null</code>
	 */
	public static String identityToString(final Object obj) {
		if (obj == null) {
			return EMPTY_STRING;
		}
		return obj.getClass().getName() + "@" + getIdentityHexString(obj);
	}

	/**
	 * Return a hex String form of an object's identity hash code.
	 * 
	 * @param obj the object
	 * @return the object's identity code in hex notation
	 */
	public static String getIdentityHexString(final Object obj) {
		return Integer.toHexString(System.identityHashCode(obj));
	}

	/**
	 * Return a content-based String representation if <code>obj</code> is
	 * not <code>null</code>; otherwise returns an empty String.
	 * <p>Differs from {@link #nullSafeToString(Object)} in that it returns
	 * an empty String rather than "null" for a <code>null</code> value.
	 * 
	 * @param obj the object to build a display String for
	 * @return a display String representation of <code>obj</code>
	 * @see #nullSafeToString(Object)
	 */
	public static String getDisplayString(final Object obj) {
		if (obj == null) {
			return EMPTY_STRING;
		}
		return nullSafeToString(obj);
	}

	/**
	 * Determine the class name for the given object.
	 * <p>Returns <code>"null"</code> if <code>obj</code> is <code>null</code>.
	 * 
	 * @param obj the object to introspect (may be <code>null</code>)
	 * @return the corresponding class name
	 */
	public static String nullSafeClassName(final Object obj) {
		return (obj != null ? obj.getClass().getName() : NULL_STRING);
	}

	/**
	 * Return a String representation of the specified Object.
	 * <p>Builds a String representation of the contents in case of an array.
	 * Returns <code>"null"</code> if <code>obj</code> is <code>null</code>.
	 * 
	 * @param obj the object to build a String representation for
	 * @return a String representation of <code>obj</code>
	 */
	public static String nullSafeToString(final Object obj) {
		if (obj == null) {
			return NULL_STRING;
		}
		if (obj instanceof String) {
			return (String) obj;
		}
		if (obj instanceof Object[]) {
			return nullSafeToString((Object[]) obj);
		}
		if (obj instanceof boolean[]) {
			return nullSafeToString((boolean[]) obj);
		}
		if (obj instanceof byte[]) {
			return nullSafeToString((byte[]) obj);
		}
		if (obj instanceof char[]) {
			return nullSafeToString((char[]) obj);
		}
		if (obj instanceof double[]) {
			return nullSafeToString((double[]) obj);
		}
		if (obj instanceof float[]) {
			return nullSafeToString((float[]) obj);
		}
		if (obj instanceof int[]) {
			return nullSafeToString((int[]) obj);
		}
		if (obj instanceof long[]) {
			return nullSafeToString((long[]) obj);
		}
		if (obj instanceof short[]) {
			return nullSafeToString((short[]) obj);
		}
		String str = obj.toString();
		return (str != null ? str : EMPTY_STRING);
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are separated
	 * by the characters <code>", "</code> (a comma followed by a space). Returns
	 * <code>"null"</code> if <code>array</code> is <code>null</code>.
	 * 
	 * @param array the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(final Object... array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(String.valueOf(array[i]));
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are separated
	 * by the characters <code>", "</code> (a comma followed by a space). Returns
	 * <code>"null"</code> if <code>array</code> is <code>null</code>.
	 * 
	 * @param array the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(final boolean... array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}

			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are separated
	 * by the characters <code>", "</code> (a comma followed by a space). Returns
	 * <code>"null"</code> if <code>array</code> is <code>null</code>.
	 * 
	 * @param array the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(final byte... array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are separated
	 * by the characters <code>", "</code> (a comma followed by a space). Returns
	 * <code>"null"</code> if <code>array</code> is <code>null</code>.
	 * 
	 * @param array the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(final char... array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append("'").append(array[i]).append("'");
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are separated
	 * by the characters <code>", "</code> (a comma followed by a space). Returns
	 * <code>"null"</code> if <code>array</code> is <code>null</code>.
	 * 
	 * @param array the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(final double... array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}

			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are separated
	 * by the characters <code>", "</code> (a comma followed by a space). Returns
	 * <code>"null"</code> if <code>array</code> is <code>null</code>.
	 * 
	 * @param array the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(final float... array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}

			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are separated
	 * by the characters <code>", "</code> (a comma followed by a space). Returns
	 * <code>"null"</code> if <code>array</code> is <code>null</code>.
	 * 
	 * @param array the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(final int... array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are separated
	 * by the characters <code>", "</code> (a comma followed by a space). Returns
	 * <code>"null"</code> if <code>array</code> is <code>null</code>.
	 * 
	 * @param array the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(final long... array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are separated
	 * by the characters <code>", "</code> (a comma followed by a space). Returns
	 * <code>"null"</code> if <code>array</code> is <code>null</code>.
	 * 
	 * @param array the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(final short... array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			}
			else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Compares the two given objects, with <code>null</code> equivalent to
	 * <code>null</code> and <code>null</code> "less than" any
	 * non-<code>null</code> instance. Two non-<code>null</code> instances are
	 * compared using the first one's {@link Comparable#compareTo(Object)}
	 * method.
	 *
	 * @param <T> the type of objects being compared
	 * @param one the first object being compared (can be <code>null</code>)
	 * @param other the second object being compared (can be <code>null</code>)
	 * @return see {@link Comparable#compareTo(Object)}
	 * @since 1.2.0
	 */
	public static <T> int nullSafeComparison(final Comparable<T> one, final T other) {
		if (one == null) {
			if (other == null) {
				return 0;
			}
			// First is null, second is not
			return -1;
		}
		// If we get here, the first object is non-null
		if (other == null) {
			return 1;
		}
		return one.compareTo(other);
	}
	
	/**
	 * Returns the String representation of the given object, or if it's
	 * <code>null</code>, the given default string
	 * 
	 * @param object the object to represent as a String (can be <code>null</code>)
	 * @param defaultValue the value to return if the given object is <code>null</code> (can itself be blank)
	 * @return see above
	 * @since 1.2.0
	 */
	public static String toString(final Object object, final String defaultValue) {
		if (object == null) {
			return defaultValue;
		}
		return object.toString();
	}
	
	/**
	 * Returns the given object if not <code>null</code>, otherwise the given
	 * default value
	 * 
	 * @param <T> the type of object being defaulted
	 * @param object the object to check (can be <code>null</code>)
	 * @param defaultValue the default value (can be <code>null</code>)
	 * @return <code>null</code> iff both values are <code>null</code>
	 * @since 1.2.0
	 */
	public static <T> T defaultIfNull(final T object, final T defaultValue) {
		if (object == null) {
			return defaultValue;
		}
		return object;
	}
	
	/**
	 * Constructor is private to prevent instantiation
	 */
	private ObjectUtils() {}
}
