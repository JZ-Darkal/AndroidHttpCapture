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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

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

/**
 * This class represents the detection information of <i>UADetector</i>.
 * <p>
 * An instance of {@code Data} is immutable, their values cannot be changed after creation.
 * 
 * @author André Rouél
 */
@Immutable
public class Data implements Serializable {

	/**
	 * An <i>immutable</i> empty {@code Data} object.
	 */
	public static final Data EMPTY = new Data(new HashSet<Browser>(0), new HashMap<Integer, SortedSet<BrowserPattern>>(0),
			new HashMap<Integer, BrowserType>(0), new TreeMap<BrowserPattern, Browser>(), new HashSet<BrowserOperatingSystemMapping>(0),
			new HashSet<OperatingSystem>(0), new HashMap<Integer, SortedSet<OperatingSystemPattern>>(0),
			new TreeMap<OperatingSystemPattern, OperatingSystem>(), new ArrayList<Robot>(0), new HashSet<Device>(0),
			new HashMap<Integer, SortedSet<DevicePattern>>(0), new TreeMap<DevicePattern, Device>(), "");

	private static final long serialVersionUID = 8522012551928801089L;

	@Nonnull
	private final Map<Integer, SortedSet<BrowserPattern>> browserPatterns;

	@Nonnull
	private final Set<Browser> browsers;

	@Nonnull
	private final Map<Integer, SortedSet<DevicePattern>> devicePatterns;

	@Nonnull
	private final Set<Device> devices;

	@Nonnull
	private final SortedMap<DevicePattern, Device> patternToDeviceMap;

	@Nonnull
	private final Set<BrowserOperatingSystemMapping> browserToOperatingSystemMappings;

	@Nonnull
	private final Map<Integer, BrowserType> browserTypes;

	@Nonnull
	private final Map<Integer, SortedSet<OperatingSystemPattern>> operatingSystemPatterns;

	@Nonnull
	private final Set<OperatingSystem> operatingSystems;

	@Nonnull
	private final SortedMap<BrowserPattern, Browser> patternToBrowserMap;

	@Nonnull
	private final SortedMap<OperatingSystemPattern, OperatingSystem> patternToOperatingSystemMap;

	@Nonnull
	private final List<Robot> robots;

	/**
	 * Version information of the UAS data
	 */
	@Nonnull
	private final String version;

