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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import net.sf.qualitycheck.Check;

/**
 * The {@code BrowserType} class represents the type of a browser.<br>
 * <br>
 * A {@code BrowserType} object is immutable, their values cannot be changed after creation.
 * 
 * @author André Rouél
 */
@Immutable
public final class BrowserType implements Identifiable, Serializable {

	@NotThreadSafe
	public static final class Builder {

		/**
		 * Identification number (ID) of a browser type entry
		 */
		private int id = Integer.MIN_VALUE;

		/**
		 * Name of a browser type entry
		 */
		private String name;

		/**
		 * Builds a new instance of {@code BrowserType} and returns it.
		 * 
		 * @return a new instance of {@code BrowserType}
		 * @throws net.sf.qualitycheck.exception.IllegalNegativeArgumentException
		 *             if one of the needed arguments to build an instance of {@code BrowserType} is invalid
		 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
		 *             if one of the needed arguments to build an instance of {@code BrowserType} is invalid
		 */
		@Nonnull
		public BrowserType build() {
			return new BrowserType(id, name);
		}

		/**
		 * Sets the identification number (ID).
		 * 
		 * @param id
		 *            ID of browser type
		 */
		@Nonnull
		public Builder setId(@Nonnegative final int id) {
			Check.notNegative(id, "id");

			this.id = id;
			return this;
		}

		/**
		 * Sets the identification number via a string.<br>
		 * <br>
		 * An opening and closing Square brackets at the end of a string will be filtered. If the string can not be
		 * parsed as a long, a {@code NumberFormatException} will be thrown.
		 * 
		 * @param id
		 *            ID of browser type
		 */
		@Nonnull
		public Builder setId(@Nonnull final String id) {
			Check.notNull(id, "id");

			this.setId(Integer.parseInt(id.trim()));
			return this;
		}

		/**
		 * Sets the name.
		 * 
		 * @param name
		 *            name of the browser type
		 */
		@Nonnull
		public Builder setName(@Nonnull final String name) {
			Check.notNull(name, "name");

			this.name = name;
			return this;
		}

	}

	private static final long serialVersionUID = 2643535063309729806L;

	@Nonnegative
	private final int id;

	@Nonnull
	private final String name;

	public BrowserType(@Nonnegative final int id, @Nonnull final String name) {
		Check.notNegative(id, "id");
		Check.notNull(name, "name");

		this.id = id;
		this.name = name;
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
		final BrowserType other = (BrowserType) obj;
		if (id != other.id) {
			return false;
		}
		if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the identification number (ID) of a browser type.
	 * 
	 * @return identification number (ID) of a browser type
	 */
	@Override
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + name.hashCode();
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("BrowserType [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}

}
