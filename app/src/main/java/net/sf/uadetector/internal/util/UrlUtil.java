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
package net.sf.uadetector.internal.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import net.sf.qualitycheck.Check;
import net.sf.qualitycheck.exception.IllegalStateOfArgumentException;
import net.sf.uadetector.exception.CanNotOpenStreamException;

/**
 * This class is intended to provide URL utility functions that encapsulate the checked exceptions like
 * {@link MalformedURLException} during the construction of an URL or the {@link IOException} while opening a stream to
 * an {@code URL}.
 * 
 * @author André Rouél
 */
public final class UrlUtil {

	/**
	 * Creates an {@code URL} instance from the given {@code String} representation.<br>
	 * <br>
	 * This method tunnels a {@link MalformedURLException} by an {@link IllegalStateOfArgumentException}.
	 * 
	 * @param url
	 *            {@code String} representation of an {@code URL}
	 * @return new {@code URL} instance
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if the given argument is {@code null}
	 * @throws IllegalStateOfArgumentException
	 *             if the string representation of the given URL is invalid and a {@link MalformedURLException} occurs
	 */
	public static URL build(@Nonnull final String url) {
		Check.notNull(url, "url");

		URL ret = null;
		try {
			ret = new URL(url);
		} catch (final MalformedURLException e) {
			throw new IllegalStateOfArgumentException("The given string is not a valid URL: " + url, e);
		}
		return ret;
	}

	/**
	 * Tries to open an {@link InputStream} to the given {@link URL}.
	 * 
	 * @param url
	 *            URL which should be opened
	 * @return opened stream
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if the given argument is {@code null}
	 * @throws CanNotOpenStreamException
	 *             if no stream to the given {@code URL} can be established
	 */
	public static InputStream open(@Nonnull final URL url) {
		Check.notNull(url, "url");

		final InputStream ret;
		try {
			ret = url.openStream();
		} catch (final IOException e) {
			throw new CanNotOpenStreamException(url.toString(), e);
		}
		return ret;
	}

	/**
	 * Reads the content of the passed {@link URL} as string representation.
	 * 
	 * @param url
	 *            URL to <em>UAS data</em>
	 * @param charset
	 *            the character set in which the data should be read
	 * @return content as {@code String}
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if any of the given arguments is {@code null}
	 * @throws CanNotOpenStreamException
	 *             if no stream to the given {@code URL} can be established
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static String read(@Nonnull final URL url, @Nonnull final Charset charset) throws IOException {
		Check.notNull(url, "url");
		Check.notNull(charset, "charset");

		final InputStream inputStream = open(url);
		BufferedReader reader = null;
		final StringBuilder buffer = new StringBuilder();
		boolean threw = true;
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream, charset));
			buffer.append(readAll(reader));
			threw = false;
		} finally {
			Closeables.close(reader, threw);
			Closeables.close(inputStream, false);
		}
		return buffer.toString();
	}

	/**
	 * Reads the entire contents via the given {@link Reader} as string.
	 * 
	 * @param reader
	 *            {@code Reader} to read the entire contents
	 * @return the read contents as string
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	private static String readAll(@Nonnull final Reader reader) throws IOException {
		final StringBuilder buffer = new StringBuilder();
		int cp;
		while ((cp = reader.read()) != -1) {
			buffer.append((char) cp);
		}
		return buffer.toString();
	}

	/**
	 * Gets the URL to a given {@code File}.
	 * 
	 * @param file
	 *            file to be converted to a URL
	 * @return an URL to the passed file
	 * @throws IllegalStateException
	 *             if no URL can be resolved to the given file
	 */
	public static URL toUrl(@Nonnull final File file) {
		Check.notNull(file, "file");

		URL url = null;
		try {
			url = file.toURI().toURL();
		} catch (final MalformedURLException e) {
			throw new IllegalStateException("Can not construct an URL for passed file.", e);
		}
		return url;
	}

	/**
	 * <strong>Attention:</strong> This class is not intended to create objects from it.
	 */
	private UrlUtil() {
		// This class is not intended to create objects from it.
	}

}
