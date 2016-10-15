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
 * Thrown to indicate that a {@link java.io.Closeable} cannot be closed.<br>
 * <br>
 * This exception is intended to tunnel the checked exception {@link java.io.IOException} during the call
 * {@link java.io.Closeable#close()}.
 * 
 * @author André Rouél
 */
public class CannotCloseException extends RuntimeException {

	private static final long serialVersionUID = -8641033043995976022L;

	protected static final String DEFAULT_MESSAGE = "Cannot close the given Closeable.";

	protected static final String MESSAGE_WITH_INFO = "Cannot close the given Closeable: %s";

	private static String format(final String url) {
		return String.format(MESSAGE_WITH_INFO, url);
	}

	/**
	 * Constructs an {@code CannotCloseException} with the default message {@link CannotCloseException#DEFAULT_MESSAGE}.
	 */
	public CannotCloseException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * Constructs an {@code CannotCloseException} with the message {@link CannotCloseException#MESSAGE_WITH_INFO}
	 * including additional information.
	 * 
	 * @param info
	 *            additional information why a {@link java.io.Closeable} cannot be closed
	 */
	public CannotCloseException(final String info) {
		super(format(info));
	}

	/**
	 * Constructs a new exception with the message {@link CannotCloseException#MESSAGE_WITH_INFO} including additional
	 * information.
	 * 
	 * @param info
	 *            additional information why a {@link java.io.Closeable} cannot be closed
	 * @param cause
	 *            the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method). (A
	 *            {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public CannotCloseException(final String info, final Throwable cause) {
		super(format(info), cause);
	}

	/**
	 * Constructs a new exception with the default message {@link CannotCloseException#DEFAULT_MESSAGE}.
	 * 
	 * @param cause
	 *            the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method). (A
	 *            {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public CannotCloseException(final Throwable cause) {
		super(DEFAULT_MESSAGE, cause);
	}

}
