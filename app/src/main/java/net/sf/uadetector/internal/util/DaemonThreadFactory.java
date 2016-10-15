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

import java.util.concurrent.ThreadFactory;

import javax.annotation.Nonnull;

import net.sf.qualitycheck.Check;

/**
 * Factory to create daemon threads that runs as a background process and do not blocks an application shutdown
 */
public final class DaemonThreadFactory implements ThreadFactory {

	/**
	 * Name of a new thread
	 */
	@Nonnull
	private final String threadName;

	/**
	 * Creates a new {@code DaemonThreadFactory} which creates itself threads with the specified name.
	 * 
	 * @param threadName
	 *            name of a thread to be created
	 */
	public DaemonThreadFactory(@Nonnull final String threadName) {
		Check.notNull(threadName, "threadName");
		Check.notEmpty(threadName.trim(), "threadName");
		this.threadName = threadName;
	}

	@Override
	public Thread newThread(@Nonnull final Runnable runnable) {
		final Thread thread = new Thread(runnable);
		thread.setName(threadName);
		thread.setDaemon(true);
		return thread;
	}

}
