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

@Immutable
public final class BrowserOperatingSystemMapping implements Serializable {

	@NotThreadSafe
	public static final class Builder {

		/**
		 * ID of a browser entry
		 */
		private int browserId = Integer.MIN_VALUE;

		/**
		 * ID of a operating system entry
		 */
		private int operatingSystemId = Integer.MIN_VALUE;

		/**
		 * Build an instance of {@code BrowserOperatingSystemMapping}.
		 * 
		 * @return a new instance of {@code BrowserOperatingSystemMapping}
		 * @throws net.sf.qualitycheck.exception.IllegalNegativeArgumentException
		 *             if one of the needed arguments to build an instance of {@code BrowserOperatingSystemMapping} is
		 *             invalid
		 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
		 *             if one of the needed arguments to build an instance of {@code BrowserOperatingSystemMapping} is
		 *             invalid
		 */
		@Nonnull
		public BrowserOperatingSystemMapping build() {
			return new BrowserOperatingSystemMapping(browserId, operatingSystemId);
		}

		/**
		 * Gets the identification number of a browser entry.
		 * 
		 * @return identification number of a browser entry
		 */
		public int getBrowserId() {
			return browserId;
		}

		/**
		 * Gets the identification number of an operating system entry.
		 * 
		 * @return identification number of an operating system entry
		 */
		public int getOperatingSystemId() {
			return operatingSystemId;
		}

		/**
		 * Sets the identification number of a browser entry.
		 * 
		 * @param browserId
		 *            identification number
		 * @throws net.sf.qualitycheck.exception.IllegalNegativeArgumentException
		 *             if the given number is smaller than {@code 0}
		 */
		@Nonnull
		public Builder setBrowserId(@Nonnegative final int browserId) {
			Check.notNegative(browserId, "browserId");

			this.browserId = browserId;
			return this;
		}

		/**
		 * Sets the identification number of a browser entry via a string.
		 * 
		 * @param browserId
		 *            identification number (as a {@code String)}
		 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
		 *             if the given argument is {@code null}
		 * @throws NumberFormatException
		 *             if the string can not be interpreted as a number
		 * @throws net.sf.qualitycheck.exception.IllegalNegativeArgumentException
		 *             if the interpreted number is smaller than {@code 0}
		 */
		@Nonnull
		public Builder setBrowserId(@Nonnull final String browserId) {
			Check.notNull(browserId, "browserId");

			this.setBrowserId(Integer.parseInt(browserId.trim()));
			return this;
		}

		/**
		 * Sets the identification number of an operating system entry.
		 * 
		 * @param operatingSystemId
		 *            identification number
		 * @throws net.sf.qualitycheck.exception.IllegalNegativeArgumentException
		 *             if the given number is smaller than {@code 0}
		 */
		@Nonnull
		public Builder setOperatingSystemId(@Nonnegative final int operatingSystemId) {
			Check.notNegative(operatingSystemId, "operatingSystemId");

			this.operatingSystemId = operatingSystemId;
			return this;
		}

		/**
		 * Sets the identification number of an operating system entry via a string.
		 * 
		 * @param operatingSystemId
		 *            identification number (as a {@code String)}
		 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
		 *             if the given argument is {@code null}
		 * @throws NumberFormatException
		 *             if the string can not be interpreted as a number
		 * @throws net.sf.qualitycheck.exception.IllegalNegativeArgumentException
		 *             if the interpreted number is smaller than {@code 0}
		 */
		@Nonnull
		public Builder setOperatingSystemId(@Nonnull final String operatingSystemId) {
			Check.notNull(operatingSystemId, "operatingSystemId");

			this.setOperatingSystemId(Integer.parseInt(operatingSystemId.trim()));
			return this;
		}

	}

	private static final long serialVersionUID = 6074931648810031757L;

	/**
	 * ID of a browser entry
	 */
	@Nonnegative
	private final int browserId;

	/**
	 * ID of a operating system entry
	 */
	@Nonnegative
	private final int operatingSystemId;

	/**
	 * Constructs an instance of {@code BrowserOperatingSystemMapping}.
	 * 
	 * @throws net.sf.qualitycheck.exception.IllegalNegativeArgumentException
	 *             if one of the given arguments is smaller than {@code 0}
	 */
	public BrowserOperatingSystemMapping(@Nonnegative final int browserId, @Nonnegative final int operatingSystemId) {
		Check.notNegative(browserId, "browserId");
		Check.notNegative(operatingSystemId, "operatingSystemId");

		this.browserId = browserId;
		this.operatingSystemId = operatingSystemId;
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
		final BrowserOperatingSystemMapping other = (BrowserOperatingSystemMapping) obj;
		if (browserId != other.browserId) {
			return false;
		}
		if (operatingSystemId != other.operatingSystemId) {
			return false;
		}
		return true;
	}

	@Nonnegative
	public int getBrowserId() {
		return browserId;
	}

	@Nonnegative
	public int getOperatingSystemId() {
		return operatingSystemId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + browserId;
		result = prime * result + operatingSystemId;
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("BrowserOperatingSystemMapping [browserId=");
		builder.append(browserId);
		builder.append(", operatingSystemId=");
		builder.append(operatingSystemId);
		builder.append("]");
		return builder.toString();
	}

}
