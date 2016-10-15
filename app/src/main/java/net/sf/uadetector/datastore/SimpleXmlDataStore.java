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
package net.sf.uadetector.datastore;

import java.net.URL;

import net.sf.uadetector.datareader.DataReader;
import net.sf.uadetector.datareader.XmlDataReader;

/**
 * This is the simplest implementation of a {@link DataStore}. It initialize the store by reading the <em>UAS data</em>
 * via the passed URL and store it only in the Java heap space.
 * 
 * <p>
 * The given resource must have a valid XML format with UTF-8 charset that validates against specified schema under <a
 * href="http://data.udger.com/uasxmldata_old.dtd">http://data.udger.com/uasxmldata_old.dtd</a>.
 * 
 * @author André Rouél
 */
public final class SimpleXmlDataStore extends AbstractDataStore implements DataStore {

	/**
	 * The default data reader to read in <em>UAS data</em> in XML format
	 */
	private static final DataReader DEFAULT_DATA_READER = new XmlDataReader();

	/**
	 * Constructs an {@code SimpleXmlDataStore} by reading <em>UAS data</em> by the specified default URL
	 * {@link DataStore#DEFAULT_DATA_URL} (in XML format).
	 * 
	 * @param dataUrl
	 *            URL to <em>UAS data</em>
	 * @param versionUrl
	 *            URL to version information about the given <em>UAS data</em>
	 */
	public SimpleXmlDataStore(final URL dataUrl, final URL versionUrl) {
		super(DEFAULT_DATA_READER, dataUrl, versionUrl, DEFAULT_CHARSET);
	}

}
