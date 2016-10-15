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
package net.sf.uadetector.internal.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * This class is intended to provide file utility functions.
 * 
 * @author André Rouél
 */
public final class FileUtil {

	/**
	 * Checks if the given file is empty.
	 * 
	 * @param file
	 *            the file that could be empty
	 * @return {@code true} when the file is accessible and empty otherwise {@code false}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static boolean isEmpty(final File file, final Charset charset) throws IOException {
		boolean empty = false;
		BufferedReader reader = null;
		boolean threw = true;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
			final String line = reader.readLine();
			empty = line == null;
			threw = false;
		} finally {
			Closeables.close(reader, threw);
		}
		return empty;
	}

	/**
	 * <strong>Attention:</strong> This class is not intended to create objects from it.
	 */
	private FileUtil() {
		// This class is not intended to create objects from it.
	}

}
