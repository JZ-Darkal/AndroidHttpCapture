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
package net.sf.uadetector.writer;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.qualitycheck.Check;
import net.sf.uadetector.internal.data.BrowserOperatingSystemMappingComparator;
import net.sf.uadetector.internal.data.Data;
import net.sf.uadetector.internal.data.IdentifiableComparator;
import net.sf.uadetector.internal.data.OrderedPatternComparator;
import net.sf.uadetector.internal.data.domain.Browser;
import net.sf.uadetector.internal.data.domain.BrowserOperatingSystemMapping;
import net.sf.uadetector.internal.data.domain.BrowserPattern;
import net.sf.uadetector.internal.data.domain.BrowserType;
import net.sf.uadetector.internal.data.domain.Device;
import net.sf.uadetector.internal.data.domain.DevicePattern;
import net.sf.uadetector.internal.data.domain.OperatingSystem;
import net.sf.uadetector.internal.data.domain.OperatingSystemPattern;
import net.sf.uadetector.internal.data.domain.Robot;
import net.sf.uadetector.internal.util.RegularExpressionConverter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This utility is intended to transform an instance of {@code Data} into an <i>UAS data</i> conform XML document and
 * allows us to recreate an <code>uas.xml</code>.
 * 
 * @author André Rouél
 */
@ThreadSafe
public final class XmlDataWriter {

	interface Tag {
		String BOT_INFO_URL = "bot_info_url";
		String BROWSER = "browser";
		String BROWSER_ID = "browser_id";
		String BROWSER_INFO_URL = "browser_info_url";
		String BROWSER_OS = "browser_os";
		String BROWSER_REG = "browser_reg";
		String BROWSER_TYPE = "browser_type";
		String BROWSER_TYPES = "browser_types";
		String BROWSERS = "browsers";
		String BROWSERS_OS = "browsers_os";
		String BROWSERS_REG = "browsers_reg";
		String COMPANY = "company";
		String DATA = "data";
		String DESCRIPTION = "description";
		String DEVICE = "device";
		String DEVICE_ID = "device_id";
		String DEVICE_INFO_URL = "device_info_url";
		String DEVICE_REG = "device_reg";
		String DEVICES = "devices";
		String DEVICES_REG = "devices_reg";
		String FAMILY = "family";
		String ICON = "icon";
		String ID = "id";
		String LABEL = "label";
		String NAME = "name";
		String OPERATING_SYSTEM_REG = "operating_system_reg";
		String OPERATING_SYSTEMS = "operating_systems";
		String OPERATING_SYSTEMS_REG = "operating_systems_reg";
		String ORDER = "order";
		String OS = "os";
		String OS_ID = "os_id";
		String OS_INFO_URL = "os_info_url";
		String REGSTRING = "regstring";
		String ROBOT = "robot";
		String ROBOTS = "robots";
		String TYPE = "type";
		String UASDATA = "uasdata";
		String URL = "url";
		String URL_COMPANY = "url_company";
		String USERAGENT = "useragent";
	}

	private static final String INDENT_AMOUNT = "4";

	private static final String INDENT_OPTION = "yes";

	private static final String SCHEMA_URL = "http://data.udger.com/uasxmldata_old.dtd";

	private static Element createBrowser(final Browser browser, final Document doc) {
		final Element b = doc.createElement(Tag.BROWSER);
		final Element id = doc.createElement(Tag.ID);
		id.appendChild(doc.createTextNode(String.valueOf(browser.getId())));
		b.appendChild(id);
		final Element family = doc.createElement(Tag.TYPE);
		family.appendChild(doc.createTextNode(String.valueOf(browser.getType().getId())));
		b.appendChild(family);
		final Element name = doc.createElement(Tag.NAME);
		name.appendChild(doc.createTextNode(browser.getFamilyName()));
		b.appendChild(name);
		final Element url = doc.createElement(Tag.URL);
		url.appendChild(doc.createCDATASection(browser.getUrl()));
		b.appendChild(url);
		final Element company = doc.createElement(Tag.COMPANY);
		company.appendChild(doc.createCDATASection(browser.getProducer()));
		b.appendChild(company);
		final Element companyUrl = doc.createElement(Tag.URL_COMPANY);
		companyUrl.appendChild(doc.createCDATASection(browser.getProducerUrl()));
		b.appendChild(companyUrl);
		final Element icon = doc.createElement(Tag.ICON);
		icon.appendChild(doc.createTextNode(browser.getIcon()));
		b.appendChild(icon);
		final Element botInfoUrl = doc.createElement(Tag.BROWSER_INFO_URL);
		botInfoUrl.appendChild(doc.createTextNode(browser.getInfoUrl()));
		b.appendChild(botInfoUrl);
		return b;
	}

