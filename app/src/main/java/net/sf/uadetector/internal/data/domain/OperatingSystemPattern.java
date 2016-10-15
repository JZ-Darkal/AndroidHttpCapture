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

import java.io.Serializable;
import java.util.regex.Pattern;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import net.sf.qualitycheck.Check;
import net.sf.uadetector.internal.util.CompareNullSafe;
import net.sf.uadetector.internal.util.RegularExpressionConverter;

/**
 * The {@code OperatingSystemPattern} class represents the detection information for a specific operating system item.<br>
 * <br>
 * A {@code OperatingSystemPattern} object is immutable, their values cannot be changed after creation.
 * 
 * @author André Rouél
 */
@Immutable
public final class OperatingSystemPattern implements Identifiable, OrderedPattern<OperatingSystemPattern>, Serializable {

	/**
	 * Factory that creates instances of {@code OperatingSystemPattern} via method calls.
	 * 
	 * @author André Rouél
	 */
	@NotThreadSafe
	public static final class Builder {

		/**
		 * Identification number (ID) of an operating system pattern
		 */
		private int id = Integer.MIN_VALUE;

		/**
		 * A compiled representation of a regular expression to detect an operating system
		 */
		private Pattern pattern;

		/**
		 * Position of a {@code OperatingSystemPattern} (only relevant if there are multiple patterns for an operating
		 * system in a {@code SortedSet})
		 */
		private int position = Integer.MIN_VALUE;

		/**
		 * Builds a new instance of {@code OperatingSystemPattern} and returns it.
		 * 
		 * @return a new instance of {@code OperatingSystemPattern}
		 * @throws net.sf.qualitycheck.exception.IllegalNegativeArgumentException
		 *             if one of the needed arguments to build an instance of {@code OperatingSystemPattern} is invalid
		 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
		 *             if one of the needed arguments to build an instance of {@code OperatingSystemPattern} is invalid
		 */
		public OperatingSystemPattern build() {
			return new OperatingSystemPattern(id, pattern, position);
		}

		/**
		 * Sets the identification number of an operating system pattern entry.
		 * 
		 * @param id
		 *            identification number
		 * @return this {@code Builder}, for chaining
		 * @throws net.sf.qualitycheck.exception.IllegalNegativeArgumentException
		 *             if the given integer is smaller than {@code 0}
		 */
		public Builder setId(final int id) {
			Check.notNegative(id, "id");

			this.id = id;
			return this;
		}

		/**
		 * Sets the identification number (ID) of an operating system pattern. The given {@code String} is parsed as a
		 * decimal number.
		 * 
		 * @param id
		 *            ID of an operating system pattern as string
		 * @return this {@code Builder}, for chaining
		 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
		 *             if the given argument is {@code null}
		 * @throws NumberFormatException
		 *             if the given string is not parsable as integer
		 * @throws net.sf.qualitycheck.exception.IllegalNegativeArgumentException
		 *             if the parsed integer is smaller than {@code 0}
		 */
		public Builder setId(@Nonnull final String id) {
			Check.notEmpty(id, "id");

			this.setId(Integer.parseInt(id.trim()));
			return this;
		}

		/**
		 * Sets a regular expression for an operating system pattern.
		 * 
		 * @param pattern
		 *            compiled representation of a regular expression
		 * @return this {@code Builder}, for chaining
		 */
		public Builder setPattern(@Nonnull final Pattern pattern) {
			Check.notNull(pattern, "pattern");

			this.pattern = pattern;
			return this;
		}

		/**
		 * Converts a PERL regular expression in a Java regular expression and sets it in the {@code Builder}.
		 * 
		 * @param regex
		 *            PERL style regular expression to be converted
		 * @return this {@code Builder}, for chaining
		 */
		public Builder setPerlRegularExpression(@Nonnull final String regex) {
			Check.notEmpty(regex, "regex");

			setPattern(RegularExpressionConverter.convertPerlRegexToPattern(regex));
			return this;
		}

