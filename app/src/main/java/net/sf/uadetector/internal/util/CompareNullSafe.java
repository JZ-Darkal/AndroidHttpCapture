/*******************************************************************************
 * Copyright 2013 André Rouél
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sf.uadetector.internal.util;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares two references to each other and {@code null} is assumed to be less than a non-{@code null} value. This
 * class provides the first check for null safe comparison.
 * 
 * @author André Rouél
 */
public abstract class CompareNullSafe<T> implements Comparator<T>, Serializable {

	private static final long serialVersionUID = -704997500621650775L;

	/**
	 * Compares to integers.
	 * 
	 * @param a
	 *            first integer
	 * @param b
	 *            second integer
	 * @return {@code -1} if {@code a} is less, {@code 0} if equal, or {@code 1} if greater than {@code b}
	 */
	public static int compareInt(final int a, final int b) {
		return a > b ? 1 : a == b ? 0 : -1;
	}

	/**
	 * Compares two objects null safe to each other.
	 * 
	 * @param o1
	 *            the first reference
	 * @param o2
	 *            the second reference
	 * @return a negative value if o1 < o2, zero if o1 = o2 and a positive value if o1 > o2
	 */
	@Override
	public int compare(final T o1, final T o2) {
		int result = 0;
		if (o1 == null) {
			if (o2 != null) {
				result = -1;
			}
		} else if (o2 == null) {
			result = 1;
		} else {
			result = compareType(o1, o2);
		}
		return result;
	}

	public abstract int compareType(final T o1, final T o2);

}
