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
package org.springframework.shell.test.jediterm.terminal.util;

import java.lang.reflect.Array;
import java.util.BitSet;

// In Java 5, the java.util.Arrays class has no copyOf() members...

/**
 * @author jediterm authors
 */
public class Util {
  @SuppressWarnings("unchecked")
  public static <T> T[] copyOf(T[] original, int newLength) {
    Class<T> type = (Class<T>)original.getClass().getComponentType();
    T[] newArr = (T[])Array.newInstance(type, newLength);

    System.arraycopy(original, 0, newArr, 0, Math.min(original.length, newLength));

    return newArr;
  }

  public static int[] copyOf(int[] original, int newLength) {
    int[] newArr = new int[newLength];

    System.arraycopy(original, 0, newArr, 0, Math.min(original.length, newLength));

    return newArr;
  }

  public static char[] copyOf(char[] original, int newLength) {
    char[] newArr = new char[newLength];

    System.arraycopy(original, 0, newArr, 0, Math.min(original.length, newLength));

    return newArr;
  }

  public static void bitsetCopy(BitSet src, int srcOffset, BitSet dest, int destOffset, int length) {
    for (int i = 0; i < length; i++) {
      dest.set(destOffset + i, src.get(srcOffset + i));
    }
  }

  public static String trimTrailing(String string) {
    int index = string.length() - 1;
    while (index >= 0 && Character.isWhitespace(string.charAt(index))) index--;
    return string.substring(0, index + 1);
  }


  public static boolean containsIgnoreCase(String where, String what) {
    return indexOfIgnoreCase(where, what, 0) >= 0;
  }

  /**
   * Implementation copied from {@link String#indexOf(String, int)} except character comparisons made case insensitive
   */
  public static int indexOfIgnoreCase(String where, String what, int fromIndex) {
    int targetCount = what.length();
    int sourceCount = where.length();

    if (fromIndex >= sourceCount) {
      return targetCount == 0 ? sourceCount : -1;
    }

    if (fromIndex < 0) {
      fromIndex = 0;
    }

    if (targetCount == 0) {
      return fromIndex;
    }

    char first = what.charAt(0);
    int max = sourceCount - targetCount;

    for (int i = fromIndex; i <= max; i++) {
      /* Look for first character. */
      if (!charsEqualIgnoreCase(where.charAt(i), first)) {
        while (++i <= max && !charsEqualIgnoreCase(where.charAt(i), first)) ;
      }

      /* Found first character, now look at the rest of v2 */
      if (i <= max) {
        int j = i + 1;
        int end = j + targetCount - 1;
        for (int k = 1; j < end && charsEqualIgnoreCase(where.charAt(j), what.charAt(k)); j++, k++) ;

        if (j == end) {
          /* Found whole string. */
          return i;
        }
      }
    }

    return -1;
  }

  public static boolean charsEqualIgnoreCase(char a, char b) {
    return a == b || toUpperCase(a) == toUpperCase(b) || toLowerCase(a) == toLowerCase(b);
  }

  private static char toLowerCase(char b) {
    return Character.toLowerCase(b);
  }

  private static char toUpperCase(char a) {
    return Character.toUpperCase(a);
  }
}
