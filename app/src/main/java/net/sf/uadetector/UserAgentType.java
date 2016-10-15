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

import javax.annotation.Nonnull;

import net.sf.qualitycheck.Check;
import net.sf.uadetector.internal.data.domain.Robot;

/**
 * This enum represents the type of an user agent. The assignment to a type is performed within the UAS data.
 * 
 * @author André Rouél
 */
public enum UserAgentType {

	/**
	 * A web browser, especially on a desktop, notebook or workstation
	 */
	BROWSER("Browser"),

	/**
	 * An email client, email reader
	 */
	EMAIL_CLIENT("Email client"),

	/**
	 * A <b>news aggregator</b>, also termed a <b>feed aggregator<b>, <b>feed reader</b>, <b>news reader</b>, <b>RSS
	 * reader</b> or simply <b>aggregator</b>
	 */
	FEED_READER("Feed Reader"),

	/**
	 * A library is a collection of resources used to develop software.
	 */
	LIBRARY("Library"),

	/**
	 * Media player, also called multimedia player, is a term typically used to describe computer software for playing
	 * back multimedia files.
	 */
	MEDIAPLAYER("Multimedia Player"),

	/**
	 * A mobile browser, also called a microbrowser, minibrowser, or wireless internet browser (WIB), is a web browser
	 * designed for use on a mobile device.
	 */
	MOBILE_BROWSER("Mobile Browser"),

	/**
	 * Offline browser which may download completely or partially a website to a hard drive
	 */
	OFFLINE_BROWSER("Offline Browser"),

	/**
	 * A software that doesn't match the other types
	 */
	OTHER("Other"),

	/**
	 * Bots, such as Web crawlers
	 */
	ROBOT(Robot.TYPENAME),

	/**
	 * An unknown user agent type
	 */
	UNKNOWN(""),

	/**
	 * A software to hide the real user agent information
	 */
	USERAGENT_ANONYMIZER("Useragent Anonymizer"),

	/**
	 * A software or service that validates parts of a website or webservice, such as CSS, HTML, JSON, XML
	 */
	VALIDATOR("Validator"),

	/**
	 * A WAP browser is a web browser for mobile devices such as mobile phones that uses the Wireless Application
	 * Protocol (WAP). WAP is a technical standard for accessing information over a mobile wireless network.
	 */
	WAP_BROWSER("Wap Browser");

	/**
	 * This method try to find by the given type name a matching enum value. The type name must match against an user
	 * agent entry in UAS data file.
	 * 
	 * @param typeName
	 *            name of an user agent type
	 * @return the matching enum value or {@code UserAgentType#UNKNOWN}
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if the given argument is {@code null}
	 */
	public static UserAgentType evaluateByTypeName(@Nonnull final String typeName) {
		Check.notNull(typeName, "typeName");

		UserAgentType result = UNKNOWN;
		for (final UserAgentType value : values()) {
			if (value.getName().equals(typeName)) {
				result = value;
				break;
			}
		}
		return result;
	}

	/**
	 * Name of the user agent type
	 */
	@Nonnull
	private final String name;

	private UserAgentType(@Nonnull final String name) {
		this.name = name;
	}

	/**
	 * Returns the name of the user agent type.
	 * 
	 * @return name of the type
	 */
	@Nonnull
	public String getName() {
		return name;
	}

}
