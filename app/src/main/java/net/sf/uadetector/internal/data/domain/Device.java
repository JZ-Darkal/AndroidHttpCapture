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
package net.sf.uadetector.internal.data.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import net.sf.qualitycheck.Check;
import net.sf.uadetector.ReadableDeviceCategory.Category;

@Immutable
public final class Device implements Identifiable, Serializable {

	@NotThreadSafe
	public static final class Builder {

		private static final String EMPTY = "";

		@Nonnull
		private String icon = EMPTY;

		private int id = Integer.MIN_VALUE;

		@Nonnull
		private String infoUrl = EMPTY;

		private String name;

		@Nonnull
		private SortedSet<DevicePattern> patterns = new TreeSet<DevicePattern>();

		public Builder() {
			// default constructor
		}

		/**
		 * Creates a new instance of a builder with the data of the passed builder.
		 * 
		 * @param builder
		 *            builder containing the data to be copied
		 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
		 *             if the given argument is {@code null}
		 */
		private Builder(@Nonnull final Builder builder) {
			Check.notNull(builder, "builder");

			icon = builder.icon;
			id = builder.id;
			infoUrl = builder.infoUrl;
			name = builder.name;
		}

		public Builder(@Nonnull final Device device) {
			Check.notNull(device, "device");
			icon = Check.notNull(device.getIcon(), "device.getIcon()");
			id = Check.notNegative(device.getId(), "device.getId()");
			infoUrl = Check.notNull(device.getInfoUrl(), "device.getInfoUrl()");
			name = Check.notNull(device.getName(), "device.getName()");
			patterns = new TreeSet<DevicePattern>(Check.notNull(device.getPatterns(), "device.getPatterns()"));
		}

		@Nonnull
		public Device build() {
			return new Device(name, id, Category.evaluate(name), icon, infoUrl, patterns);
		}

		/**
		 * Creates a copy (with all its data) of the current builder.
		 * 
		 * @return a new instance of the current builder, never {@code null}
		 */
		@Nonnull
		public Builder copy() {
			return new Builder(this);
		}

		public String getIcon() {
			return icon;
		}

		public int getId() {
			return id;
		}

		public String getInfoUrl() {
			return infoUrl;
		}

		public String getName() {
			return name;
		}

		public SortedSet<DevicePattern> getPatterns() {
			return patterns;
		}

		@Nonnull
		public Builder setIcon(@Nonnull final String icon) {
			this.icon = Check.notNull(icon, "icon");
			return this;
		}

		@Nonnull
		public Builder setId(@Nonnegative final int id) {
			this.id = Check.notNegative(id, "id");
			return this;
		}

		@Nonnull
		public Builder setId(@Nonnull final String id) {
			setId(Integer.parseInt(Check.notEmpty(id.replace("\n","").replace("\t",""), "id")));
			return this;
		}

		@Nonnull
		public Builder setInfoUrl(@Nonnull final String infoUrl) {
			this.infoUrl = Check.notNull(infoUrl, "infoUrl");
			return this;
		}

		@Nonnull
		public Builder setName(@Nonnull final String name) {
			this.name = Check.notNull(name, "name");
			return this;
		}

		@Nonnull
		public Builder setPatterns(@Nonnull final SortedSet<DevicePattern> patterns) {
			this.patterns = new TreeSet<DevicePattern>(Check.notNull(patterns, "patterns"));
			return this;
		}

	}

	private static final long serialVersionUID = 1L;

	private static int buildHashCode(@Nonnull final Category category, @Nonnull final String icon, @Nonnegative final int id,
			@Nonnull final String infoUrl, @Nonnull final String name, @Nonnull final SortedSet<DevicePattern> patterns) {
		final int prime = 31;
		int result = 1;
		result = prime * result + category.hashCode();
		result = prime * result + icon.hashCode();
		result = prime * result + id;
		result = prime * result + infoUrl.hashCode();
		result = prime * result + name.hashCode();
		result = prime * result + patterns.hashCode();
		return result;
	}

	private final int hash;

	@Nonnull
	private final String icon;

	@Nonnull
	private final Category category;

	@Nonnegative
	private final int id;

	@Nonnull
	private final String infoUrl;

	@Nonnull
	private final String name;

	@Nonnull
	private final SortedSet<DevicePattern> patterns;

	public Device(@Nonnull final String name, @Nonnegative final int id, @Nonnull final Category category, @Nonnull final String icon,
			@Nonnull final String infoUrl, @Nonnull final SortedSet<DevicePattern> patterns) {
		this.category = category;
		this.icon = Check.notNull(icon, "icon");
		this.id = Check.notNegative(id, "id");
		this.infoUrl = Check.notNull(infoUrl, "infoUrl");
		this.name = Check.notNull(name, "name");
		this.patterns = Collections.unmodifiableSortedSet(new TreeSet<DevicePattern>(Check.notNull(patterns, "patterns")));
		hash = buildHashCode(category, icon, id, infoUrl, name, patterns);
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
		final Device other = (Device) obj;
		if (category != other.category) {
			return false;
		}
		if (!icon.equals(other.icon)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (!infoUrl.equals(other.infoUrl)) {
			return false;
		}
		if (!name.equals(other.name)) {
			return false;
		}
		if (!patterns.equals(other.patterns)) {
			return false;
		}
		return true;
	}

	@Nonnull
	public Category getCategory() {
		return category;
	}

	@Nonnull
	public String getIcon() {
		return icon;
	}

	@Override
	@Nonnegative
	public int getId() {
		return id;
	}

	@Nonnull
	public String getInfoUrl() {
		return infoUrl;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	@Nonnull
	public SortedSet<DevicePattern> getPatterns() {
		return patterns;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public String toString() {
		return "Device [icon=" + icon + ", id=" + id + ", infoUrl=" + infoUrl + ", name=" + name + ", patterns=" + patterns + "]";
	}

}
