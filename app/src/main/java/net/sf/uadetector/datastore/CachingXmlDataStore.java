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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import net.sf.qualitycheck.Check;
import net.sf.qualitycheck.exception.IllegalStateOfArgumentException;
import net.sf.uadetector.datareader.DataReader;
import net.sf.uadetector.datareader.XmlDataReader;
import net.sf.uadetector.internal.data.Data;
import net.sf.uadetector.internal.util.FileUtil;
import net.sf.uadetector.internal.util.UrlUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a {@link DataStore} which is able to recover <em>UAS data</em> in XML format from a cache file. If
 * the cache file is empty, the data will be read from the given data URL.<br>
 * <br>
 * You can also update the data of the store at any time if you trigger {@link CachingXmlDataStore#refresh()}.
 * 
 * @author André Rouél
 */
public final class CachingXmlDataStore extends AbstractRefreshableDataStore {

	/**
	 * Internal data store which will be used to load previously saved <em>UAS data</em> from a cache file.
	 */
	private static class CacheFileDataStore extends AbstractDataStore {
		protected CacheFileDataStore(final Data data, final DataReader reader, final URL dataUrl, final Charset charset) {
			super(data, reader, dataUrl, dataUrl, charset);
		}
	}

	/**
	 * The default temporary-file directory
	 */
	private static final String CACHE_DIR = System.getProperty("java.io.tmpdir");

	/**
	 * Corresponding default logger of this class
	 */
	private static final Logger LOG = LoggerFactory.getLogger(CachingXmlDataStore.class);

	/**
	 * Message for the log if the cache file is filled
	 */
	private static final String MSG_CACHE_FILE_IS_EMPTY = "The cache file is empty. The given UAS data source will be imported.";

	/**
	 * Message for the log if the cache file is empty
	 */
	private static final String MSG_CACHE_FILE_IS_FILLED = "The cache file is filled and will be imported.";

	/**
	 * Message if the cache file contains unexpected data and must be deleted manually
	 */
	private static final String MSG_CACHE_FILE_IS_DAMAGED = "The cache file '%s' is damaged and must be removed manually.";

	/**
	 * Message if the cache file contains unexpected data and has been removed
	 */
	private static final String MSG_CACHE_FILE_IS_DAMAGED_AND_DELETED = "The cache file '%s' is damaged and has been deleted.";

	/**
	 * The prefix string to be used in generating the cache file's name; must be at least three characters long
	 */
	private static final String PREFIX = "uas";

	/**
	 * The suffix string to be used in generating the cache file's name; may be {@code null}, in which case the suffix "
	 * {@code .tmp}" will be used
	 */
	private static final String SUFFIX = ".xml";

	/**
	 * Constructs a new instance of {@code CachingXmlDataStore} with the given arguments. The given {@code cacheFile}
	 * can be empty or filled with previously cached data in XML format. The file must be writable otherwise an
	 * exception will be thrown.
	 * 
	 * @param dataUrl
	 * 			  URL for online version of <em>UAS data</em>
	 * @param versionURL
	 *            URL for version information of online <em>UAS data</em>
	 * @param fallback
	 *            <em>UAS data</em> as fallback in case the data on the specified resource can not be read correctly
	 * @return new instance of {@link CachingXmlDataStore}
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if one of the given arguments is {@code null}
	 * @throws net.sf.qualitycheck.exception.IllegalStateOfArgumentException
	 *             if the given cache file can not be read
	 * @throws net.sf.qualitycheck.exception.IllegalStateOfArgumentException
	 *             if no URL can be resolved to the given given file
	 */
	@Nonnull
	public static CachingXmlDataStore createCachingXmlDataStore(@Nonnull final URL dataUrl, @Nonnull final URL versionURL, @Nonnull final DataStore fallback) {
		return createCachingXmlDataStore(findOrCreateCacheFile(), dataUrl, versionURL, DEFAULT_CHARSET,
				fallback);
	}

	@Deprecated
	public static CachingXmlDataStore createCachingXmlDataStore(@Nonnull final DataStore fallback) {
		return createCachingXmlDataStore(findOrCreateCacheFile(), fallback);
	}

	/**
	 * Constructs a new instance of {@code CachingXmlDataStore} with the given arguments. The given {@code cacheFile}
	 * can be empty or filled with previously cached data in XML format. The file must be writable otherwise an
	 * exception will be thrown.
	 * 
	 * @param cacheFile
	 *            file with cached <em>UAS data</em> in XML format or empty file
	 * @param fallback
	 *            <em>UAS data</em> as fallback in case the data on the specified resource can not be read correctly
	 * @return new instance of {@link CachingXmlDataStore}
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if one of the given arguments is {@code null}
	 * @throws net.sf.qualitycheck.exception.IllegalStateOfArgumentException
	 *             if the given cache file can not be read
	 * @throws net.sf.qualitycheck.exception.IllegalStateOfArgumentException
	 *             if no URL can be resolved to the given given file
	 */
	@Nonnull
	@Deprecated
	public static CachingXmlDataStore createCachingXmlDataStore(@Nonnull final File cacheFile, @Nonnull final DataStore fallback) {
		return createCachingXmlDataStore(cacheFile, UrlUtil.build(DEFAULT_DATA_URL), UrlUtil.build(DEFAULT_VERSION_URL), DEFAULT_CHARSET,
				fallback);
	}

	/**
	 * Constructs a new instance of {@code CachingXmlDataStore} with the given arguments. The given {@code cacheFile}
	 * can be empty or filled with previously cached data in XML format. The file must be writable otherwise an
	 * exception will be thrown.
	 * 
	 * @param cacheFile
	 *            file with cached <em>UAS data</em> in XML format or empty file
	 * @param dataUrl
	 *            URL to <em>UAS data</em>
	 * @param versionUrl
	 *            URL to version information about the given <em>UAS data</em>
	 * @param charset
	 *            the character set in which the data should be read
	 * @param fallback
	 *            <em>UAS data</em> as fallback in case the data on the specified resource can not be read correctly
	 * @return new instance of {@link CachingXmlDataStore}
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if one of the given arguments is {@code null}
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if the given cache file can not be read
	 * @throws net.sf.qualitycheck.exception.IllegalStateOfArgumentException
	 *             if no URL can be resolved to the given given file
	 */
	@Nonnull
	public static CachingXmlDataStore createCachingXmlDataStore(@Nonnull final File cacheFile, @Nonnull final URL dataUrl,
			@Nonnull final URL versionUrl, @Nonnull final Charset charset, @Nonnull final DataStore fallback) {
		Check.notNull(cacheFile, "cacheFile");
		Check.notNull(charset, "charset");
		Check.notNull(dataUrl, "dataUrl");
		Check.notNull(fallback, "fallback");
		Check.notNull(versionUrl, "versionUrl");

		final DataReader reader = new XmlDataReader();
		final DataStore fallbackDataStore = readCacheFileAsFallback(reader, cacheFile, charset, fallback);
		return new CachingXmlDataStore(reader, dataUrl, versionUrl, charset, cacheFile, fallbackDataStore);
	}

	/**
	 * Constructs a new instance of {@code CachingXmlDataStore} with the given arguments. The file used to cache the
	 * read in <em>UAS data</em> will be called from {@link CachingXmlDataStore#findOrCreateCacheFile()}. This file may
	 * be empty or filled with previously cached data in XML format. The file must be writable otherwise an exception
	 * will be thrown.
	 * 
	 * @param dataUrl
	 *            URL to <em>UAS data</em>
	 * @param versionUrl
	 *            URL to version information about the given <em>UAS data</em>
	 * @param charset
	 *            the character set in which the data should be read
	 * @param fallback
	 *            <em>UAS data</em> as fallback in case the data on the specified resource can not be read correctly
	 * @return new instance of {@link CachingXmlDataStore}
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if one of the given arguments is {@code null}
	 * @throws net.sf.qualitycheck.exception.IllegalStateOfArgumentException
	 *             if the given cache file can not be read
	 */
	@Nonnull
	public static CachingXmlDataStore createCachingXmlDataStore(@Nonnull final URL dataUrl, @Nonnull final URL versionUrl,
			@Nonnull final Charset charset, @Nonnull final DataStore fallback) {
		return createCachingXmlDataStore(findOrCreateCacheFile(), dataUrl, versionUrl, charset, fallback);
	}

	/**
	 * Removes the given cache file because it contains damaged content.
	 * 
	 * @param cacheFile
	 *            cache file to delete
	 */
	private static void deleteCacheFile(final File cacheFile) {
		try {
			if (cacheFile.delete()) {
				LOG.warn(String.format(MSG_CACHE_FILE_IS_DAMAGED_AND_DELETED, cacheFile.getPath()));
			} else {
				LOG.warn(String.format(MSG_CACHE_FILE_IS_DAMAGED, cacheFile.getPath()));
			}
		} catch (final Exception e) {
			LOG.warn(String.format(MSG_CACHE_FILE_IS_DAMAGED, cacheFile.getPath()));
		}
	}

	/**
	 * Gets the cache file for <em>UAS data</em> in the default temporary-file directory. If no cache file exists, a new
	 * empty file in the default temporary-file directory will be created, using the default prefix and suffix to
	 * generate its name.
	 * 
	 * @return file to cache read in <em>UAS data</em>
	 * @throws net.sf.qualitycheck.exception.IllegalStateOfArgumentException
	 *             if the cache file can not be created
	 */
	@Nonnull
	public static File findOrCreateCacheFile() {
		final File file = new File(CACHE_DIR, PREFIX + SUFFIX);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (final IOException e) {
				throw new IllegalStateOfArgumentException("Can not create a cache file.", e);
			}
		}
		return file;
	}

	/**
	 * Checks if the given file is empty.
	 * 
	 * @param file
	 *            the file that could be empty
	 * @return {@code true} when the file is accessible and empty otherwise {@code false}
	 * @throws net.sf.qualitycheck.exception.IllegalStateOfArgumentException
	 *             if an I/O error occurs
	 */
	private static boolean isEmpty(@Nonnull final File file, @Nonnull final Charset charset) {
		try {
			return FileUtil.isEmpty(file, charset);
		} catch (final IOException e) {
			throw new IllegalStateOfArgumentException("The given file could not be read.", e);
		}
	}

	/**
	 * Tries to read the content of specified cache file and returns them as fallback data store. If the cache file
	 * contains unexpected data the given fallback data store will be returned instead.
	 * 
	 * @param reader
	 *            data reader to read the given {@code dataUrl}
	 * @param cacheFile
	 *            file with cached <em>UAS data</em> in XML format or empty file
	 * @param versionUrl
	 *            URL to version information about the given <em>UAS data</em>
	 * @param charset
	 *            the character set in which the data should be read
	 * @param fallback
	 *            <em>UAS data</em> as fallback in case the data on the specified resource can not be read correctly
	 * @return a fallback data store
	 */
	private static DataStore readCacheFileAsFallback(@Nonnull final DataReader reader, @Nonnull final File cacheFile,
			@Nonnull final Charset charset, @Nonnull final DataStore fallback) {
		DataStore fallbackDataStore;
		if (!isEmpty(cacheFile, charset)) {
			final URL cacheFileUrl = UrlUtil.toUrl(cacheFile);
			try {
				fallbackDataStore = new CacheFileDataStore(reader.read(cacheFileUrl, charset), reader, cacheFileUrl, charset);
				LOG.debug(MSG_CACHE_FILE_IS_FILLED);
			} catch (final RuntimeException e) {
				fallbackDataStore = fallback;
				deleteCacheFile(cacheFile);
			}
		} else {
			fallbackDataStore = fallback;
			LOG.debug(MSG_CACHE_FILE_IS_EMPTY);
		}
		return fallbackDataStore;
	}

	/**
	 * Constructs an {@code CachingXmlDataStore} with the given arguments.
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
	 * @param cacheFile
	 *            file with cached <em>UAS data</em> in XML format or an empty file
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if one of the given arguments is {@code null}
	 */
	private CachingXmlDataStore(@Nonnull final DataReader reader, @Nonnull final URL dataUrl, @Nonnull final URL versionUrl,
			@Nonnull final Charset charset, @Nonnull final File cacheFile, @Nonnull final DataStore fallback) {
		super(reader, dataUrl, versionUrl, charset, fallback);
		setUpdateOperation(new UpdateOperationWithCacheFileTask(this, cacheFile));
	}

}
