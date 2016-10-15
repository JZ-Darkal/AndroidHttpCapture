/*******************************************************************************
 * Copyright 2012 André Rouél
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
package net.sf.uadetector.internal.data;

import net.sf.uadetector.internal.util.CompareNullSafe;

public final class OrderedPatternComparator<T extends Comparable<T>> extends CompareNullSafe<T> {

	private static final long serialVersionUID = -3561941361756671092L;

	@Override
	public int compareType(final T o1, final T o2) {
		return o1.compareTo(o2);
	}

}
