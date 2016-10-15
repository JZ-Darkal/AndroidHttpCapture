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

import javax.annotation.Nonnegative;

/**
 * Defines an update operation which will be executed within a executor service in background.
 * 
 * @author André Rouél
 */
public interface UpdateOperation extends Runnable {

	/**
	 * The default interval to check for updates is once per day
	 */
	@Nonnegative
	long DEFAULT_UPDATE_INTERVAL = 1000 * 60 * 60 * 24;

	/**
	 * This function checks whether updated UAS data are available and updates silently the data in a
	 * {@link net.sf.uadetector.datastore.DataStore}.
	 */
	void call();

	/**
	 * Gets the time of the last update check in milliseconds.
	 * 
	 * @return time of the last update check in milliseconds
	 */
	@Nonnegative
	long getLastUpdateCheck();

	/**
	 * Shuts down the corresponding background executor.
	 */
	void shutdown();

}
