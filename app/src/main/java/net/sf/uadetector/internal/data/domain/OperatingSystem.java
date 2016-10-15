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
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import net.sf.qualitycheck.Check;
import net.sf.uadetector.OperatingSystemFamily;
import net.sf.uadetector.UserAgent;
import net.sf.uadetector.VersionNumber;

@Immutable
public final class OperatingSystem implements Identifiable, Serializable {

	@NotThreadSafe
	public static final class Builder {

		@Nonnull
		private String family = "";

		@Nonnull
		private String icon = "";

		private int id = Integer.MIN_VALUE;

		@Nonnull
		private String infoUrl = "";

		@Nonnull
		private String name = "";

		@Nonnull
		private SortedSet<OperatingSystemPattern> patterns = new TreeSet<OperatingSystemPattern>();

		@Nonnull
		private String producer = "";

		@Nonnull
		private String producerUrl = "";

		@Nonnull
		private String url = "";

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
		protected Builder(@Nonnull final Builder builder) {
			Check.notNull(builder, "builder");

			family = builder.family;
			icon = builder.icon;
			id = builder.id;
			infoUrl = builder.infoUrl;
			name = builder.name;
			patterns.addAll(builder.patterns);
			producer = builder.producer;
			producerUrl = builder.producerUrl;
			url = builder.url;
		}

		public Builder(@Nonnull final OperatingSystem operatingSystem) {
			Check.notNull(operatingSystem, "operatingSystem");
			id = Check.notNegative(operatingSystem.getId(), "operatingSystem.getId()");
			name = Check.notNull(operatingSystem.getName(), "operatingSystem.getName()");
			family = Check.notNull(operatingSystem.getFamily(), "operatingSystem.getFamily()");
			infoUrl = Check.notNull(operatingSystem.getInfoUrl(), "operatingSystem.getInfoUrl()");
			patterns = new TreeSet<OperatingSystemPattern>(Check.notNull(operatingSystem.getPatterns(), "operatingSystem.getPatterns()"));
			producer = Check.notNull(operatingSystem.getProducer(), "operatingSystem.getProducer()");
			producerUrl = Check.notNull(operatingSystem.getProducerUrl(), "operatingSystem.getProducerUrl()");
			url = Check.notNull(operatingSystem.getUrl(), "operatingSystem.getUrl()");
			icon = Check.notNull(operatingSystem.getIcon(), "operatingSystem.getIcon()");
		}

		@Nonnull
		public Builder addPatterns(@Nonnull final Set<OperatingSystemPattern> patterns) {
			Check.notNull(patterns, "patterns");

			this.patterns.addAll(patterns);
			return this;
		}

		@Nonnull
		public OperatingSystem build() {
			return new OperatingSystem(id, name, family, infoUrl, patterns, producer, producerUrl, url, icon);
		}

		/**
		 * Creates a copy (with all its data) of the current builder.
		 * 
		 * @return a new instance of the current builder, never {@code null}
		 */
		@Nonnull
		public OperatingSystem.Builder copy() {
			return new Builder(this);
		}

		@Nonnull
		public String getFamily() {
			return family;
		}

		@Nonnull
		public String getIcon() {
			return icon;
		}

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
		public SortedSet<OperatingSystemPattern> getPatterns() {
			return patterns;
		}

		@Nonnull
		public String getProducer() {
			return producer;
		}

		@Nonnull
		public String getProducerUrl() {
			return producerUrl;
		}

		@Nonnull
		public String getUrl() {
			return url;
		}

		@Nonnull
		public Builder setFamily(@Nonnull final String family) {
			this.family = Check.notNull(family, "family");
			return this;
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
			Check.notEmpty(id, "id");

			this.setId(Integer.parseInt(id.trim()));
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
		public Builder setPatterns(@Nonnull final SortedSet<OperatingSystemPattern> patterns) {
			this.patterns = new TreeSet<OperatingSystemPattern>(Check.notNull(patterns, "patterns"));
			return this;
		}

		@Nonnull
		public Builder setProducer(@Nonnull final String producer) {
			this.producer = Check.notNull(producer, "producer");
			return this;
		}

		@Nonnull
		public Builder setProducerUrl(@Nonnull final String producerUrl) {
			this.producerUrl = Check.notNull(producerUrl, "producerUrl");
			return this;
		}

		@Nonnull
		public Builder setUrl(@Nonnull final String url) {
			this.url = Check.notNull(url, "url");
			return this;
		}

	}

