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
import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import net.sf.qualitycheck.Check;
import net.sf.uadetector.datareader.DataReader;
import net.sf.uadetector.internal.data.Data;
import net.sf.uadetector.internal.util.UrlUtil;

/**
 * The abstract implementation to store <em>UAS data</em> only in the heap space.<br>
 * <br>
 * A store must always have an usable instance of {@link Data}. It is recommended to initialize it with the supplied UAS
 * file in the <em>uadetector-resources</em> module.
 * 
 * @author André Rouél
 */
public abstract class AbstractDataStore implements DataStore {

	/**
	 * Runtime check that the passed instance of {@link Data} is not empty (respectively {@link Data#EMPTY}).
	 * 
	 * @param data
	 *            instance of {@code Data}
	 * @throws IllegalStateException
	 *             if the passed instance is empty
	 */
	private static Data checkData(final Data data) {
		if (Data.EMPTY.equals(data)) {
			throw new IllegalStateException("Argument 'data' must not be empty.");
		}
		return data;
	}

	/**
	 * This method reads the given {@link URL} by using an {@link DataReader}. The new created instance of {@link Data}
	 * will be returned.
	 * 
	 * @param reader
	 *            data reader to read the given {@code dataUrl}
	 * @param url
	 *            URL to <em>UAS data</em>
	 * @param charset
	 *            the character set in which the data should be read
	 * @return an instance of {@code Data} or {@link Data#EMPTY} if an error occurred, but never {@code null}
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if the given argument is {@code null}
	 */
	protected static final Data readData(@Nonnull final DataReader reader, @Nonnull final URL url, @Nonnull final Charset charset) {
		Check.notNull(reader, "reader");
		Check.notNull(url, "url");
		Check.notNull(charset, "charset");

		return reader.read(url, charset);
	}

	/**
	 * Current the character set in which the <em>UAS data</em> will be read
	 */
	private final Charset charset;

	/**
	 * Current <em>UAS data</em>
	 */
	private final Data data;

	/**
	 * The {@code URL} to get <em>UAS data</em>
	 */
	private final URL dataUrl;

	/**
	 * The data reader to read in <em>UAS data</em>
	 */
	private final DataReader reader;

	/**
	 * The {@code URL} to get the latest version information of <em>UAS data</em>
	 */
	private final URL versionUrl;

	/**
	 * Constructs an new instance of {@link AbstractDataStore}.
	 * 
	 * @param data
	 *            first <em>UAS data</em> which will be available in the store
	 * @param reader
	 *            data reader to read the given {@code dataUrl}
	 * @param dataUrl
	 *            URL to <em>UAS data</em>
	 * @param versionUrl
	 *            URL to version information about the given <em>UAS data</em>
	 * @param charset
	 *            the character set in which the data should be read
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if one of the given arguments is {@code null}
	 */
	protected AbstractDataStore(@Nonnull final Data data, @Nonnull final DataReader reader, @Nonnull final URL dataUrl,
			@Nonnull final URL versionUrl, @Nonnull final Charset charset) {
		Check.notNull(data, "data");
		Check.notNull(reader, "reader");
		Check.notNull(charset, "charset");
		Check.notNull(dataUrl, "dataUrl");
		Check.notNull(versionUrl, "versionUrl");

		this.data = checkData(data);
		this.reader = reader;
		this.dataUrl = dataUrl;
		this.versionUrl = versionUrl;
		this.charset = charset;
	}

	/**
	 * Constructs an {@code AbstractDataStore} by reading the given {@code dataUrl} as <em>UAS data</em>.
	 * 
	 * @param reader
	 *            data reader to read the given {@code dataUrl}
	 * @param dataUrl
	 *            URL to <em>UAS data</em>
	 * @param versionUrl
	 *            URL to version information about the given <em>UAS data</em>
	 * @param charset
	 *            the character set in which the data should be read
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if one of the given arguments is {@code null}
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if the given strings are not valid URLs
	 */
	protected AbstractDataStore(final DataReader reader, final String dataUrl, final String versionUrl, final Charset charset) {
		this(reader, UrlUtil.build(dataUrl), UrlUtil.build(versionUrl), charset);
	}

	/**
	 * Constructs an {@code AbstractDataStore} by reading the given {@code dataUrl} as <em>UAS data</em>.
	 * 
	 * @param reader
	 *            data reader to read the given {@code dataUrl}
	 * @param dataUrl
	 *            URL to <em>UAS data</em>
	 * @param versionUrl
	 *            URL to version information about the given <em>UAS data</em>
	 * @param charset
	 *            the character set in which the data should be read
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if the given argument is {@code null}
	 * @throws net.sf.qualitycheck.exception.IllegalStateOfArgumentException
	 *             if the created instance of {@link Data} is empty
	 */
	protected AbstractDataStore(final DataReader reader, final URL dataUrl, final URL versionUrl, final Charset charset) {
		this(checkData(readData(reader, dataUrl, charset)), reader, dataUrl, versionUrl, charset);
	}

	@Override
	public Charset getCharset() {
		return charset;
	}

	@Override
	public Data getData() {
		return data;
	}

	@Override
	public DataReader getDataReader() {
		return reader;
	}

	@Override
	public URL getDataUrl() {
		return dataUrl;
	}

	@Override
	public URL getVersionUrl() {
		return versionUrl;
	}

}