	public Data(@Nonnull final Set<Browser> browsers, @Nonnull final Map<Integer, SortedSet<BrowserPattern>> browserPatterns,
			@Nonnull final Map<Integer, BrowserType> browserTypes, @Nonnull final SortedMap<BrowserPattern, Browser> patternToBrowserMap,
			@Nonnull final Set<BrowserOperatingSystemMapping> browserToOperatingSystemMappings,
			@Nonnull final Set<OperatingSystem> operatingSystems,
			@Nonnull final Map<Integer, SortedSet<OperatingSystemPattern>> operatingSystemPatterns,
			@Nonnull final SortedMap<OperatingSystemPattern, OperatingSystem> patternToOperatingSystemMap,
			@Nonnull final List<Robot> robots, @Nonnull final Set<Device> devices,
			@Nonnull final Map<Integer, SortedSet<DevicePattern>> devicePatterns,
			@Nonnull final SortedMap<DevicePattern, Device> patternToDeviceMap, @Nonnull final String version) {
		Check.notNull(browsers, "browsers");
		Check.notNull(browserPatterns, "browserPatterns");
		Check.notNull(browserTypes, "browserTypes");
		Check.notNull(patternToBrowserMap, "patternToBrowserMap");
		Check.notNull(browserToOperatingSystemMappings, "browserToOperatingSystemMap");
		Check.notNull(operatingSystems, "operatingSystems");
		Check.notNull(operatingSystemPatterns, "operatingSystemPatterns");
		Check.notNull(patternToOperatingSystemMap, "patternToOperatingSystemMap");
		Check.notNull(robots, "robots");
		Check.notNull(devices, "devices");
		Check.notNull(devicePatterns, "devicePatterns");
		Check.notNull(patternToDeviceMap, "patternToDeviceMap");
		Check.notNull(version, "version");

		this.browsers = Collections.unmodifiableSet(new HashSet<Browser>(browsers));
		this.browserPatterns = Collections.unmodifiableMap(new HashMap<Integer, SortedSet<BrowserPattern>>(browserPatterns));
		this.browserTypes = Collections.unmodifiableMap(new HashMap<Integer, BrowserType>(Check.notNull(browserTypes, "browserTypes")));
		this.patternToBrowserMap = Collections.unmodifiableSortedMap(new TreeMap<BrowserPattern, Browser>(patternToBrowserMap));
		this.browserToOperatingSystemMappings = Collections.unmodifiableSet(new HashSet<BrowserOperatingSystemMapping>(
				browserToOperatingSystemMappings));
		this.operatingSystems = Collections.unmodifiableSet(new HashSet<OperatingSystem>(operatingSystems));
		this.operatingSystemPatterns = Collections.unmodifiableMap(new HashMap<Integer, SortedSet<OperatingSystemPattern>>(
				operatingSystemPatterns));
		this.patternToOperatingSystemMap = Collections.unmodifiableSortedMap(new TreeMap<OperatingSystemPattern, OperatingSystem>(
				patternToOperatingSystemMap));
		this.robots = Collections.unmodifiableList(new ArrayList<Robot>(robots));
		this.devices = Collections.unmodifiableSet(new HashSet<Device>(devices));
		this.devicePatterns = Collections.unmodifiableMap(new HashMap<Integer, SortedSet<DevicePattern>>(devicePatterns));
		this.patternToDeviceMap = Collections.unmodifiableSortedMap(new TreeMap<DevicePattern, Device>(patternToDeviceMap));
		this.version = Check.notNull(version, "version");
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
		final Data other = (Data) obj;
		if (!browsers.equals(other.browsers)) {
			return false;
		}
		if (!browserPatterns.equals(other.browserPatterns)) {
			return false;
		}
		if (!browserTypes.equals(other.browserTypes)) {
			return false;
		}
		if (!patternToBrowserMap.equals(other.patternToBrowserMap)) {
			return false;
		}
		if (!browserToOperatingSystemMappings.equals(other.browserToOperatingSystemMappings)) {
			return false;
		}
		if (!operatingSystems.equals(other.operatingSystems)) {
			return false;
		}
		if (!operatingSystemPatterns.equals(other.operatingSystemPatterns)) {
			return false;
		}
		if (!patternToOperatingSystemMap.equals(other.patternToOperatingSystemMap)) {
			return false;
		}
		if (!robots.equals(other.robots)) {
			return false;
		}
		if (!devices.equals(other.devices)) {
			return false;
		}
		if (!devicePatterns.equals(other.devicePatterns)) {
			return false;
		}
		if (!patternToDeviceMap.equals(other.patternToDeviceMap)) {
			return false;
		}
		if (!version.equals(other.version)) {
			return false;
		}
		return true;
	}

	@Nonnull
	public Map<Integer, SortedSet<BrowserPattern>> getBrowserPatterns() {
		return browserPatterns;
	}

	@Nonnull
	public Set<Browser> getBrowsers() {
		return browsers;
	}

	@Nonnull
	public Set<BrowserOperatingSystemMapping> getBrowserToOperatingSystemMappings() {
		return browserToOperatingSystemMappings;
	}

	@Nonnull
	public Map<Integer, BrowserType> getBrowserTypes() {
		return browserTypes;
	}

	@Nonnull
	public Map<Integer, SortedSet<DevicePattern>> getDevicePatterns() {
		return devicePatterns;
	}

	@Nonnull
	public Set<Device> getDevices() {
		return devices;
	}

	@Nonnull
	public Map<Integer, SortedSet<OperatingSystemPattern>> getOperatingSystemPatterns() {
		return operatingSystemPatterns;
	}

	@Nonnull
	public Set<OperatingSystem> getOperatingSystems() {
		return operatingSystems;
	}

	@Nonnull
	public SortedMap<BrowserPattern, Browser> getPatternToBrowserMap() {
		return patternToBrowserMap;
	}

