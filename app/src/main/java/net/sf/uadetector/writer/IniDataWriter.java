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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

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
import net.sf.uadetector.internal.data.domain.Identifiable;
import net.sf.uadetector.internal.data.domain.OperatingSystem;
import net.sf.uadetector.internal.data.domain.OperatingSystemPattern;
import net.sf.uadetector.internal.data.domain.Robot;
import net.sf.uadetector.internal.util.RegularExpressionConverter;

/**
 * This utility is intended to transform an instance of {@code Data} into an <i>UAS data</i> conform XML document and
 * allows us to recreate an <code>uas.xml</code>.
 * 
 * @author André Rouél
 */
@ThreadSafe
public final class IniDataWriter {

	interface Char {
		char EQUALS = '=';
		char NEWLINE = '\n';
		char QUOTE = '"';
		char SEMICOLON = ';';
		char SQUARE_BRACKET_CLOSE = ']';
		char SQUARE_BRACKET_OPEN = '[';
		char WHITESPACE = ' ';
	}

	interface Tag {
		String BROWSER = "browser";
		String BROWSER_OS = "browser_os";
		String BROWSER_REG = "browser_reg";
		String BROWSER_TYPE = "browser_type";
		String DEVICE = "device";
		String DEVICE_REG = "device_reg";
		String OS = "os";
		String OS_REG = "os_reg";
		String ROBOTS = "robots";
	}

	private static final String EMPTY = "";

	private static void createBrowser(final Browser browser, final StringBuilder builder) {
		createKeyValuePair(browser, String.valueOf(browser.getType().getId()), builder);
		createKeyValuePair(browser, browser.getFamilyName(), builder);
		createKeyValuePair(browser, browser.getUrl(), builder);
		createKeyValuePair(browser, browser.getProducer(), builder);
		createKeyValuePair(browser, browser.getProducerUrl(), builder);
		createKeyValuePair(browser, browser.getIcon(), builder);
		createKeyValuePair(browser, browser.getInfoUrl(), builder);
	}

	private static void createBrowserOperatingSystemMappings(final Data data, final StringBuilder builder) {
		final List<BrowserOperatingSystemMapping> mappings = new ArrayList<BrowserOperatingSystemMapping>(
				data.getBrowserToOperatingSystemMappings());
		Collections.sort(mappings, BrowserOperatingSystemMappingComparator.INSTANCE);
		createCategory(Tag.BROWSER_OS, builder);
		createComment("browser_id[] = \"OS id\"", builder);
		for (final BrowserOperatingSystemMapping mapping : mappings) {
			createKeyValuePair(mapping.getBrowserId(), String.valueOf(mapping.getOperatingSystemId()), builder);
		}
	}

	private static void createBrowserPatterns(final Data data, final StringBuilder builder) {
		final List<BrowserPattern> patterns = new ArrayList<BrowserPattern>(data.getBrowserPatterns().size());
		for (final Entry<Integer, SortedSet<BrowserPattern>> entry : data.getBrowserPatterns().entrySet()) {
			patterns.addAll(entry.getValue());
		}
		Collections.sort(patterns, new OrderedPatternComparator<BrowserPattern>());
		createCategory(Tag.BROWSER_REG, builder);
		createComment("browser_reg_id[] = \"Browser regstring\"", builder);
		createComment("browser_reg_id[] = \"Browser id\"", builder);
		for (final BrowserPattern pattern : patterns) {
			final String regex = RegularExpressionConverter.convertPatternToPerlRegex(pattern.getPattern());
			createKeyValuePair(pattern.getPosition(), regex, builder);
			createKeyValuePair(pattern.getPosition(), String.valueOf(pattern.getId()), builder);
		}
	}

	private static void createBrowsers(final Data data, final StringBuilder builder) {
		createCategory(Tag.BROWSER, builder);
		createComment("browser_id[] = \"Browser type\"", builder);
		createComment("browser_id[] = \"Browser Name\"", builder);
		createComment("browser_id[] = \"Browser URL\"", builder);
		createComment("browser_id[] = \"Browser Company\"", builder);
		createComment("browser_id[] = \"Browser Company URL\"", builder);
		createComment("browser_id[] = \"Browser ico\"", builder);
		createComment("browser_id[] = \"Browser info URL\"", builder);
		final List<Browser> browsers = new ArrayList<Browser>(data.getBrowsers());
		Collections.sort(browsers, IdentifiableComparator.INSTANCE);
		for (final Browser browser : browsers) {
			createBrowser(browser, builder);
		}
	}