	private static Element createBrowserOperatingSystemMappings(final Data data, final Document doc) {
		final List<BrowserOperatingSystemMapping> mappings = new ArrayList<BrowserOperatingSystemMapping>(
				data.getBrowserToOperatingSystemMappings());
		Collections.sort(mappings, BrowserOperatingSystemMappingComparator.INSTANCE);

		final Element browserTypesElement = doc.createElement(Tag.BROWSERS_OS);
		for (final BrowserOperatingSystemMapping mapping : mappings) {
			final Element t = doc.createElement(Tag.BROWSER_OS);
			final Element browserId = doc.createElement(Tag.BROWSER_ID);
			browserId.appendChild(doc.createTextNode(String.valueOf(mapping.getBrowserId())));
			t.appendChild(browserId);
			final Element osId = doc.createElement(Tag.OS_ID);
			osId.appendChild(doc.createTextNode(String.valueOf(mapping.getOperatingSystemId())));
			t.appendChild(osId);
			browserTypesElement.appendChild(t);
		}
		return browserTypesElement;
	}

	private static Element createBrowserPatterns(final Data data, final Document doc) {
		final List<BrowserPattern> patterns = new ArrayList<BrowserPattern>(data.getBrowserPatterns().size());
		for (final Entry<Integer, SortedSet<BrowserPattern>> entry : data.getBrowserPatterns().entrySet()) {
			patterns.addAll(entry.getValue());
		}
		Collections.sort(patterns, new OrderedPatternComparator<BrowserPattern>());

		final Element browserTypesElement = doc.createElement(Tag.BROWSERS_REG);
		for (final BrowserPattern pattern : patterns) {
			final Element t = doc.createElement(Tag.BROWSER_REG);
			final Element order = doc.createElement(Tag.ORDER);
			order.appendChild(doc.createTextNode(String.valueOf(pattern.getPosition())));
			t.appendChild(order);
			final Element id = doc.createElement(Tag.BROWSER_ID);
			id.appendChild(doc.createTextNode(String.valueOf(pattern.getId())));
			t.appendChild(id);
			final Element family = doc.createElement(Tag.REGSTRING);
			family.appendChild(doc.createTextNode(RegularExpressionConverter.convertPatternToPerlRegex(pattern.getPattern())));
			t.appendChild(family);
			browserTypesElement.appendChild(t);
		}
		return browserTypesElement;
	}

	private static Element createBrowsers(final Data data, final Document doc) {
		final Element browsersElement = doc.createElement(Tag.BROWSERS);
		final List<Browser> browsers = new ArrayList<Browser>(data.getBrowsers());
		Collections.sort(browsers, IdentifiableComparator.INSTANCE);
		for (final Browser browser : browsers) {
			browsersElement.appendChild(createBrowser(browser, doc));
		}
		return browsersElement;
	}

