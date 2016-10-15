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
package net.sf.uadetector.datastore;

import java.net.URL;

import net.sf.uadetector.datareader.DataReader;
import net.sf.uadetector.datareader.XmlDataReader;

/**
 * This is the simplest implementation of a {@link RefreshableDataStore}. It initialize the store by reading the
 * <em>UAS data</em> online via {@link DataStore#DEFAULT_DATA_URL} and store it only in the Java heap space.
 * <p>
 * <b>Attentation</b>: During initialization the fallback data store will be used and the remote data will be read in
 * background (non-blocking).
 * 
 * @author André Rouél
 */
public final class OnlineXmlDataStore extends AbstractRefreshableDataStore {

	/**
	 * The default data reader to read in <em>UAS data</em> in XML format
	 */
	private static final DataReader DEFAULT_DATA_READER = new XmlDataReader();

	/**
	 * Constructs an {@code OnlineXmlDataStore} by reading <em>UAS data</em> by the specified default URL
	 * {@link DataStore#DEFAULT_DATA_URL} (in XML format).
	 * 
	 * @param fallback
	 *            <em>UAS data</em> as fallback in case the data on the specified resource can not be read correctly
	 */
	@Deprecated
	public OnlineXmlDataStore(final DataStore fallback) {
		super(DEFAULT_DATA_READER, DEFAULT_DATA_URL, DEFAULT_VERSION_URL, DEFAULT_CHARSET, fallback);
	}

	/**
	 * Constructs an {@code OnlineXmlDataStore} by reading <em>UAS data</em> by the specified URL (in XML format).
	 * @param dataurl
	 * @param versionUrl
	 * @param fallback
	 */
	public OnlineXmlDataStore(final URL dataurl, final URL versionUrl, final DataStore fallback) {
		super(DEFAULT_DATA_READER, dataurl, versionUrl, DEFAULT_CHARSET, fallback);
	}

}
