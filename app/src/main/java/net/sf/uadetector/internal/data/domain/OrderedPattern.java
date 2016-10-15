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
package net.sf.uadetector.internal.data.domain;

import java.util.regex.Pattern;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public interface OrderedPattern<T extends OrderedPattern<?>> extends Comparable<T> {

	/**
	 * Gets a compiled representation of a regular expression.
	 * 
	 * @return compiled representation of a regular expression
	 */
	@Nonnull
	Pattern getPattern();

	/**
	 * Gets the position of a browser pattern in a set of patterns.
	 * 
	 * @return position of a browser pattern
	 */
	@Nonnegative
	int getPosition();

}
