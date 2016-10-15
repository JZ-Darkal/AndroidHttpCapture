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

import javax.annotation.Nonnull;

/**
 * Extends the interface with an update functionality for <em>UAS data</em> in the store.
 * 
 * @author André Rouél
 */
public interface RefreshableDataStore extends DataStore {

	/**
	 * Message for the log if the read content can not be processed correctly
	 */
	String MSG_FAULTY_CONTENT = "The read content is faulty and can not be processed correctly.";

	/**
	 * Message for the log if the <em>UAS data</em> can not be read from the given URL
	 */
	String MSG_URL_NOT_READABLE = "The data can not be read from the specified URL: %s";

	/**
	 * This method returns a data store which will be used during start up and can be used in emergency cases.
	 * <p>
	 * This data store will be used instantly during initialization to avoid long initializations times of an
	 * {@link net.sf.uadetector.UserAgentStringParser}, especially when reading data by a network connection.
	 */
	@Nonnull
	DataStore getFallback();

	/**
	 * Returns the update operation of this data store which can be triggered within an executor service.
	 * 
	 * @return an update operation
	 */
	@Nonnull
	UpdateOperation getUpdateOperation();

	/**
	 * Triggers the update of the {@code DataStore}. When this action is executed, the current data URL will be read in
	 * and the {@code DataReader} parses and builds a new {@code Data} instance. Finally, the currently set {@code Data}
	 * reference will be replaced by the new one.
	 */
	void refresh();

}
