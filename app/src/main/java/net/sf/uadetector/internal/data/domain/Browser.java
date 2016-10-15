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
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import net.sf.qualitycheck.Check;
import net.sf.uadetector.UserAgent;
import net.sf.uadetector.UserAgentFamily;

@Immutable
public final class Browser implements Identifiable, Serializable {

	@NotThreadSafe
	public static final class Builder {

		private static final String EMPTY = "";

		@Nonnull
		private UserAgentFamily family = UserAgentFamily.UNKNOWN;

		@Nonnull
		private String familyName = EMPTY;

		@Nonnull
		private String icon = EMPTY;

		private int id = Integer.MIN_VALUE;

		@Nonnull
		private String infoUrl = EMPTY;

		@Nullable
		private OperatingSystem operatingSystem;

		@Nonnull
		private SortedSet<BrowserPattern> patterns = new TreeSet<BrowserPattern>();

		@Nonnull
		private String producer = EMPTY;

		@Nonnull
		private String producerUrl = EMPTY;

		@Nullable
		private BrowserType type;

		private transient int typeId = Integer.MIN_VALUE;

		@Nonnull
		private String url = EMPTY;

		public Builder() {
			// default constructor
		}

		public Builder(@Nonnull final Browser browser) {
			Check.notNull(browser, "browser");
			id = Check.notNegative(browser.getId(), "browser.getId()");
			family = Check.notNull(browser.getFamily(), "browser.getFamily()");
			familyName = Check.notNull(browser.getFamilyName(), "browser.getFamilyName()");
			patterns = new TreeSet<BrowserPattern>(Check.notNull(browser.getPatterns(), "browser.getPatterns()"));
			type = Check.notNull(browser.getType(), "browser.getType()");
			operatingSystem = Check.notNull(browser.getOperatingSystem(), "browser.getOperatingSystem()");
			icon = Check.notNull(browser.getIcon(), "browser.getIcon()");
			infoUrl = Check.notNull(browser.getInfoUrl(), "browser.getInfoUrl()");
			producer = Check.notNull(browser.getProducer(), "browser.getProducer()");
			producerUrl = Check.notNull(browser.getProducerUrl(), "browser.getProducerUrl()");
			url = Check.notNull(browser.getUrl(), "browser.getUrl()");
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
			familyName = builder.familyName;
			icon = builder.icon;
			id = builder.id;
			infoUrl = builder.infoUrl;
			operatingSystem = builder.operatingSystem;
			patterns = builder.patterns;
			producer = builder.producer;
			producerUrl = builder.producerUrl;
			type = builder.type;
			typeId = builder.typeId;
			url = builder.url;
		}

