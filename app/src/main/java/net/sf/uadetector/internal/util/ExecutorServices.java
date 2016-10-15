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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import net.sf.qualitycheck.Check;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This utility is intended to provide predefined {@link ExecutorService}s which runs in background and can be easily
 * shut-downed within {@link #shutdownAll()} if necessary.
 * 
 * @author André Rouél
 */
public final class ExecutorServices {

	/**
	 * This synchronized {@link Set} is a registry of all distributed background executors by this utility.
	 * <p>
	 * The containing {@link ExecutorService}s will be used to run a concrete update of <i>UAS data</i> instantly in
	 * background.
	 */
	private static final Set<ExecutorService> BACKGROUND_EXECUTORS = Collections.synchronizedSet(new HashSet<ExecutorService>(3));

	/**
	 * Default name of a thread which will be used to run a concrete update within a background executor
	 */
	private static final String DEFAULT_BACKGROUND_EXECUTOR_NAME = "update-operation";

	/**
	 * Default name of a thread which will be created within a scheduler
	 */
	private static final String DEFAULT_SCHEDULER_NAME = "update-scheduler";

	/**
	 * Corresponding logger for this class
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ExecutorServices.class);

	/**
	 * This synchronized {@link Set} is a registry of all distributed schedulers by this utility.
	 * <p>
	 * The containing {@link ScheduledExecutorService}s will be used to schedule commands which updates the <i>UAS
	 * data</i> in defined intervals.
	 */
	private static final Set<ScheduledExecutorService> SCHEDULERS = Collections.synchronizedSet(new HashSet<ScheduledExecutorService>(3));

	/**
	 * Timeout (in seconds) to shutdown all available executors at the latest
	 */
	public static final long SHUTDOWN_DURATION = 5;

	/**
	 * Creates a single-threaded executor that is registered by this class in order to shut it down later (when it
	 * becomes necessary).
	 * 
	 * @return a new background executor
	 */
	public static ExecutorService createBackgroundExecutor() {
		final ExecutorService executor = Executors.newSingleThreadExecutor(new DaemonThreadFactory(DEFAULT_BACKGROUND_EXECUTOR_NAME));
		BACKGROUND_EXECUTORS.add(executor);
		return executor;
	}

	/**
	 * Creates a single-threaded scheduler that is registered by this class in order to shut it down later (when it
	 * becomes necessary).
	 * 
	 * @return a new scheduler
	 */
	public static ScheduledExecutorService createScheduler() {
		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new DaemonThreadFactory(DEFAULT_SCHEDULER_NAME));
		SCHEDULERS.add(scheduler);
		return scheduler;
	}

	/**
	 * Shutdowns the given {@code ExecutorService} as soon as possible, but not later than the specified default time
	 * (which is {@value #SHUTDOWN_DURATION} seconds).
	 * 
	 * @param executorService
	 *            executor to stop
	 */
	public static void shutdown(@Nonnull final ExecutorService executorService) {
		Check.notNull(executorService, "executorService");
		shutdown(executorService, SHUTDOWN_DURATION, TimeUnit.SECONDS);
	}

	/**
	 * Shutdowns the given {@code ExecutorService} as soon as possible, but not later than the specified time.
	 * 
	 * @param executorService
	 *            executor to stop
	 * @param duration
	 *            duration as a numerical value
	 * @param unit
	 *            duration unit
	 */
	public static void shutdown(@Nonnull final ExecutorService executorService, @Nonnegative final long duration,
			@Nonnull final TimeUnit unit) {
		Check.notNull(executorService, "executorService");
		Check.notNull(duration, "duration");
		Check.notNull(unit, "unit");

		executorService.shutdown();
		try {
			if (!executorService.awaitTermination(duration, unit)) {
				LOG.info(String.format("Executor did not terminate in %s %s.", duration, unit.name().toLowerCase()));
				final List<Runnable> droppedTasks = executorService.shutdownNow();
				LOG.info("Executor was abruptly shut down. " + droppedTasks.size() + " tasks will not be executed.");
			}
			unregisterIfPossible(executorService);
		} catch (final InterruptedException e) {
			LOG.warn("Executor termination failed: " + e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Shuts down all registered scheduler and background workers as soon as possible, but at the latest in specified
	 * {@link #SHUTDOWN_DURATION} seconds.
	 */
	public static void shutdownAll() {
		for (final ExecutorService executor : new ArrayList<ExecutorService>(BACKGROUND_EXECUTORS)) {
			shutdown(executor);
			BACKGROUND_EXECUTORS.remove(executor);
		}
		for (final ScheduledExecutorService scheduler : new ArrayList<ScheduledExecutorService>(SCHEDULERS)) {
			shutdown(scheduler);
			SCHEDULERS.remove(scheduler);
		}
	}

	/**
	 * Unregisters the given {@code ExecutorService} if it is an instance of {@code ScheduledExecutorService} from the
	 * list of registered schedulers.
	 * 
	 * @param executorService
	 *            a possible scheduler
	 */
	private static void unregisterIfPossible(final ExecutorService executorService) {
		if (executorService instanceof ScheduledExecutorService) {
			SCHEDULERS.remove(executorService);
		} else {
			BACKGROUND_EXECUTORS.remove(executorService);
		}
	}

	/**
	 * <strong>Attention:</strong> This class is not intended to create objects from it.
	 */
	private ExecutorServices() {
		// This class is not intended to create objects from it.
	}

}