		/**
		 * Sets the position of an operating system pattern in a set of patterns.
		 * 
		 * @param position
		 *            position of an operating system pattern
		 * @return this {@code Builder}, for chaining
		 * @throws net.sf.qualitycheck.exception.IllegalNegativeArgumentException
		 *             if the given integer is smaller than {@code 0}
		 */
		public Builder setPosition(@Nonnegative final int position) {
			Check.notNegative(position, "position");

			this.position = position;
			return this;
		}

		/**
		 * Sets the position of an operating system pattern in a set of patterns. The given {@code String} is parsed as
		 * a decimal number.
		 * 
		 * @param position
		 *            position of an operating system pattern as string
		 * @return this {@code Builder}, for chaining
		 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
		 *             if the given argument is {@code null}
		 * @throws NumberFormatException
		 *             if the given string is not parsable as integer
		 * @throws net.sf.qualitycheck.exception.IllegalNegativeArgumentException
		 *             if the parsed integer is smaller than {@code 0}
		 */
		public Builder setPosition(@Nonnull final String position) {
			Check.notEmpty(position, "position");

			this.setPosition(Integer.parseInt(position.trim()));
			return this;
		}

	}

	private static final long serialVersionUID = 1583732916568404647L;

	/**
	 * Identification number (ID) of an operating system pattern
	 */
	@Nonnegative
	private final int id;

	/**
	 * A compiled representation of a regular expression to detect an operating system
	 */
	@Nonnull
	private final Pattern pattern;

	/**
	 * Position of a {@code OperatingSystemPattern} (only relevant if there are multiple patterns for an operating
	 * system in a {@code SortedSet})
	 */
	@Nonnegative
	private final int position;

	public OperatingSystemPattern(@Nonnegative final int id, @Nonnull final Pattern pattern, @Nonnegative final int position) {
		Check.notNegative(id, "id");
		Check.notNull(pattern, "pattern");
		Check.notNegative(position, "position");

		this.id = id;
		this.pattern = pattern;
		this.position = position;
	}

	/**
	 * Compares all attributes of this instance with the given instance of a {@code OperatingSystemPattern}.
	 * 
	 * <p>
	 * This method is <em>consistent with equals</em>.
	 * 
	 * @param other
	 *            another instance of {@code OperatingSystemPattern}
	 * @return negative value if one of the attributes of this instance is less, 0 if equal, or positive value if
	 *         greater than the other one
	 */
	@Override
	public int compareTo(final OperatingSystemPattern other) {
		int result = other == null ? -1 : 0;
		if (result == 0) {
			result = CompareNullSafe.compareInt(getPosition(), other.getPosition());
			if (result == 0) {
				result = CompareNullSafe.compareInt(getId(), other.getId());
			}
			if (result == 0) {
				result = getPattern().pattern().compareTo(other.getPattern().pattern());
			}
			if (result == 0) {
				result = CompareNullSafe.compareInt(getPattern().flags(), other.getPattern().flags());
			}
		}
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final OperatingSystemPattern other = (OperatingSystemPattern) obj;
		if (id != other.id) {
			return false;
		}
		if (position != other.position) {
			return false;
		}
		if (!pattern.pattern().equals(other.pattern.pattern())) {
			return false;
		}
		if (pattern.flags() != other.pattern.flags()) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the identification number (ID) of an operating system pattern.
	 * 
	 * @return identification number (ID) of an operating system pattern
	 */
	@Override
	public int getId() {
		return id;
	}

	@Override
	public Pattern getPattern() {
		return pattern;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + position;
		result = prime * result + pattern.pattern().hashCode();
		result = prime * result + pattern.flags();
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("OperatingSystemPattern [id=");
		builder.append(id);
		builder.append(", pattern=");
		builder.append(pattern);
		builder.append(", position=");
		builder.append(position);
		builder.append("]");
		return builder.toString();
	}

}
