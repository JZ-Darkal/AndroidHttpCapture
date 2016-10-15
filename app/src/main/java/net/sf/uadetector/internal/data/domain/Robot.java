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
import net.sf.uadetector.UserAgent;
import net.sf.uadetector.UserAgentFamily;
import net.sf.uadetector.UserAgentType;

@Immutable
public final class Robot implements Identifiable, Serializable {

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

		@Nonnull
		private String name = EMPTY;

		@Nonnull
		private String producer = EMPTY;

		@Nonnull
		private String producerUrl = EMPTY;

		@Nonnull
		private String userAgentString = EMPTY;

		public Builder() {
			// default constructor
		}

		public Builder(@Nonnull final Robot robot) {
			Check.notNull(robot, "robot");
			id = Check.notNegative(robot.getId(), "robot.getId()");
			name = Check.notNull(robot.getName(), "robot.getName()");
			family = Check.notNull(robot.getFamily(), "robot.getFamily()");
			familyName = Check.notNull(robot.getFamilyName(), "robot.getFamilyName()");
			infoUrl = Check.notNull(robot.getInfoUrl(), "robot.getInfoUrl()");
			producer = Check.notNull(robot.getProducer(), "robot.getProducer()");
			producerUrl = Check.notNull(robot.getProducerUrl(), "robot.getProducerUrl()");
			userAgentString = Check.notNull(robot.getUserAgentString(), "robot.getUserAgentString()");
			icon = Check.notNull(robot.getIcon(), "robot.getIcon()");
		}

		@Nonnull
		public Robot build() {
			return new Robot(id, name, family, familyName, infoUrl, producer, producerUrl, userAgentString, icon);
		}

		@Nonnull
		public Builder setFamilyName(@Nonnull final String familyName) {
			this.familyName = Check.notNull(familyName, "familyName");
			family = UserAgentFamily.evaluate(familyName);
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
			this.id = Integer.parseInt(Check.notEmpty(id.replace("\n","").replace("\t",""), "id"));
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
		public Builder setUserAgentString(@Nonnull final String userAgentString) {
			this.userAgentString = Check.notNull(userAgentString, "userAgentString");
			return this;
		}

	}

	private static final long serialVersionUID = -605392434061575985L;

	/**
	 * Default type name to support the classification against corresponding enum later
	 */
	public static final String TYPENAME = "Robot";

	private static int buildHashCode(@Nonnegative final int id, @Nonnull final String name, @Nonnull final UserAgentFamily family,
			@Nonnull final String familyName, @Nonnull final String infoUrl, @Nonnull final String producer,
			@Nonnull final String producerUrl, @Nonnull final String userAgentString, @Nonnull final String icon) {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + name.hashCode();
		result = prime * result + family.hashCode();
		result = prime * result + familyName.hashCode();
		result = prime * result + infoUrl.hashCode();
		result = prime * result + producer.hashCode();
		result = prime * result + producerUrl.hashCode();
		result = prime * result + userAgentString.hashCode();
		result = prime * result + icon.hashCode();
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

	@Nonnull
	private final String name;

	@Nonnull
	private final String producer;

	@Nonnull
	private final String producerUrl;

	@Nonnull
	private final String userAgentString;

	public Robot(@Nonnegative final int id, @Nonnull final String name, @Nonnull final UserAgentFamily family,
			@Nonnull final String familyName, @Nonnull final String infoUrl, @Nonnull final String producer,
			@Nonnull final String producerUrl, @Nonnull final String userAgentString, @Nonnull final String icon) {
		this.id = Check.notNegative(id, "id");
		this.name = Check.notNull(name, "name");
		this.family = Check.notNull(family, "family");
		this.familyName = Check.notNull(familyName, "familyName");
		this.infoUrl = Check.notNull(infoUrl, "infoUrl");
		this.producer = Check.notNull(producer, "producer");
		this.producerUrl = Check.notNull(producerUrl, "producerUrl");
		this.userAgentString = Check.notNull(userAgentString, "userAgentString");
		this.icon = Check.notNull(icon, "icon");
		hash = buildHashCode(id, name, family, familyName, infoUrl, producer, producerUrl, userAgentString, icon);
	}

	public void copyTo(@Nonnull final UserAgent.Builder builder) {
		builder.setFamily(family);
		builder.setIcon(icon);
		builder.setName(name);
		builder.setProducer(producer);
		builder.setProducerUrl(producerUrl);
		builder.setUrl(infoUrl);
		builder.setType(UserAgentType.ROBOT);
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
		final Robot other = (Robot) obj;
		if (id != other.id) {
			return false;
		}
		if (!name.equals(other.name)) {
			return false;
		}
		if (!family.equals(other.family)) {
			return false;
		}
		if (!familyName.equals(other.familyName)) {
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
		if (!userAgentString.equals(other.userAgentString)) {
			return false;
		}
		if (!icon.equals(other.icon)) {
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

	@Nonnull
	public String getName() {
		return name;
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
	public String getUserAgentString() {
		return userAgentString;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ReadableRobot [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", family=");
		builder.append(family);
		builder.append(", familyName=");
		builder.append(familyName);
		builder.append(", infoUrl=");
		builder.append(infoUrl);
		builder.append(", producer=");
		builder.append(producer);
		builder.append(", producerUrl=");
		builder.append(producerUrl);
		builder.append(", userAgentString=");
		builder.append(userAgentString);
		builder.append(", icon=");
		builder.append(icon);
		builder.append("]");
		return builder.toString();
	}

}