	private static Element createBrowserTypes(final Data data, final Document doc) {
		final Element browserTypesElement = doc.createElement(Tag.BROWSER_TYPES);
		final List<BrowserType> browserTypes = new ArrayList<BrowserType>(data.getBrowserTypes().values());
		Collections.sort(browserTypes, IdentifiableComparator.INSTANCE);
		for (final BrowserType browserType : browserTypes) {
			final Element t = doc.createElement(Tag.BROWSER_TYPE);
			final Element id = doc.createElement(Tag.ID);
			id.appendChild(doc.createTextNode(String.valueOf(browserType.getId())));
			t.appendChild(id);
			final Element family = doc.createElement(Tag.TYPE);
			family.appendChild(doc.createTextNode(String.valueOf(browserType.getName())));
			t.appendChild(family);
			browserTypesElement.appendChild(t);
		}
		return browserTypesElement;
	}

	private static Element createDescription(@Nonnull final Data data, @Nonnull final Document doc) {
		final Element description = doc.createElement(Tag.DESCRIPTION);
		final Element label = doc.createElement(Tag.LABEL);
		description.appendChild(label).appendChild(
				doc.createTextNode("Data (format xml) for UASparser - http://user-agent-string.info/download/UASparser"));
		final Element version = doc.createElement("version");
		description.appendChild(version).appendChild(doc.createTextNode(data.getVersion()));
		final Element md5Checksum = doc.createElement("checksum");
		md5Checksum.setAttribute(Tag.TYPE, "MD5");
		description.appendChild(md5Checksum).appendChild(
				doc.createTextNode("http://user-agent-string.info/rpc/get_data.php?format=xml&md5=y"));
		final Element shaChecksum = doc.createElement("checksum");
		shaChecksum.setAttribute(Tag.TYPE, "SHA1");
		description.appendChild(shaChecksum).appendChild(
				doc.createTextNode("http://user-agent-string.info/rpc/get_data.php?format=xml&sha1=y"));
		return description;
	}

	private static Element createDevice(final Device device, final Document doc) {
		final Element b = doc.createElement(Tag.DEVICE);
		final Element id = doc.createElement(Tag.ID);
		id.appendChild(doc.createTextNode(String.valueOf(device.getId())));
		b.appendChild(id);
		final Element name = doc.createElement(Tag.NAME);
		name.appendChild(doc.createTextNode(device.getName()));
		b.appendChild(name);
		final Element icon = doc.createElement(Tag.ICON);
		icon.appendChild(doc.createTextNode(device.getIcon()));
		b.appendChild(icon);
		final Element botInfoUrl = doc.createElement(Tag.DEVICE_INFO_URL);
		botInfoUrl.appendChild(doc.createTextNode(device.getInfoUrl()));
		b.appendChild(botInfoUrl);
		return b;
	}

	private static Element createDevicePatterns(final Data data, final Document doc) {
		final List<DevicePattern> patterns = new ArrayList<DevicePattern>(data.getDevicePatterns().size());
		for (final Entry<Integer, SortedSet<DevicePattern>> entry : data.getDevicePatterns().entrySet()) {
			patterns.addAll(entry.getValue());
		}
		Collections.sort(patterns, new OrderedPatternComparator<DevicePattern>());

		final Element deviceTypesElement = doc.createElement(Tag.DEVICES_REG);
		for (final DevicePattern pattern : patterns) {
			final Element t = doc.createElement(Tag.DEVICE_REG);
			final Element order = doc.createElement(Tag.ORDER);
			order.appendChild(doc.createTextNode(String.valueOf(pattern.getPosition())));
			t.appendChild(order);
			final Element id = doc.createElement(Tag.DEVICE_ID);
			id.appendChild(doc.createTextNode(String.valueOf(pattern.getId())));
			t.appendChild(id);
			final Element family = doc.createElement(Tag.REGSTRING);
			family.appendChild(doc.createTextNode(RegularExpressionConverter.convertPatternToPerlRegex(pattern.getPattern())));
			t.appendChild(family);
			deviceTypesElement.appendChild(t);
		}
		return deviceTypesElement;
	}

	private static Element createDevices(final Data data, final Document doc) {
		final Element devicesElement = doc.createElement(Tag.DEVICES);
		final List<Device> devices = new ArrayList<Device>(data.getDevices());
		Collections.sort(devices, IdentifiableComparator.INSTANCE);
		for (final Device device : devices) {
			devicesElement.appendChild(createDevice(device, doc));
		}
		return devicesElement;
	}

