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
package net.sf.uadetector.parser;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.Nonnegative;
import javax.annotation.PreDestroy;

import net.sf.qualitycheck.Check;
import net.sf.uadetector.datastore.RefreshableDataStore;
import net.sf.uadetector.internal.util.ExecutorServices;

/**
 * This parser checks once per day if newer data are available. When newer data are available, they will be loaded, read
 * and replaced by the current one.
 * 
 * @author André Rouél
 */
public final class UpdatingUserAgentStringParserImpl extends UserAgentStringParserImpl<RefreshableDataStore> {

	/**
	 * The default interval to check for updates is once per day
	 */
	public static final long DEFAULT_UPDATE_INTERVAL = 1000 * 60 * 60 * 24;

	/**
	 * Interval to check for updates in milliseconds
	 */
	private long updateInterval = DEFAULT_UPDATE_INTERVAL;

	/**
	 * Current update task of {@link UpdatingUserAgentStringParserImpl#scheduler}
	 */
	private ScheduledFuture<?> currentUpdateTask;

	/**
	 * {@link ScheduledExecutorService} to schedule commands to update the UAS data in defined intervals
	 */
	private final ScheduledExecutorService scheduler = ExecutorServices.createScheduler();

	/**
	 * Constructs an instance of {@code OnlineUserAgentStringParser}. During construction new UAS data will be queried
	 * online by the given {@code URL}s.
	 * 
	 * @param store
	 *            {@code DataStore} with reference UAS data used in fallback case
	 * @throws net.sf.qualitycheck.exception.IllegalNullArgumentException
	 *             if one of the given arguments is {@code null}
	 */
	public UpdatingUserAgentStringParserImpl(final RefreshableDataStore store) {
		super(store);

		// set up update service
		setUpUpdateService();
	}

	/**
	 * Gets the current update interval in milliseconds.
	 * 
	 * @return current update interval in milliseconds
	 */
	public long getUpdateInterval() {
		return updateInterval;
	}

	/**
	 * Sets a new update interval in milliseconds.<br>
	 * <br>
	 * When a new update interval is set, the old update service is removed and a new one will be set.
	 * 
	 * @param updateInterval
	 *            update interval in milliseconds
	 * @throws net.sf.qualitycheck.exception.IllegalNegativeArgumentException
	 *             if the given update interval is less than 0
	 */
	public void setUpdateInterval(@Nonnegative final long updateInterval) {
		Check.notNegative(updateInterval, "updateInterval");
		this.updateInterval = updateInterval;
		setUpUpdateService();
	}

	/**
	 * Set up a new update service to get newer UAS data
	 */
	private void setUpUpdateService() {
		if (currentUpdateTask != null) {
			currentUpdateTask.cancel(false);
		}
		currentUpdateTask = scheduler.scheduleWithFixedDelay(getDataStore().getUpdateOperation(), 0, updateInterval, MILLISECONDS);
	}

	@Override
	@PreDestroy
	public void shutdown() {
		currentUpdateTask.cancel(false);
		ExecutorServices.shutdown(scheduler);
		getDataStore().getUpdateOperation().shutdown();
	}

}