		@Nonnull
		public Browser build() {
			return new Browser(id, family, familyName, patterns, type, operatingSystem, icon, infoUrl, producer, producerUrl, url);
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

		@Nonnull
		public UserAgentFamily getFamily() {
			return family;
		}

		@Nonnull
		public String getFamilyName() {
			return familyName;
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

		@Nullable
		public OperatingSystem getOperatingSystem() {
			return operatingSystem;
		}

		@Nonnull
		public SortedSet<BrowserPattern> getPatterns() {
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

		@Nullable
		public BrowserType getType() {
			return type;
		}

		public int getTypeId() {
			return typeId;
		}

		@Nonnull
		public String getUrl() {
			return url;
		}

		@Nonnull
		private Builder setFamily(@Nonnull final UserAgentFamily family) {
			this.family = Check.notNull(family, "family");
			return this;
		}

		@Nonnull
		public Builder setFamilyName(@Nonnull final String familyName) {
			this.familyName = Check.notNull(familyName, "familyName");
			return setFamily(UserAgentFamily.evaluate(familyName));
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
		public Builder setOperatingSystem(@Nonnull final OperatingSystem operatingSystem) {
			this.operatingSystem = Check.notNull(operatingSystem, "operatingSystem");
			return this;
		}

		@Nonnull
		public Builder setPatterns(@Nonnull final SortedSet<BrowserPattern> patterns) {
			this.patterns = new TreeSet<BrowserPattern>(Check.notNull(patterns, "patterns"));
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
		public Builder setType(@Nonnull final BrowserType type) {
			this.type = Check.notNull(type, "type");
			setTypeId(type.getId());
			return this;
		}

		@Nonnull
		public Builder setTypeId(@Nonnegative final int typeId) {
			this.typeId = Check.notNegative(typeId, "typeId");
			return this;
		}

		@Nonnull
		public Builder setTypeId(@Nonnull final String typeId) {
			setTypeId(Integer.parseInt(Check.notEmpty(typeId.replace("\n","").replace("\t",""), "typeId")));
			return this;
		}

		@Nonnull
		public Builder setUrl(@Nonnull final String url) {
			this.url = Check.notNull(url, "url");
			return this;
		}

	}

	private static final long serialVersionUID = 6741143419664475577L;

	private static int buildHashCode(@Nonnegative final int id, @Nonnull final UserAgentFamily family, @Nonnull final String familyName,
			@Nonnull final SortedSet<BrowserPattern> patterns, @Nonnull final BrowserType type,
			@Nullable final OperatingSystem operatingSystem, @Nonnull final String icon, @Nonnull final String infoUrl,
			@Nonnull final String producer, @Nonnull final String producerUrl, @Nonnull final String url) {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + family.hashCode();
		result = prime * result + familyName.hashCode();
		result = prime * result + patterns.hashCode();
		result = prime * result + type.hashCode();
		result = prime * result + (operatingSystem == null ? 0 : operatingSystem.hashCode());
		result = prime * result + icon.hashCode();
		result = prime * result + infoUrl.hashCode();
		result = prime * result + producer.hashCode();
		result = prime * result + producerUrl.hashCode();
		result = prime * result + url.hashCode();
		return result;
	}

	@Nonnull
	private final UserAgentFamily family;

	@Nonnull
	private final String familyName;

	private final int hash;

	@Nonnull
	private final String icon;

	@Nonnegative
	private final int id;

	@Nonnull
	private final String infoUrl;

	@Nullable
	private final OperatingSystem operatingSystem;

	@Nonnull
	private final SortedSet<BrowserPattern> patterns;

	@Nonnull
	private final String producer;

	@Nonnull
	private final String producerUrl;

	@Nonnull
	private final BrowserType type;

	@Nonnull
	private final String url;

	public Browser(@Nonnegative final int id, @Nonnull final UserAgentFamily family, @Nonnull final String familyName,
			@Nonnull final SortedSet<BrowserPattern> patterns, @Nonnull final BrowserType type,
			@Nonnull final OperatingSystem operatingSystem, @Nonnull final String icon, @Nonnull final String infoUrl,
			@Nonnull final String producer, @Nonnull final String producerUrl, @Nonnull final String url) {
		this.id = Check.notNegative(id, "id");
		this.family = Check.notNull(family, "family");
		this.familyName = Check.notNull(familyName, "familyName");
		this.patterns = Collections.unmodifiableSortedSet(new TreeSet<BrowserPattern>(Check.notNull(patterns, "patterns")));
		this.type = Check.notNull(type, "type");
		this.operatingSystem = operatingSystem;
		this.icon = Check.notNull(icon, "icon");
		this.infoUrl = Check.notNull(infoUrl, "infoUrl");
		this.producer = Check.notNull(producer, "producer");
		this.producerUrl = Check.notNull(producerUrl, "producerUrl");
		this.url = Check.notNull(url, "url");
		hash = buildHashCode(id, family, familyName, patterns, type, operatingSystem, icon, infoUrl, producer, producerUrl, url);
	}

	/**
	 * Copy values from itself to a <code>UserAgentInfo.Builder</code>.
	 */
	public void copyTo(@Nonnull final UserAgent.Builder builder) {
		builder.setFamily(family);
		builder.setIcon(icon);
		builder.setName(familyName);
		builder.setProducer(producer);
		builder.setProducerUrl(producerUrl);
		builder.setTypeName(type.getName());
		builder.setUrl(url);
		if (operatingSystem != null) {
			operatingSystem.copyTo(builder);
		}
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
		final Browser other = (Browser) obj;
		if (id != other.id) {
			return false;
		}
		if (!family.equals(other.family)) {
			return false;
		}
		if (!familyName.equals(other.familyName)) {
			return false;
		}
		if (!patterns.equals(other.patterns)) {
			return false;
		}
		if (!type.equals(other.type)) {
			return false;
		}
		if (operatingSystem == null) {
			if (other.operatingSystem != null) {
				return false;
			}
		} else if (!operatingSystem.equals(other.operatingSystem)) {
			return false;
		}
		if (!icon.equals(other.icon)) {
			return false;
		}
		if (!infoUrl.equals(other.infoUrl)) {
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
		return true;
	}

	@Nonnull
	public UserAgentFamily getFamily() {
		return family;
	}

	@Nonnull
	public String getFamilyName() {
		return familyName;
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

	@Nullable
	public OperatingSystem getOperatingSystem() {
		return operatingSystem;
	}

	@Nonnull
	public SortedSet<BrowserPattern> getPatterns() {
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
	public BrowserType getType() {
		return type;
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
		builder.append("Browser [id=");
		builder.append(id);
		builder.append(", family=");
		builder.append(family);
		builder.append(", familyName=");
		builder.append(familyName);
		builder.append(", patterns=");
		builder.append(patterns);
		builder.append(", type=");
		builder.append(type);
		builder.append(", operatingSystem=");
		builder.append(operatingSystem);
		builder.append(", icon=");
		builder.append(icon);
		builder.append(", infoUrl=");
		builder.append(infoUrl);
		builder.append(", producer=");
		builder.append(producer);
		builder.append(", producerUrl=");
		builder.append(producerUrl);
		builder.append(", url=");
		builder.append(url);
		builder.append("]");
		return builder.toString();
	}

}
