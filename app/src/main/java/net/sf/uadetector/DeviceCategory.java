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
package net.sf.uadetector;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import net.sf.qualitycheck.Check;

@Immutable
public final class DeviceCategory implements ReadableDeviceCategory, Serializable {

	@NotThreadSafe
	public static final class Builder {

		private Category category;

		private String icon;

		private String infoUrl;

		private String name;

		public Builder() {
			// default constructor
		}

		public Builder(@Nonnull final DeviceCategory deviceCategory) {
			Check.notNull(deviceCategory, "deviceCategory");
			category = Check.notNull(deviceCategory.getCategory(), "deviceCategory.getCategory()");
			icon = Check.notNull(deviceCategory.getIcon(), "deviceCategory.getIcon()");
			infoUrl = Check.notNull(deviceCategory.getInfoUrl(), "deviceCategory.getInfoUrl()");
			name = Check.notNull(deviceCategory.getName(), "deviceCategory.getName()");
		}

		@Nonnull
		public DeviceCategory build() {
			return new DeviceCategory(category, icon, infoUrl, name);
		}

		@Nonnull
		public Builder setCategory(@Nonnull final Category category) {
			this.category = Check.notNull(category, "category");
			return this;
		}

		@Nonnull
		public Builder setIcon(@Nonnull final String icon) {
			this.icon = Check.notNull(icon, "icon");
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

	}

	private static final long serialVersionUID = 1L;

	/**
	 * Represents a not set device category.
	 */
	public static final DeviceCategory EMPTY = new DeviceCategory();

	private static int buildHashCode(@Nonnull final Category category, @Nonnull final String icon, @Nonnull final String infoUrl,
			@Nonnull final String name) {
		final int prime = 31;
		int result = 1;
		result = prime * result + category.hashCode();
		result = prime * result + icon.hashCode();
		result = prime * result + infoUrl.hashCode();
		result = prime * result + name.hashCode();
		return result;
	}

	@Nonnull
	private final Category category;

	@Nonnull
	private final String icon;

	@Nonnull
	private final String infoUrl;

	@Nonnull
	private final String name;

	private final int hash;

	/**
	 * Builds an instance that represents an empty device category.
	 * <p>
	 * <b>Attention</b>: This is only intended to build one instance at runtime to represent value behind the constant
	 * {@link #EMPTY}.
	 */
	private DeviceCategory() {
		category = Category.UNKNOWN;
		icon = "";
		infoUrl = "";
		name = "";
		hash = buildHashCode(category, icon, infoUrl, name);
	}

	public DeviceCategory(@Nonnull final Category category, @Nonnull final String icon, @Nonnull final String infoUrl,
			@Nonnull final String name) {
		this.category = Check.notNull(category, "category");
		this.icon = Check.notNull(icon, "icon");
		this.infoUrl = Check.notNull(infoUrl, "infoUrl");
		this.name = Check.notEmpty(name, "name");
		hash = buildHashCode(category, icon, infoUrl, name);
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
		final DeviceCategory other = (DeviceCategory) obj;
		if (!category.equals(other.category)) {
			return false;
		}
		if (!icon.equals(other.icon)) {
			return false;
		}
		if (!infoUrl.equals(other.infoUrl)) {
			return false;
		}
		if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	@Nonnull
	public Category getCategory() {
		return category;
	}

	@Override
	@Nonnull
	public String getIcon() {
		return icon;
	}

	@Override
	@Nonnull
	public String getInfoUrl() {
		return infoUrl;
	}

	@Override
	@Nonnull
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public String toString() {
		return "DeviceCategory [category=" + category + ", icon=" + icon + ", infoUrl=" + infoUrl + ", name=" + name + "]";
	}

}