	private static void createBrowserTypes(final Data data, final StringBuilder builder) {
		createCategory(Tag.BROWSER_TYPE, builder);
		createComment("browser_type_id[] = \"Browser type\"", builder);
		final List<BrowserType> browserTypes = new ArrayList<BrowserType>(data.getBrowserTypes().values());
		Collections.sort(browserTypes, IdentifiableComparator.INSTANCE);
		for (final BrowserType browserType : browserTypes) {
			createKeyValuePair(browserType, browserType.getName(), builder);
		}
	}

	private static void createCategory(@Nonnull final String category, @Nonnull final StringBuilder builder) {
		builder.append(Char.SQUARE_BRACKET_OPEN);
		builder.append(category);
		builder.append(Char.SQUARE_BRACKET_CLOSE);
		builder.append(Char.NEWLINE);
	}

	private static void createComment(@Nonnull final String comment, @Nonnull final StringBuilder builder) {
		builder.append(Char.SEMICOLON);
		builder.append(Char.WHITESPACE);
		builder.append(comment);
		builder.append(Char.NEWLINE);
	}

	private static void createDescription(@Nonnull final Data data, @Nonnull final StringBuilder builder) {
		createComment("Data (format ini) for UASparser - http://user-agent-string.info/download/UASparser", builder);
		createComment("Version: " + data.getVersion(), builder);
		createComment("Checksum:", builder);
		createComment("MD5 - http://user-agent-string.info/rpc/get_data.php?format=ini&md5=y", builder);
		createComment("SHA1 - http://user-agent-string.info/rpc/get_data.php?format=ini&sha1=y", builder);
		builder.append(Char.SEMICOLON);
		builder.append(Char.NEWLINE);
	}

	private static void createDevice(final Device device, final StringBuilder builder) {
		createKeyValuePair(device, device.getName(), builder);
		createKeyValuePair(device, device.getIcon(), builder);
		createKeyValuePair(device, device.getInfoUrl(), builder);
	}

	private static void createDevicePatterns(final Data data, final StringBuilder builder) {
		final List<DevicePattern> patterns = new ArrayList<DevicePattern>(data.getDevicePatterns().size());
		for (final Entry<Integer, SortedSet<DevicePattern>> entry : data.getDevicePatterns().entrySet()) {
			patterns.addAll(entry.getValue());
		}
		Collections.sort(patterns, new OrderedPatternComparator<DevicePattern>());
		createCategory(Tag.DEVICE_REG, builder);
		createComment("device_reg_id[] = \"Device regstring\"", builder);
		createComment("device_reg_id[] = \"Device id\"", builder);
		for (final DevicePattern pattern : patterns) {
			final String regex = RegularExpressionConverter.convertPatternToPerlRegex(pattern.getPattern());
			createKeyValuePair(pattern.getPosition(), regex, builder);
			createKeyValuePair(pattern.getPosition(), String.valueOf(pattern.getId()), builder);
		}
	}

	private static void createDevices(final Data data, final StringBuilder builder) {
		createCategory(Tag.DEVICE, builder);
		createComment("device_id[] = \"Device type\"", builder);
		createComment("device_id[] = \"Device ico\"", builder);
		createComment("device_id[] = \"Device info URL\"", builder);
		final List<Device> devices = new ArrayList<Device>(data.getDevices());
		Collections.sort(devices, IdentifiableComparator.INSTANCE);
		for (final Device device : devices) {
			createDevice(device, builder);
		}
	}

	private static void createKeyValuePair(@Nonnull final Identifiable identifiable, @Nonnull final String value,
			@Nonnull final StringBuilder builder) {
		createKeyValuePair(identifiable.getId(), value, builder);
	}

	private static void createKeyValuePair(@Nonnull final int id, @Nonnull final String value, @Nonnull final StringBuilder builder) {
		builder.append(id);
		builder.append(Char.SQUARE_BRACKET_OPEN);
		builder.append(Char.SQUARE_BRACKET_CLOSE);
		builder.append(Char.WHITESPACE);
		builder.append(Char.EQUALS);
		builder.append(Char.WHITESPACE);
		builder.append(Char.QUOTE);
		builder.append(value);
		builder.append(Char.QUOTE);
		builder.append(Char.NEWLINE);
	}

	private static void createOperatingSystem(final OperatingSystem operatingSystem, final StringBuilder builder) {
		createKeyValuePair(operatingSystem, operatingSystem.getFamily(), builder);
		createKeyValuePair(operatingSystem, operatingSystem.getName(), builder);
		createKeyValuePair(operatingSystem, operatingSystem.getUrl(), builder);
		createKeyValuePair(operatingSystem, operatingSystem.getProducer(), builder);
		createKeyValuePair(operatingSystem, operatingSystem.getProducerUrl(), builder);
		createKeyValuePair(operatingSystem, operatingSystem.getIcon(), builder);
	}

