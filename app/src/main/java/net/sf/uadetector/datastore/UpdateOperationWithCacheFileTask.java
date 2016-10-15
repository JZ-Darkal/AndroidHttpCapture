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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import net.sf.qualitycheck.Check;
import net.sf.uadetector.exception.CanNotOpenStreamException;
import net.sf.uadetector.internal.data.Data;
import net.sf.uadetector.internal.util.Closeables;
import net.sf.uadetector.internal.util.FileUtil;
import net.sf.uadetector.internal.util.UrlUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class UpdateOperationWithCacheFileTask extends AbstractUpdateOperation {

	/**
	 * Corresponding default logger of this class
	 */
	private static final Logger LOG = LoggerFactory.getLogger(UpdateOperationWithCacheFileTask.class);

	/**
	 * Message for the log when issues occur during reading of or writing to the cache file.
	 */
	private static final String MSG_CACHE_FILE_ISSUES = "Issues occured during reading of or writing to the cache file: %s";

	/**
	 * Message for the log if the passed resources are the same and an update makes no sense
	 */
	private static final String MSG_SAME_RESOURCES = "The passed URL and file resources are the same. An update was not performed.";

	/**
	 * Creates a temporary file near the passed file. The name of the given one will be used and the suffix ".temp" will
	 * be added.
	 * 
	 * @param file
	 *            file in which the entire contents from the given URL can be saved
	 * @throws IllegalStateException
	 *             if the file can not be deleted
	 */
	protected static File createTemporaryFile(@Nonnull final File file) {
		Check.notNull(file, "file");

		final File tempFile = new File(file.getParent(), file.getName() + ".temp");

		// remove orphaned temporary file
		deleteFile(tempFile);

		return tempFile;
	}

	/**
	 * Removes the given file.
	 * 
	 * @param file
	 *            a file which should be deleted
	 * 
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if the given argument is {@code null}
	 * @throws net.sf.qualitycheck.exception.IllegalStateOfArgumentException
	 *             if the file can not be deleted
	 */
	protected static void deleteFile(@Nonnull final File file) {
		Check.notNull(file, "file");
		Check.stateIsTrue(!file.exists() || file.delete(), "Cannot delete file '%s'.", file.getPath());
	}

	/**
	 * Checks if the given file is empty.
	 * 
	 * @param file
	 *            the file that could be empty
	 * @return {@code true} when the file is accessible and empty otherwise {@code false}
	 * @throws IllegalStateException
	 *             if an I/O error occurs
	 */
	private static boolean isEmpty(@Nonnull final File file, @Nonnull final Charset charset) {
		try {
			return FileUtil.isEmpty(file, charset);
		} catch (final IOException e) {
			throw new IllegalStateException("The given file could not be read.");
		}
	}

	/**
	 * Checks that {@code older} {@link Data} has a lower version number than the {@code newer} one.
	 * 
	 * @param older
	 *            possibly older {@code Data}
	 * @param newer
	 *            possibly newer {@code Data}
	 * @return {@code true} if the {@code newer} Data is really newer, otherwise {@code false}
	 */
	protected static boolean isNewerData(@Nonnull final Data older, @Nonnull final Data newer) {
		return newer.getVersion().compareTo(older.getVersion()) > 0;
	}

	/**
	 * Reads the content from the given {@link URL} and saves it to the passed file.
	 * 
	 * @param file
	 *            file in which the entire contents from the given URL can be saved
	 * @param store
	 *            a data store for <em>UAS data</em>
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if any of the passed arguments is {@code null}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected static void readAndSave(@Nonnull final File file, @Nonnull final DataStore store) throws IOException {
		Check.notNull(file, "file");
		Check.notNull(store, "store");

		final URL url = store.getDataUrl();
		final Charset charset = store.getCharset();

		final boolean isEqual = url.toExternalForm().equals(UrlUtil.toUrl(file).toExternalForm());
		if (!isEqual) {

			// check if the data can be read in successfully
			final String data = UrlUtil.read(url, charset);
			if (Data.EMPTY.equals(store.getDataReader().read(data))) {
				throw new IllegalStateException("The read in content can not be transformed to an instance of 'Data'.");
			}

			final File tempFile = createTemporaryFile(file);

			FileOutputStream outputStream = null;
			boolean threw = true;
			try {
				// write data to temporary file
				outputStream = new FileOutputStream(tempFile);
				outputStream.write(data.getBytes(charset));

				// delete the original file
				deleteFile(file);

				threw = false;
			} finally {
				Closeables.close(outputStream, threw);
			}

			// rename the new file to the original one
			renameFile(tempFile, file);
		} else {
			LOG.debug(MSG_SAME_RESOURCES);
		}
	}

	/**
	 * Renames the given file {@code from} to the new file {@code to}.
	 * 
	 * @param from
	 *            an existing file
	 * @param to
	 *            a new file
	 * 
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if one of the given arguments is {@code null}
	 * @throws net.sf.qualitycheck.exception.IllegalStateOfArgumentException
	 *             if the file can not be renamed
	 */
	protected static void renameFile(@Nonnull final File from, @Nonnull final File to) {
		Check.notNull(from, "from");
		Check.stateIsTrue(from.exists(), "Argument 'from' must not be an existing file.");
		Check.notNull(to, "to");
		Check.stateIsTrue(from.renameTo(to), "Renaming file from '%s' to '%s' failed.", from.getAbsolutePath(), to.getAbsolutePath());
	}

	/**
	 * File to cache read in <em>UAS data</em>
	 */
	private final File cacheFile;

	/**
	 * The data store for instances that implements {@link net.sf.uadetector.internal.data.Data}
	 */
	private final AbstractRefreshableDataStore store;

	public UpdateOperationWithCacheFileTask(@Nonnull final AbstractRefreshableDataStore dataStore, @Nonnull final File cacheFile) {
		super(dataStore);
		Check.notNull(dataStore, "dataStore");
		Check.notNull(cacheFile, "cacheFile");
		store = dataStore;
		this.cacheFile = cacheFile;
	}

	@Override
	public void call() {
		readDataIfNewerAvailable();
	}

	private boolean isCacheFileEmpty() {
		return isEmpty(cacheFile, store.getCharset());
	}

	private void readDataIfNewerAvailable() {
		try {
			if (isUpdateAvailable() || isCacheFileEmpty()) {
				readAndSave(cacheFile, store);
				store.setData(store.getDataReader().read(cacheFile.toURI().toURL(), store.getCharset()));
			}
		} catch (final CanNotOpenStreamException e) {
			LOG.warn(String.format(RefreshableDataStore.MSG_URL_NOT_READABLE, e.getLocalizedMessage()));
			readFallbackData();
		} catch (final RuntimeException e) {
			LOG.warn(RefreshableDataStore.MSG_FAULTY_CONTENT, e);
			readFallbackData();
		} catch (final IOException e) {
			LOG.warn(String.format(MSG_CACHE_FILE_ISSUES, e.getLocalizedMessage()), e);
			readFallbackData();
		}
	}

	private void readFallbackData() {
		LOG.info("Reading fallback data...");
		try {
			if (isCacheFileEmpty()) {
				readAndSave(cacheFile, store.getFallback());
				final Data data = store.getDataReader().read(cacheFile.toURI().toURL(), store.getCharset());
				if (isNewerData(store.getData(), data)) {
					store.setData(data);
				}
			}
		} catch (final CanNotOpenStreamException e) {
			LOG.warn(String.format(RefreshableDataStore.MSG_URL_NOT_READABLE, e.getLocalizedMessage()));
		} catch (final RuntimeException e) {
			LOG.warn(RefreshableDataStore.MSG_FAULTY_CONTENT, e);
		} catch (final IOException e) {
			LOG.warn(String.format(MSG_CACHE_FILE_ISSUES, e.getLocalizedMessage()), e);
		}
	}

}