	private static Element createOperatingSystem(final OperatingSystem operatingSystem, final Document doc) {
		final Element os = doc.createElement(Tag.OS);
		final Element id = doc.createElement("id");
		id.appendChild(doc.createTextNode(String.valueOf(operatingSystem.getId())));
		os.appendChild(id);
		final Element family = doc.createElement(Tag.FAMILY);
		family.appendChild(doc.createTextNode(operatingSystem.getFamily()));
		os.appendChild(family);
		final Element name = doc.createElement(Tag.NAME);
		name.appendChild(doc.createTextNode(operatingSystem.getName()));
		os.appendChild(name);
		final Element url = doc.createElement(Tag.URL);
		url.appendChild(doc.createCDATASection(operatingSystem.getUrl()));
		os.appendChild(url);
		final Element company = doc.createElement(Tag.COMPANY);
		company.appendChild(doc.createCDATASection(operatingSystem.getProducer()));
		os.appendChild(company);
		final Element companyUrl = doc.createElement(Tag.URL_COMPANY);
		companyUrl.appendChild(doc.createCDATASection(operatingSystem.getProducerUrl()));
		os.appendChild(companyUrl);
		final Element icon = doc.createElement(Tag.ICON);
		icon.appendChild(doc.createTextNode(operatingSystem.getIcon()));
		os.appendChild(icon);
		final Element botInfoUrl = doc.createElement(Tag.OS_INFO_URL);
		botInfoUrl.appendChild(doc.createTextNode(operatingSystem.getInfoUrl()));
		os.appendChild(botInfoUrl);
		return os;
	}

	private static Element createOperatingSystemPatterns(final Data data, final Document doc) {
		final List<OperatingSystemPattern> patterns = new ArrayList<OperatingSystemPattern>(data.getOperatingSystemPatterns().size());
		for (final Entry<Integer, SortedSet<OperatingSystemPattern>> entry : data.getOperatingSystemPatterns().entrySet()) {
			patterns.addAll(entry.getValue());
		}
		Collections.sort(patterns, new OrderedPatternComparator<OperatingSystemPattern>());

		final Element browserTypesElement = doc.createElement(Tag.OPERATING_SYSTEMS_REG);
		for (final OperatingSystemPattern pattern : patterns) {
			final Element t = doc.createElement(Tag.OPERATING_SYSTEM_REG);
			final Element order = doc.createElement(Tag.ORDER);
			order.appendChild(doc.createTextNode(String.valueOf(pattern.getPosition())));
			t.appendChild(order);
			final Element id = doc.createElement(Tag.OS_ID);
			id.appendChild(doc.createTextNode(String.valueOf(pattern.getId())));
			t.appendChild(id);
			final Element family = doc.createElement(Tag.REGSTRING);
			family.appendChild(doc.createTextNode(RegularExpressionConverter.convertPatternToPerlRegex(pattern.getPattern())));
			t.appendChild(family);
			browserTypesElement.appendChild(t);
		}
		return browserTypesElement;
	}

	private static Element createOperatingSystems(final Data data, final Document doc) {
		final Element operatingSystemsElement = doc.createElement(Tag.OPERATING_SYSTEMS);
		final List<OperatingSystem> operatingSystems = new ArrayList<OperatingSystem>(data.getOperatingSystems());
		Collections.sort(operatingSystems, IdentifiableComparator.INSTANCE);
		for (final OperatingSystem operatingSystem : operatingSystems) {
			operatingSystemsElement.appendChild(createOperatingSystem(operatingSystem, doc));
		}
		return operatingSystemsElement;
	}

	private static Element createRobots(final Data data, final Document doc) {
		final Element robotsElement = doc.createElement(Tag.ROBOTS);
		for (final Robot robot : data.getRobots()) {
			robotsElement.appendChild(createRobots(robot, doc));
		}
		return robotsElement;
	}

