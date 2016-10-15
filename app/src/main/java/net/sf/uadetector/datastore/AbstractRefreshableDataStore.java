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
import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import net.sf.qualitycheck.Check;
import net.sf.qualitycheck.exception.IllegalStateOfArgumentException;
import net.sf.uadetector.datareader.DataReader;
import net.sf.uadetector.exception.CanNotOpenStreamException;
import net.sf.uadetector.internal.data.Data;
import net.sf.uadetector.internal.util.UrlUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This abstract implementation provides basic update functionality to be able to update the <em>UAS data</em> in your
 * data store. During initialization the passed in fallback data store will be used and an update process will be
 * triggered. The update operation itself works as background task to avoid blocking (for example when reading data by a
 * network connection and the remote host is not available or really slow).
 * <p>
 * Such a store must always have an usable instance of {@link Data} and should be initialized with the supplied UAS file
 * in the <em>uadetector-resources</em> module.
 * 
 * @author André Rouél
 */
public abstract class AbstractRefreshableDataStore implements RefreshableDataStore {

	/**
	 * Corresponding default logger for this class
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractRefreshableDataStore.class);

	/**
	 * Runtime check that the passed instance of {@link Data} is not empty (respectively {@link Data#EMPTY}).
	 * 
	 * @param data
	 *            instance of {@code Data}
	 * @throws net.sf.qualitycheck.exception.IllegalStateOfArgumentException
	 *             if the passed instance is empty
	 */
	private static Data checkData(final Data data) {
		if (Data.EMPTY.equals(data)) {
			throw new IllegalStateOfArgumentException("Argument 'data' must not be empty.");
		}
		return data;
	}

	/**
	 * Current the character set in which the <em>UAS data</em> will be read
	 */
	private final Charset charset;

	/**
	 * Current <em>UAS data</em>
	 */
	private Data data;

	/**
	 * The {@code URL} to get <em>UAS data</em>
	 */
	private final URL dataUrl;

	/**
	 * Default data store which will be used during initialization of a {@code DataStore} and can be used in emergency
	 * cases.
	 */
	private final DataStore fallback;

	/**
	 * The data reader to read in <em>UAS data</em>
	 */
	private final DataReader reader;

	/**
	 * Update operation which runs itself in background (non-blocking)
	 */
	private UpdateOperation updateOperation = new UpdateOperationTask(this);

	/**
	 * The {@code URL} to get the latest version information of <em>UAS data</em>
	 */
	private final URL versionUrl;

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
	 * @param fallback
	 *            <em>UAS data</em> as fallback in case the data on the specified resource can not be read correctly
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if one of the given arguments is {@code null}
	 * @throws net.sf.qualitycheck.exception.IllegalStateOfArgumentException
	 *             if the given strings are not valid URLs
	 */
	protected AbstractRefreshableDataStore(@Nonnull final DataReader reader, @Nonnull final String dataUrl,
			@Nonnull final String versionUrl, @Nonnull final Charset charset, @Nonnull final DataStore fallback) {
		this(reader, UrlUtil.build(dataUrl), UrlUtil.build(versionUrl), charset, fallback);
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
	 * @param fallback
	 *            <em>UAS data</em> as fallback in case the data on the specified resource can not be read correctly
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if the given argument is {@code null}
	 * @throws net.sf.qualitycheck.exception.IllegalStateOfArgumentException
	 *             if the created instance of {@link Data} is empty
	 */
	protected AbstractRefreshableDataStore(@Nonnull final DataReader reader, @Nonnull final URL dataUrl, @Nonnull final URL versionUrl,
			@Nonnull final Charset charset, final DataStore fallback) {
		Check.notNull(reader, "reader");
		Check.notNull(charset, "charset");
		Check.notNull(dataUrl, "dataUrl");
		Check.notNull(versionUrl, "versionUrl");
		Check.notNull(fallback, "fallback");

		this.reader = reader;
		this.dataUrl = dataUrl;
		this.versionUrl = versionUrl;
		this.charset = charset;
		this.fallback = fallback;

		data = checkData(fallback.getData());
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
	public DataStore getFallback() {
		return fallback;
	}

	@Override
	public UpdateOperation getUpdateOperation() {
		return updateOperation;
	}

	@Override
	public URL getVersionUrl() {
		return versionUrl;
	}

	/**
	 * Triggers the update of the <code>DataStore</code>. When this action is executed, the current data URL will be
	 * read in and the <code>DataReader</code> parses and builds a new Data instance. Finally, the currently set
	 * <code>Data</code> reference will be replaced by the new one.
	 * <p>
	 * <b>Attention</b>: This method is implemented as background task. You can not assume that you immediately get an
	 * updated data store.
	 */
	@Override
	public void refresh() {
		try {
			updateOperation.run();
		} catch (final CanNotOpenStreamException e) {
			LOG.warn(String.format(MSG_URL_NOT_READABLE, e.getLocalizedMessage()));
		} catch (final IllegalArgumentException e) {
			LOG.warn(MSG_FAULTY_CONTENT + " " + e.getLocalizedMessage());
		} catch (final RuntimeException e) {
			LOG.warn(MSG_FAULTY_CONTENT, e);
		}
	}

	/**
	 * Sets new <em>UAS data</em> in the store.
	 * 
	 * @param data
	 *            <em>UAS data</em> to override the current ({@code null} is not allowed)
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if the given argument is {@code null}
	 * @throws net.sf.qualitycheck.exception.IllegalStateOfArgumentException
	 *             if the given instance of {@code Data} is empty
	 */
	protected void setData(@Nonnull final Data data) {
		Check.notNull(data, "data");

		this.data = checkData(data);

		// add some useful UAS data informations to the log
		if (LOG.isDebugEnabled()) {
			LOG.debug(data.toStats());
		}
	}

	/**
	 * Sets a new update operation.
	 * 
	 * @param updateOperation
	 *            operation to override the current ({@code null} is not allowed)
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if the given argument is {@code null}
	 */
	protected void setUpdateOperation(@Nonnull final UpdateOperation updateOperation) {
		Check.notNull(updateOperation, "updateOperation");
		this.updateOperation = updateOperation;
	}

}