	private static final long serialVersionUID = -5330180544816352323L;

	private static int buildHashCode(@Nonnegative final int id, @Nonnull final String name, @Nonnull final String family,
			@Nonnull final String infoUrl, @Nonnull final SortedSet<OperatingSystemPattern> patterns, @Nonnull final String producer,
			@Nonnull final String producerUrl, @Nonnull final String url, @Nonnull final String icon) {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + name.hashCode();
		result = prime * result + family.hashCode();
		result = prime * result + infoUrl.hashCode();
		result = prime * result + patterns.hashCode();
		result = prime * result + producer.hashCode();
		result = prime * result + producerUrl.hashCode();
		result = prime * result + url.hashCode();
		result = prime * result + icon.hashCode();
		return result;
	}

	@Nonnull
	private final String family;

	private final int hash;

	@Nonnull
	private final String icon;

	@Nonnegative
	private final int id;

	@Nonnull
	private final String infoUrl;

	@Nonnull
	private final String name;

	@Nonnull
	private final SortedSet<OperatingSystemPattern> patterns;

	@Nonnull
	private final String producer;

	@Nonnull
	private final String producerUrl;

	@Nonnull
	private final String url;

	public OperatingSystem(@Nonnegative final int id, @Nonnull final String name, @Nonnull final String family,
			@Nonnull final String infoUrl, @Nonnull final SortedSet<OperatingSystemPattern> patterns, @Nonnull final String producer,
			@Nonnull final String producerUrl, @Nonnull final String url, @Nonnull final String icon) {
		this.id = Check.notNegative(id, "id");
		this.name = Check.notNull(name, "name");
		this.family = Check.notNull(family, "family");
		this.infoUrl = Check.notNull(infoUrl, "infoUrl");
		this.patterns = Collections.unmodifiableSortedSet(new TreeSet<OperatingSystemPattern>(Check.notNull(patterns, "patterns")));
		this.producer = Check.notNull(producer, "producer");
		this.producerUrl = Check.notNull(producerUrl, "producerUrl");
		this.url = Check.notNull(url, "url");
		this.icon = Check.notNull(icon, "icon");
		hash = buildHashCode(id, name, family, infoUrl, patterns, producer, producerUrl, url, icon);
	}

	/**
	 * Copies all information of the current operating system entry to the given user agent builder.
	 * 
	 * @param builder
	 *            user agent builder
	 */
	public void copyTo(@Nonnull final UserAgent.Builder builder) {
		final OperatingSystemFamily f = OperatingSystemFamily.evaluate(family);
		final VersionNumber version = VersionNumber.parseOperatingSystemVersion(f, builder.getUserAgentString());
		builder.setOperatingSystem(new net.sf.uadetector.OperatingSystem(f, family, icon, name, producer, producerUrl, url, version));
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
		if (id != other.id) {
			return false;
		}
		if (!name.equals(other.name)) {
			return false;
		}
		if (!family.equals(other.family)) {
			return false;
		}
		if (!infoUrl.equals(other.infoUrl)) {
			return false;
		}
		if (!patterns.equals(other.patterns)) {
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
		if (!icon.equals(other.icon)) {
			return false;
		}
		return true;
	}

	@Nonnull
	public String getFamily() {
		return family;
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
	public SortedSet<OperatingSystemPattern> getPatterns() {
		return patterns;
	}

	@Nonnull
	public String getProducer() {
		return producer;
	}

	@Nonnull
	public String getProducerUrl() {
		return producerUrl;
	}

	@Nonnull
	public String getUrl() {
		return url;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("OperatingSystem [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", family=");
		builder.append(family);
		builder.append(", infoUrl=");
		builder.append(infoUrl);
		builder.append(", patterns=");
		builder.append(patterns);
		builder.append(", producer=");
		builder.append(producer);
		builder.append(", producerUrl=");
		builder.append(producerUrl);
		builder.append(", url=");
		builder.append(url);
		builder.append(", icon=");
		builder.append(icon);
		builder.append("]");
		return builder.toString();
	}

}