	@Nonnull
	public SortedMap<DevicePattern, Device> getPatternToDeviceMap() {
		return patternToDeviceMap;
	}

	@Nonnull
	public SortedMap<OperatingSystemPattern, OperatingSystem> getPatternToOperatingSystemMap() {
		return patternToOperatingSystemMap;
	}

	@Nonnull
	public List<Robot> getRobots() {
		return robots;
	}

	/**
	 * Gets the version of the UAS data which are available within this instance.
	 * 
	 * @return version of UAS data
	 */
	@Nonnull
	public String getVersion() {
		return version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + browsers.hashCode();
		result = prime * result + browserPatterns.hashCode();
		result = prime * result + browserTypes.hashCode();
		result = prime * result + patternToBrowserMap.hashCode();
		result = prime * result + browserToOperatingSystemMappings.hashCode();
		result = prime * result + operatingSystems.hashCode();
		result = prime * result + operatingSystemPatterns.hashCode();
		result = prime * result + patternToOperatingSystemMap.hashCode();
		result = prime * result + robots.hashCode();
		result = prime * result + devices.hashCode();
		result = prime * result + devicePatterns.hashCode();
		result = prime * result + patternToDeviceMap.hashCode();
		result = prime * result + version.hashCode();
		return result;
	}

	@Nonnull
	public String toStats() {
		final StringBuilder builder = new StringBuilder();
		builder.append("UAS data stats\n");
		builder.append("----------------------------------------------------------------");
		builder.append('\n');
		builder.append("version:\t\t");
		builder.append(version);
		builder.append('\n');
		builder.append("browser:\t\t");
		builder.append(browsers.size());
		builder.append('\n');
		final Map<String, AtomicInteger> browserByType = new HashMap<String, AtomicInteger>();
		for (final Browser browser : browsers) {
			final AtomicInteger counter = browserByType.get(browser.getType().getName());
			if (counter == null) {
				browserByType.put(browser.getType().getName(), new AtomicInteger(1));
			} else {
				counter.incrementAndGet();
			}
		}
		for (final Entry<String, AtomicInteger> entry : browserByType.entrySet()) {
			builder.append('\t');
			builder.append('\t');
			builder.append('\t');
			builder.append(entry.getKey());
			builder.append(":\t");
			builder.append(entry.getValue().get());
			builder.append('\n');
		}
		builder.append("browser patterns:\t");
		builder.append(patternToBrowserMap.size());
		builder.append('\n');
		builder.append("operating systems:\t");
		builder.append(operatingSystems.size());
		builder.append('\n');
		builder.append("os patterns:\t\t");
		builder.append(patternToOperatingSystemMap.size());
		builder.append('\n');
		builder.append("robots:\t\t\t");
		builder.append(robots.size());
		builder.append('\n');
		builder.append("devices:\t");
		builder.append(devices.size());
		builder.append('\n');
		builder.append("device patterns:\t");
		builder.append(patternToDeviceMap.size());
		builder.append('\n');
		builder.append("----------------------------------------------------------------");
		return builder.toString();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Data [browsers=");
		builder.append(browsers);
		builder.append(", browserPatterns=");
		builder.append(browserPatterns);
		builder.append(", browserTypes=");
		builder.append(browserTypes);
		builder.append(", patternToBrowserMap=");
		builder.append(patternToBrowserMap);
		builder.append(", browserToOperatingSystemMap=");
		builder.append(browserToOperatingSystemMappings);
		builder.append(", operatingSystems=");
		builder.append(operatingSystems);
		builder.append(", operatingSystemPatterns=");
		builder.append(operatingSystemPatterns);
		builder.append(", patternToOperatingSystemMap=");
		builder.append(patternToOperatingSystemMap);
		builder.append(", robots=");
		builder.append(robots);
		builder.append(", devices=");
		builder.append(devices);
		builder.append(", devicePatterns=");
		builder.append(devicePatterns);
		builder.append(", patternToDeviceMap=");
		builder.append(patternToDeviceMap);
		builder.append(", version=");
		builder.append(version);
		builder.append("]");
		return builder.toString();
	}

}
