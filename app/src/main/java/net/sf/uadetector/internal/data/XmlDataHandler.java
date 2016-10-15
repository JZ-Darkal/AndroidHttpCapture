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
package net.sf.uadetector.internal.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.Nonnull;

import net.sf.qualitycheck.Check;
import net.sf.uadetector.internal.data.domain.Browser;
import net.sf.uadetector.internal.data.domain.BrowserOperatingSystemMapping;
import net.sf.uadetector.internal.data.domain.BrowserPattern;
import net.sf.uadetector.internal.data.domain.BrowserType;
import net.sf.uadetector.internal.data.domain.Device;
import net.sf.uadetector.internal.data.domain.DevicePattern;
import net.sf.uadetector.internal.data.domain.OperatingSystem;
import net.sf.uadetector.internal.data.domain.OperatingSystemPattern;
import net.sf.uadetector.internal.data.domain.Robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public final class XmlDataHandler extends DefaultHandler {

	public enum Tag {

		/**
		 * Tag name of a browser entry
		 */
		BROWSER("browser"),

		/**
		 * Tag name of the ID of an browser pattern
		 */
		BROWSER_ID("browser_id"),

		/**
		 * Tag name of the informational URL of a browser entry
		 */
		BROWSER_INFO_URL("browser_info_url"),

		/**
		 * Tag name of a mapping entry between a browser and an operating system
		 */
		BROWSER_OS_MAPPING("browser_os"),

		/**
		 * Tag name of a browser pattern
		 */
		BROWSER_PATTERN("browser_reg"),

		/**
		 * Tag name of a browser type entry
		 */
		BROWSER_TYPE("browser_type"),

		/**
		 * Tag name of the type ID of a browser
		 */
		BROWSER_TYPE_ID("type"),

		/**
		 * Tag name of a producer company of an user agent
		 */
		COMPANY("company"),

		/**
		 * Tag name of the URL of a producer company from an user agent
		 */
		COMPANY_URL("url_company"),

		/**
		 * Tag name of a device
		 */
		DEVICE("device"),

		/**
		 * Tag name of a device pattern
		 */
		DEVICE_ID("device_id"),

		/**
		 * Tag name of the informational URL of an device entry
		 */
		DEVICE_INFO_URL("device_info_url"),

		/**
		 * Tag name of a device pattern
		 */
		DEVICE_PATTERN("device_reg"),

		/**
		 * Tag name of all devices
		 */
		DEVICES("devices"),

		/**
		 * Tag name of all device patterns
		 */
		DEVICES_PATTERN("devices_reg"),

		/**
		 * Tag name of an family of an user agent
		 */
		FAMILY("family"),

		/**
		 * Tag name of the icon of an entry
		 */
		ICON("icon"),

		/**
		 * Tag name of an ID of an user agent
		 */
		ID("id"),

		/**
		 * Tag name of the product name of an user agent
		 */
		NAME("name"),

		/**
		 * Tag name of an operating system entry
		 */
		OPERATING_SYSTEM("os"),

		/**
		 * Tag name of the ID of an operating system pattern
		 */
		OPERATING_SYSTEM_ID("os_id"),

		/**
		 * Tag name of the informational URL of an operating system entry
		 */
		OPERATING_SYSTEM_INFO_URL("os_info_url"),

		/**
		 * Tag name of an operating system pattern
		 */
		OPERATING_SYSTEM_PATTERN("operating_system_reg"),

		/**
		 * Tag name of the order of an user agent pattern
		 */
		PATTERN_ORDER("order"),

		/**
		 * Tag name of the regular expression of an user agent pattern
		 */
		PATTERN_REGEX("regstring"),

		/**
		 * Tag name of a robot entry
		 */
		ROBOT("robot"),

		/**
		 * Tag name of the informational URL of a robot entry
		 */
		ROBOT_INFO_URL("bot_info_url"),

		/**
		 * Tag name of the product URL of an user agent
		 */
		URL("url"),

		/**
		 * Tag name of an user agent string of a robot entry
		 */
		USERAGENT("useragent"),

		/**
		 * Tag name of the data version
		 */
		VERSION("version");

		public static Tag evaluate(@Nonnull final String tagName) {
			Check.notNull(tagName, "tagName");

			Tag result = null;
			for (final Tag tag : values()) {
				if (tag.getTagName().equalsIgnoreCase(tagName)) {
					result = tag;
					break;
				}
			}
			return result;
		}

		public static boolean isBrowserOsMappingTag(final String tagName) {
			return BROWSER_OS_MAPPING.getTagName().equalsIgnoreCase(tagName);
		}

		public static boolean isBrowserPatternTag(final String tagName) {
			return BROWSER_PATTERN.getTagName().equalsIgnoreCase(tagName);
		}

		public static boolean isBrowserTag(final String tagName) {
			return BROWSER.getTagName().equalsIgnoreCase(tagName);
		}

		public static boolean isBrowserTypeTag(final String tagName) {
			return BROWSER_TYPE.getTagName().equalsIgnoreCase(tagName);
		}

		public static boolean isDevicePatternTag(final String tagName) {
			return DEVICE_PATTERN.getTagName().equalsIgnoreCase(tagName);
		}

		public static boolean isDeviceTag(final String tagName) {
			return DEVICE.getTagName().equalsIgnoreCase(tagName);
		}

		public static boolean isIdTag(final String tagName) {
			return ID.getTagName().equalsIgnoreCase(tagName);
		}

		public static boolean isOperatingSystemPatternTag(final String tagName) {
			return OPERATING_SYSTEM_PATTERN.getTagName().equalsIgnoreCase(tagName);
		}

		public static boolean isOperatingSystemTag(final String tagName) {
			return OPERATING_SYSTEM.getTagName().equalsIgnoreCase(tagName);
		}

		public static boolean isRobotTag(final String tagName) {
			return ROBOT.getTagName().equalsIgnoreCase(tagName);
		}

		@Nonnull
		private String tagName;

		private Tag(@Nonnull final String tagName) {
			this.tagName = tagName;
		}

		@Nonnull
		public String getTagName() {
			return tagName;
		}

	}

	/**
	 * Character set to read the internal Document Type Definition (DTD) of UAS data
	 */
	private static final String CHARSET = "UTF-8";

	/**
	 * Corresponding logger for this class
	 */
	private static final Logger LOG = LoggerFactory.getLogger(XmlDataHandler.class);

	/**
	 * Path to the internal Document Type Definition (DTD) of UAS data files to be able to work completely offline
	 */
	protected static final String UASDATA_DEF = "uadetector/uasxmldata.dtd";

	/**
	 * URL to the Document Type Definition (DTD) of UAS data files
	 */
	protected static final String UASDATA_DEF_URL = "http://data.udger.com/uasxmldata_old.dtd";

	/**
	 * Logs an issue while parsing XML.
	 * 
	 * @param prefix
	 *            log level as string to add at the beginning of the message
	 * @param e
	 *            exception to log
	 */
	protected static void logParsingIssue(final String prefix, final SAXParseException e) {
		final StringBuilder buffer = new StringBuilder();
		buffer.append(prefix);
		buffer.append(" while reading UAS data: ");
		buffer.append(e.getMessage());
		buffer.append(" (line: ");
		buffer.append(e.getLineNumber());
		if (e.getSystemId() != null) {
			buffer.append(" uri: ");
			buffer.append(e.getSystemId());
		}
		buffer.append(")");
		LOG.warn(buffer.toString());
	}

	private Browser.Builder browserBuilder = new Browser.Builder();

	private Device.Builder deviceBuilder = new Device.Builder();

	private BrowserOperatingSystemMapping.Builder browserOsMappingBuilder = new BrowserOperatingSystemMapping.Builder();

	private BrowserPattern.Builder browserPatternBuilder = new BrowserPattern.Builder();

	private DevicePattern.Builder devicePatternBuilder = new DevicePattern.Builder();

	private BrowserType.Builder browserTypeBuilder = new BrowserType.Builder();

	private StringBuilder buffer = new StringBuilder();

	private Tag currentTag = null;

	@Nonnull
	private final DataBuilder dataBuilder;

	/**
	 * Flag to note that a fatal error occurred while parsing the document
	 */
	private boolean error = false;

	private boolean isBrowser = false;

	private boolean isBrowserOsMapping = false;

	private boolean isBrowserPattern = false;

	private boolean isBrowserType = false;

	private boolean isDevice = false;

	private boolean isDevicePattern = false;

	private boolean isOperatingSystem = false;

	private boolean isOperatingSystemPattern = false;

	private boolean isRobot = false;

	private OperatingSystem.Builder operatingSystemBuilder = new OperatingSystem.Builder();

	private OperatingSystemPattern.Builder operatingSystemPatternBuilder = new OperatingSystemPattern.Builder();

	private Robot.Builder robotBuilder = new Robot.Builder();

	/**
	 * Flag to note that a warning occurred while parsing the document
	 */
	private boolean warning = false;

	public XmlDataHandler(@Nonnull final DataBuilder builder) {
		Check.notNull(builder, "builder");

		dataBuilder = builder;
	}

	private void addToBrowserBuilder() {
		if (isBrowser) {
			if (currentTag == Tag.ID) {
				browserBuilder.setId(buffer.toString());
			} else if (currentTag == Tag.BROWSER_TYPE_ID) {
				browserBuilder.setTypeId(buffer.toString());
			} else if (currentTag == Tag.NAME) {
				browserBuilder.setFamilyName(buffer.toString());
			} else if (currentTag == Tag.URL) {
				browserBuilder.setUrl(buffer.toString());
			} else if (currentTag == Tag.COMPANY) {
				browserBuilder.setProducer(buffer.toString());
			} else if (currentTag == Tag.COMPANY_URL) {
				browserBuilder.setProducerUrl(buffer.toString());
			} else if (currentTag == Tag.ICON) {
				browserBuilder.setIcon(buffer.toString());
			} else if (currentTag == Tag.BROWSER_INFO_URL) {
				browserBuilder.setInfoUrl(buffer.toString());
			}
		}
	}

	private void addToBrowserOsMappingBuilder() {
		if (isBrowserOsMapping && currentTag == Tag.BROWSER_ID) {
			browserOsMappingBuilder.setBrowserId(buffer.toString());
		} else if (isBrowserOsMapping && currentTag == Tag.OPERATING_SYSTEM_ID) {
			browserOsMappingBuilder.setOperatingSystemId(buffer.toString());
		}
	}

	private void addToBrowserPatternBuilder() {
		if (isBrowserPattern && currentTag == Tag.PATTERN_ORDER) {
			browserPatternBuilder.setPosition(buffer.toString());
		} else if (isBrowserPattern && currentTag == Tag.BROWSER_ID) {
			browserPatternBuilder.setId(buffer.toString());
		} else if (isBrowserPattern && currentTag == Tag.PATTERN_REGEX) {
			browserPatternBuilder.setPerlRegularExpression(buffer.toString());
		}
	}

	private void addToBrowserTypeBuilder() {
		if (isBrowserType && currentTag == Tag.ID) {
			browserTypeBuilder.setId(buffer.toString());
		} else if (isBrowserType && currentTag == Tag.BROWSER_TYPE_ID) {
			browserTypeBuilder.setName(buffer.toString());
		}
	}

	private void addToDeviceBuilder() {
		if (isDevice) {
			if (currentTag == Tag.ID) {
				deviceBuilder.setId(buffer.toString());
			} else if (currentTag == Tag.NAME) {
				deviceBuilder.setName(buffer.toString());
			} else if (currentTag == Tag.ICON) {
				deviceBuilder.setIcon(buffer.toString());
			} else if (currentTag == Tag.DEVICE_INFO_URL) {
				deviceBuilder.setInfoUrl(buffer.toString());
			}
		}
	}

	private void addToDevicePatternBuilder() {
		if (isDevicePattern && currentTag == Tag.PATTERN_ORDER) {
			devicePatternBuilder.setPosition(buffer.toString());
		} else if (isDevicePattern && currentTag == Tag.DEVICE_ID) {
			devicePatternBuilder.setId(buffer.toString());
		} else if (isDevicePattern && currentTag == Tag.PATTERN_REGEX) {
			devicePatternBuilder.setPerlRegularExpression(buffer.toString());
		}
	}

	private void addToOperatingSystemBuilder() {
		if (isOperatingSystem) {
			if (currentTag == Tag.ID) {
				operatingSystemBuilder.setId(buffer.toString());
			} else if (currentTag == Tag.FAMILY) {
				operatingSystemBuilder.setFamily(buffer.toString());
			} else if (currentTag == Tag.NAME) {
				operatingSystemBuilder.setName(buffer.toString());
			} else if (currentTag == Tag.URL) {
				operatingSystemBuilder.setUrl(buffer.toString());
			} else if (currentTag == Tag.COMPANY) {
				operatingSystemBuilder.setProducer(buffer.toString());
			} else if (currentTag == Tag.COMPANY_URL) {
				operatingSystemBuilder.setProducerUrl(buffer.toString());
			} else if (currentTag == Tag.ICON) {
				operatingSystemBuilder.setIcon(buffer.toString());
			} else if (currentTag == Tag.OPERATING_SYSTEM_INFO_URL) {
				operatingSystemBuilder.setInfoUrl(buffer.toString());
			}
		}
	}

	private void addToOperatingSystemPatternBuilder() {
		if (isOperatingSystemPattern) {
			if (currentTag == Tag.PATTERN_ORDER) {
				operatingSystemPatternBuilder.setPosition(buffer.toString());
			} else if (currentTag == Tag.OPERATING_SYSTEM_ID) {
				operatingSystemPatternBuilder.setId(buffer.toString());
			} else if (currentTag == Tag.PATTERN_REGEX) {
				operatingSystemPatternBuilder.setPerlRegularExpression(buffer.toString());
			}
		}
	}

	private void addToRobotBuilder() {
		if (isRobot) {
			if (currentTag == Tag.ID) {
				robotBuilder.setId(buffer.toString());
			} else if (currentTag == Tag.USERAGENT) {
				robotBuilder.setUserAgentString(buffer.toString());
			} else if (currentTag == Tag.FAMILY) {
				robotBuilder.setFamilyName(buffer.toString());
			} else if (currentTag == Tag.NAME) {
				robotBuilder.setName(buffer.toString());
			} else if (currentTag == Tag.COMPANY) {
				robotBuilder.setProducer(buffer.toString());
			} else if (currentTag == Tag.COMPANY_URL) {
				robotBuilder.setProducerUrl(buffer.toString());
			} else if (currentTag == Tag.ICON) {
				robotBuilder.setIcon(buffer.toString());
			} else if (currentTag == Tag.ROBOT_INFO_URL) {
				robotBuilder.setInfoUrl(buffer.toString());
			}
		}
	}

	@Override
	public void characters(final char ch[], final int start, final int length) throws SAXException {
		buffer.append(new String(ch, start, length));
	}

	@Override
	public void endElement(final String uri, final String localName, final String tagName) throws SAXException {

		transferToSpecificBuilderAndReset();

		if (Tag.isRobotTag(tagName)) {
			saveAndResetRobotBuilder();
			isRobot = false;
		} else if (Tag.isBrowserTag(tagName)) {
			saveAndResetBrowserBuilder();
			isBrowser = false;
		} else if (Tag.isOperatingSystemTag(tagName)) {
			saveAndResetOperatingSystemBuilder();
			isOperatingSystem = false;
		} else if (Tag.isBrowserTypeTag(tagName)) {
			saveAndResetBrowserTypeBuilder();
			isBrowserType = false;
		} else if (Tag.isBrowserPatternTag(tagName)) {
			saveAndResetBrowserPatternBuilder();
			isBrowserPattern = false;
		} else if (Tag.isBrowserOsMappingTag(tagName)) {
			saveAndResetBrowserOperatingSystemMapping();
			isBrowserOsMapping = false;
		} else if (Tag.isOperatingSystemPatternTag(tagName)) {
			saveAndResetOperatingSystemPatternBuilder();
			isOperatingSystemPattern = false;
		} else if (Tag.isDeviceTag(tagName)) {
			saveAndResetDeviceBuilder();
			isDevice = false;
		} else if (Tag.isDevicePatternTag(tagName)) {
			saveAndResetDevicePatternBuilder();
			isDevicePattern = false;
		}

		currentTag = null;
	}

	@Override
	public void error(final SAXParseException e) throws SAXException {
		error = true;
		logParsingIssue("Error", e);
		super.fatalError(e);
	}

	@Override
	public void fatalError(final SAXParseException e) throws SAXException {
		logParsingIssue("Fatal error", e);

		// this call throws a SAXException
		super.fatalError(e);
	}

	/**
	 * Gets the flag whether an error occurred while parsing the document.
	 * 
	 * @return {@code true} if an error occurred otherwise {@code false}
	 */
	public boolean hasError() {
		return error;
	}

	/**
	 * Gets the flag whether an warning occurred while parsing the document.
	 * 
	 * @return {@code true} if an warning occurred otherwise {@code false}
	 */
	public boolean hasWarning() {
		return warning;
	}

	@Override
	public InputSource resolveEntity(final String publicId, final String systemId) throws IOException, SAXException {
		if (UASDATA_DEF_URL.equals(systemId)) {
			final InputStream stream = this.getClass().getClassLoader().getResourceAsStream(UASDATA_DEF);
			return new InputSource(new InputStreamReader(stream, CHARSET));
		}
		throw new SAXException("unable to resolve remote entity, systemId = " + systemId);
	}

	private void saveAndResetBrowserBuilder() {
		dataBuilder.appendBrowserBuilder(browserBuilder);
		browserBuilder = new Browser.Builder();
	}

	private void saveAndResetBrowserOperatingSystemMapping() {
		dataBuilder.appendBrowserOperatingSystemMapping(browserOsMappingBuilder.build());
		browserOsMappingBuilder = new BrowserOperatingSystemMapping.Builder();
	}

	private void saveAndResetBrowserPatternBuilder() {
		try {
			dataBuilder.appendBrowserPattern(browserPatternBuilder.build());
		} catch (final IllegalArgumentException e) {
			LOG.warn("Can not append browser pattern: " + e.getLocalizedMessage());
		}
		browserPatternBuilder = new BrowserPattern.Builder();
	}

	private void saveAndResetBrowserTypeBuilder() {
		dataBuilder.appendBrowserType(browserTypeBuilder.build());
		browserTypeBuilder = new BrowserType.Builder();
	}

	private void saveAndResetDeviceBuilder() {
		dataBuilder.appendDeviceBuilder(deviceBuilder);
		deviceBuilder = new Device.Builder();
	}

	private void saveAndResetDevicePatternBuilder() {
		try {
			dataBuilder.appendDevicePattern(devicePatternBuilder.build());
		} catch (final IllegalArgumentException e) {
			LOG.warn("Can not append device pattern: " + e.getLocalizedMessage());
		}
		devicePatternBuilder = new DevicePattern.Builder();
	}

	private void saveAndResetOperatingSystemBuilder() {
		dataBuilder.appendOperatingSystemBuilder(operatingSystemBuilder);
		operatingSystemBuilder = new OperatingSystem.Builder();
	}

	private void saveAndResetOperatingSystemPatternBuilder() {
		try {
			dataBuilder.appendOperatingSystemPattern(operatingSystemPatternBuilder.build());
		} catch (final IllegalArgumentException e) {
			LOG.warn("Can not append OS pattern: " + e.getLocalizedMessage());
		}
		operatingSystemPatternBuilder = new OperatingSystemPattern.Builder();
	}

	private void saveAndResetRobotBuilder() {
		dataBuilder.appendRobot(robotBuilder.build());
		robotBuilder = new Robot.Builder();
	}

	@Override
	public void startElement(final String uri, final String localName, final String tagName, final Attributes attributes)
			throws SAXException {

		if (Tag.isRobotTag(tagName)) {
			isRobot = true;
		} else if (Tag.isBrowserTag(tagName)) {
			isBrowser = true;
		} else if (Tag.isOperatingSystemTag(tagName)) {
			isOperatingSystem = true;
		} else if (Tag.isBrowserTypeTag(tagName)) {
			isBrowserType = true;
		} else if (Tag.isBrowserPatternTag(tagName)) {
			isBrowserPattern = true;
		} else if (Tag.isBrowserOsMappingTag(tagName)) {
			isBrowserOsMapping = true;
		} else if (Tag.isOperatingSystemPatternTag(tagName)) {
			isOperatingSystemPattern = true;
		} else if (Tag.isDeviceTag(tagName)) {
			isDevice = true;
		} else if (Tag.isDevicePatternTag(tagName)) {
			isDevicePattern = true;
		}

		currentTag = Tag.evaluate(tagName);
	}

	/**
	 * Transfers all characters of a specific tag to the corresponding builder and resets the string buffer.
	 */
	private void transferToSpecificBuilderAndReset() {

		// version
		if (currentTag == Tag.VERSION) {
			dataBuilder.setVersion(buffer.toString());
		}

		// robot browser
		addToRobotBuilder();

		// build browser
		addToBrowserBuilder();

		// build operating system
		addToOperatingSystemBuilder();

		// build browser pattern
		addToBrowserPatternBuilder();

		// build browser type
		addToBrowserTypeBuilder();

		// build browser to operating system mapping
		addToBrowserOsMappingBuilder();

		// build operating system pattern
		addToOperatingSystemPatternBuilder();

		// build browser
		addToDeviceBuilder();

		// build browser pattern
		addToDevicePatternBuilder();

		buffer = new StringBuilder();
	}

	@Override
	public void warning(final SAXParseException e) throws SAXException {
		warning = true;
		logParsingIssue("Warning", e);
		super.warning(e);
	}

}
