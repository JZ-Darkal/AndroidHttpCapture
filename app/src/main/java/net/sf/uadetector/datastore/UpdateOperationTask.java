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

import net.sf.uadetector.internal.data.Data;

final class UpdateOperationTask extends AbstractUpdateOperation {

	/**
	 * The data store for instances that implements {@link net.sf.uadetector.internal.data.Data}
	 */
	private final AbstractRefreshableDataStore store;

	public UpdateOperationTask(final AbstractRefreshableDataStore dataStore) {
		super(dataStore);
		store = dataStore;
	}

	@Override
	public void call() {
		if (isUpdateAvailable()) {
			final Data data = store.getDataReader().read(store.getDataUrl(), store.getCharset());
			store.setData(data);
		}
	}

}