	private static Element createRobots(final Robot robot, final Document doc) {
		final Element r = doc.createElement(Tag.ROBOT);
		final Element id = doc.createElement(Tag.ID);
		id.appendChild(doc.createTextNode(String.valueOf(robot.getId())));
		r.appendChild(id);
		final Element useragent = doc.createElement(Tag.USERAGENT);
		useragent.appendChild(doc.createCDATASection(robot.getUserAgentString()));
		r.appendChild(useragent);
		final Element family = doc.createElement(Tag.FAMILY);
		family.appendChild(doc.createTextNode(robot.getFamilyName()));
		r.appendChild(family);
		final Element name = doc.createElement(Tag.NAME);
		name.appendChild(doc.createTextNode(robot.getName()));
		r.appendChild(name);
		final Element company = doc.createElement(Tag.COMPANY);
		company.appendChild(doc.createCDATASection(robot.getProducer()));
		r.appendChild(company);
		final Element companyUrl = doc.createElement(Tag.URL_COMPANY);
		companyUrl.appendChild(doc.createCDATASection(robot.getProducerUrl()));
		r.appendChild(companyUrl);
		final Element icon = doc.createElement(Tag.ICON);
		icon.appendChild(doc.createTextNode(robot.getIcon()));
		r.appendChild(icon);
		final Element botInfoUrl = doc.createElement(Tag.BOT_INFO_URL);
		botInfoUrl.appendChild(doc.createTextNode(robot.getInfoUrl()));
		r.appendChild(botInfoUrl);
		return r;
	}

	@Nonnull
	static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
		final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		return docFactory.newDocumentBuilder();
	}

	static void transform(@Nonnull final Source xmlInput, @Nonnull final Result xmlOutput) throws TransformerException {
		Check.notNull(xmlInput, "xmlInput");
		Check.notNull(xmlOutput, "xmlOutput");

		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, INDENT_OPTION);
		transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, SCHEMA_URL);
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", INDENT_AMOUNT);
		transformer.transform(xmlInput, xmlOutput);
	}

	/**
	 * Transforms a given {@code Data} instance into XML and writes it to the passed in {@code OutputStream}.
	 * 
	 * @param data
	 *            {@code Data} to transform into XML
	 * @param outputStream
	 *            output stream to write
	 * @throws ParserConfigurationException
	 *             If a DocumentBuilder cannot be created which satisfies the configuration requested.
	 * @throws TransformerException
	 *             If an unrecoverable error occurs during the course of the transformation.
	 */
	public static void write(@Nonnull final Data data, @Nonnull final OutputStream outputStream) throws ParserConfigurationException,
			TransformerException {
		Check.notNull(data, "data");
		Check.notNull(outputStream, "outputStream");

		final Document doc = newDocumentBuilder().newDocument();

		// root element
		final Element uasdataElement = doc.createElement(Tag.UASDATA);
		doc.appendChild(uasdataElement);

		// description element
		uasdataElement.appendChild(createDescription(data, doc));

		// data element
		final Element dataElement = doc.createElement(Tag.DATA);
		uasdataElement.appendChild(dataElement);

		dataElement.appendChild(createRobots(data, doc));
		dataElement.appendChild(createOperatingSystems(data, doc));
		dataElement.appendChild(createBrowsers(data, doc));
		dataElement.appendChild(createBrowserTypes(data, doc));
		dataElement.appendChild(createBrowserPatterns(data, doc));
		dataElement.appendChild(createBrowserOperatingSystemMappings(data, doc));
		dataElement.appendChild(createOperatingSystemPatterns(data, doc));
		dataElement.appendChild(createDevices(data, doc));
		dataElement.appendChild(createDevicePatterns(data, doc));

		// write the content to output stream
		final DOMSource source = new DOMSource(doc);
		final StreamResult result = new StreamResult(outputStream);
		transform(source, result);
	}

	/**
	 * <strong>Attention:</strong> This class is not intended to create objects from it.
	 */
	private XmlDataWriter() {
		// This class is not intended to create objects from it.
	}

}
