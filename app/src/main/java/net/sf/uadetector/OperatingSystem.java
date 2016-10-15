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

import net.sf.qualitycheck.Check;

/**
 * {@code OperatingSystem} is an immutable entity that represents the informations about an operating system like Linux,
 * Mac OS X or Windows.
 * 
 * @author André Rouél
 */
public final class OperatingSystem implements ReadableOperatingSystem, Serializable {

	public static final OperatingSystem EMPTY = new OperatingSystem(OperatingSystemFamily.UNKNOWN, "unknown", "unknown.png", "unknown", "",
			"", "", VersionNumber.UNKNOWN);

	/**
	 * Serialization version
	 */
	private static final long serialVersionUID = 1L;

	@Nonnull
	private final OperatingSystemFamily family;

	@Nonnull
	private final String familyName;

	@Nonnull
	private final String icon;

	@Nonnull
	private final String name;

	@Nonnull
	private final String producer;

	@Nonnull
	private final String producerUrl;

	@Nonnull
	private final String url;

	@Nonnull
	private final VersionNumber versionNumber;

	public OperatingSystem(@Nonnull final OperatingSystemFamily family, @Nonnull final String familyName, @Nonnull final String icon,
			@Nonnull final String name, @Nonnull final String producer, @Nonnull final String producerUrl, @Nonnull final String url,
			@Nonnull final VersionNumber versionNumber) {
		Check.notNull(family, "family");
		Check.notNull(familyName, "familyName");
		Check.notNull(icon, "icon");
		Check.notNull(name, "name");
		Check.notNull(producer, "producer");
		Check.notNull(producerUrl, "producerUrl");
		Check.notNull(url, "url");
		Check.notNull(versionNumber, "versionNumber");

		this.family = family;
		this.familyName = familyName;
		this.icon = icon;
		this.name = name;
		this.producer = producer;
		this.producerUrl = producerUrl;
		this.url = url;
		this.versionNumber = versionNumber;
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
		final OperatingSystem other = (OperatingSystem) obj;
		if (family != other.family) {
			return false;
		}
		if (!familyName.equals(other.familyName)) {
			return false;
		}
		if (!icon.equals(other.icon)) {
			return false;
		}
		if (!name.equals(other.name)) {
			return false;
		}
		if (!producer.equals(other.producer)) {
			return false;
		}
		if (!producerUrl.equals(other.producerUrl)) {
			return false;
		}
		if (!url.equals(other.url)) {
			return false;
		}
		if (!versionNumber.equals(other.versionNumber)) {
			return false;
		}
		return true;
	}

	@Override
	public OperatingSystemFamily getFamily() {
		return family;
	}

	@Override
	public String getFamilyName() {
		return familyName;
	}

	@Override
	public String getIcon() {
		return icon;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getProducer() {
		return producer;
	}

	@Override
	public String getProducerUrl() {
		return producerUrl;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public VersionNumber getVersionNumber() {
		return versionNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + family.hashCode();
		result = prime * result + familyName.hashCode();
		result = prime * result + icon.hashCode();
		result = prime * result + name.hashCode();
		result = prime * result + producer.hashCode();
		result = prime * result + producerUrl.hashCode();
		result = prime * result + url.hashCode();
		result = prime * result + versionNumber.hashCode();
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("OperatingSystem [family=");
		builder.append(family);
		builder.append(", familyName=");
		builder.append(familyName);
		builder.append(", icon=");
		builder.append(icon);
		builder.append(", name=");
		builder.append(name);
		builder.append(", producer=");
		builder.append(producer);
		builder.append(", producerUrl=");
		builder.append(producerUrl);
		builder.append(", url=");
		builder.append(url);
		builder.append(", versionNumber=");
		builder.append(versionNumber);
		builder.append("]");
		return builder.toString();
	}

}