	private static void createOperatingSystemPatterns(final Data data, final StringBuilder builder) {
		createCategory(Tag.OS_REG, builder);
		createComment("os_reg_id[] = \"OS regstring\"", builder);
		createComment("os_reg_id[] = \"OS id\"", builder);
		final List<OperatingSystemPattern> patterns = new ArrayList<OperatingSystemPattern>(data.getOperatingSystemPatterns().size());
		for (final Entry<Integer, SortedSet<OperatingSystemPattern>> entry : data.getOperatingSystemPatterns().entrySet()) {
			patterns.addAll(entry.getValue());
		}
		Collections.sort(patterns, new OrderedPatternComparator<OperatingSystemPattern>());

		for (final OperatingSystemPattern pattern : patterns) {
			final String regex = RegularExpressionConverter.convertPatternToPerlRegex(pattern.getPattern());
			createKeyValuePair(pattern.getPosition(), regex, builder);
			createKeyValuePair(pattern.getPosition(), String.valueOf(pattern.getId()), builder);
		}
	}

	private static void createOperatingSystems(final Data data, final StringBuilder builder) {
		createCategory(Tag.OS, builder);
		createComment("os_id[] = \"OS Family\"", builder);
		createComment("os_id[] = \"OS Name\"", builder);
		createComment("os_id[] = \"OS URL\"", builder);
		createComment("os_id[] = \"OS Company\"", builder);
		createComment("os_id[] = \"OS Company URL\"", builder);
		createComment("os_id[] = \"OS ico\"", builder);
		final List<OperatingSystem> operatingSystems = new ArrayList<OperatingSystem>(data.getOperatingSystems());
		Collections.sort(operatingSystems, IdentifiableComparator.INSTANCE);
		for (final OperatingSystem operatingSystem : operatingSystems) {
			createOperatingSystem(operatingSystem, builder);
		}
	}

	private static void createRobot(final Robot robot, final StringBuilder builder) {
		createKeyValuePair(robot, robot.getUserAgentString(), builder);
		createKeyValuePair(robot, robot.getFamilyName(), builder);
		createKeyValuePair(robot, robot.getName(), builder);
		createKeyValuePair(robot, EMPTY, builder);
		createKeyValuePair(robot, robot.getProducer(), builder);
		createKeyValuePair(robot, robot.getProducerUrl(), builder);
		createKeyValuePair(robot, robot.getIcon(), builder);
		createKeyValuePair(robot, EMPTY, builder);
		createKeyValuePair(robot, robot.getInfoUrl(), builder);
	}

	private static void createRobots(final Data data, final StringBuilder builder) {
		createCategory(Tag.ROBOTS, builder);
		createComment("bot_id[] = \"bot useragentstring\"", builder);
		createComment("bot_id[] = \"bot Family\"", builder);
		createComment("bot_id[] = \"bot Name\"", builder);
		createComment("bot_id[] = \"bot URL\"", builder);
		createComment("bot_id[] = \"bot Company\"", builder);
		createComment("bot_id[] = \"bot Company URL\"", builder);
		createComment("bot_id[] = \"bot ico\"", builder);
		createComment("bot_id[] = \"bot OS id\"", builder);
		createComment("bot_id[] = \"bot info URL\"", builder);
		for (final Robot robot : data.getRobots()) {
			createRobot(robot, builder);
		}
	}

	/**
	 * Transforms a given {@code Data} instance into XML and writes it to the passed in {@code OutputStream}.
	 * 
	 * @param data
	 *            {@code Data} to transform into XML
	 * @param outputStream
	 *            output stream to write
	 * @throws IOException
	 *             if the given output stream can not be written
	 */
	public static void write(@Nonnull final Data data, @Nonnull final OutputStream outputStream) throws IOException {
		Check.notNull(data, "data");
		Check.notNull(outputStream, "outputStream");

		final StringBuilder doc = new StringBuilder(10000);

		// description element
		createDescription(data, doc);

		// data
		createRobots(data, doc);
		createOperatingSystems(data, doc);
		createBrowsers(data, doc);
		createBrowserTypes(data, doc);
		createBrowserPatterns(data, doc);
		createBrowserOperatingSystemMappings(data, doc);
		createOperatingSystemPatterns(data, doc);
		createDevices(data, doc);
		createDevicePatterns(data, doc);

		// write the content to output stream
		outputStream.write(doc.toString().getBytes("UTF-8"));
	}

	/**
	 * <strong>Attention:</strong> This class is not intended to create objects from it.
	 */
	private IniDataWriter() {
		// This class is not intended to create objects from it.
	}

}
