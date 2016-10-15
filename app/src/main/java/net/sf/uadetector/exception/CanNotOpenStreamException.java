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
package net.sf.uadetector.exception;

/**
 * Thrown to indicate that no stream to an {@link java.net.URL} can be established.<br>
 * <br>
 * This exception is intended to tunnel the checked exception {@link java.io.IOException} during the call
 * {@link java.net.URL#openStream()}.
 * 
 * @author André Rouél
 */
public class CanNotOpenStreamException extends RuntimeException {

	private static final long serialVersionUID = 8381680536297450770L;

	protected static final String DEFAULT_MESSAGE = "Can not open stream to the given URL.";

	protected static final String MESSAGE_WITH_URL = "Can not open stream to the given URL: %s";

	private static String format(final String url) {
		return String.format(MESSAGE_WITH_URL, url);
	}

	/**
	 * Constructs an {@code CanNotOpenStreamException} with the default message
	 * {@link CanNotOpenStreamException#DEFAULT_MESSAGE}.
	 */
	public CanNotOpenStreamException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * Constructs an {@code CanNotOpenStreamException} with the message
	 * {@link CanNotOpenStreamException#MESSAGE_WITH_URL} including the given URL as string representation.
	 * 
	 * @param url
	 *            the URL to which no stream can be established
	 */
	public CanNotOpenStreamException(final String url) {
		super(format(url));
	}

	/**
	 * Constructs a new exception with the message {@link CanNotOpenStreamException#MESSAGE_WITH_URL} including the
	 * given URL as string representation and cause.
	 * 
	 * @param url
	 *            the URL to which no stream can be established
	 * @param cause
	 *            the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method). (A
	 *            {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public CanNotOpenStreamException(final String url, final Throwable cause) {
		super(format(url), cause);
	}

	/**
	 * Constructs a new exception with the default message {@link CanNotOpenStreamException#DEFAULT_MESSAGE}.
	 * 
	 * @param cause
	 *            the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method). (A
	 *            {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public CanNotOpenStreamException(final Throwable cause) {
		super(DEFAULT_MESSAGE, cause);
	}

}
