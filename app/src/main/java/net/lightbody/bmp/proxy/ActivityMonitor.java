package net.lightbody.bmp.proxy;

import com.google.common.util.concurrent.Monitor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks active and total requests on a proxy, and provides a mechanism to wait for active requests to finish.
 * See {@link net.lightbody.bmp.proxy.ActivityMonitor#waitForQuiescence(long, long, java.util.concurrent.TimeUnit)}.
 */
public class ActivityMonitor {
    private final AtomicInteger activeRequests = new AtomicInteger(0);
    private final AtomicInteger totalRequests = new AtomicInteger(0);

    private final AtomicLong lastRequestFinishedNanos = new AtomicLong(System.nanoTime());

    private final Monitor monitor = new Monitor();

    private final Monitor.Guard requestNotActive = new Monitor.Guard(monitor) {
        @Override
        public boolean isSatisfied() {
            return activeRequests.get() == 0;
        }
    };

    private final Monitor.Guard requestActive = new Monitor.Guard(monitor) {
        @Override
        public boolean isSatisfied() {
            return activeRequests.get() > 0;
        }
    };

    public void requestStarted() {
        int previousCount = activeRequests.getAndIncrement();
        totalRequests.incrementAndGet();
        if (previousCount == 0) {
            // previously there were no active requests, but now there are -- signal to any waitForQuiescence threads that they need to
            // begin waiting again
            monitor.enter();
            monitor.leave();
        }
    }

    public void requestFinished() {
        int newCount = activeRequests.decrementAndGet();
        lastRequestFinishedNanos.set(System.nanoTime());

        if (newCount == 0) {
            // there are no active requests, so signal to any waitForQuiescence threads that they can begin waiting for their quietPeriod
            monitor.enter();
            monitor.leave();
        }
    }

    public int getActiveRequests() {
        return activeRequests.get();
    }

    public int getTotalRequests() {
        return totalRequests.get();
    }

    public boolean waitForQuiescence(long quietPeriod, long timeout, TimeUnit timeUnit) {
        // the minRequestFinishTime is the earliest possible time the current or last request "could" finish. if there is no active
        // request, this is simply the lastRequestFinishedNanos time. if there is an active request, it is "now". this helps avoid waiting
        // for quiescence if there is an active request and the timeout is less than the quietPeriod.
        long minRequestFinishTime;
        if (activeRequests.get() == 0) {
            if (timeUnit.convert(System.nanoTime() - lastRequestFinishedNanos.get(), TimeUnit.NANOSECONDS) >= quietPeriod) {
                return true;
            } else {
                minRequestFinishTime = lastRequestFinishedNanos.get();
            }
        } else {
            minRequestFinishTime = System.nanoTime();
        }

        // record the maximum time we can wait until (the current time + the timeout), which will allow us to avoid waiting for
        // quiescence if it is not possible to satisfy the quietPeriod before the waitUntil time elapses
        long waitUntil = System.nanoTime() + TimeUnit.NANOSECONDS.convert(timeout, timeUnit);

        while (minRequestFinishTime + TimeUnit.NANOSECONDS.convert(quietPeriod, timeUnit) <= waitUntil) {
            // the maximum amount of time we can wait for active requests to finish that will still allow us to wait for quiescence
            // for the quietPeriod.
            long maxWaitTimeForActiveRequests = waitUntil - System.nanoTime() - TimeUnit.NANOSECONDS.convert(quietPeriod, timeUnit);

            // wait for active requests to finish
            boolean success = monitor.enterWhenUninterruptibly(requestNotActive, maxWaitTimeForActiveRequests, TimeUnit.NANOSECONDS);

            if (!success) {
                // timed out waiting for active requests to finish
                return false;
            }

            monitor.leave();

            // the time needed to monitor for new active requests is whenever the last request finished + the quiet period. this may be less
            // than the actual quiet period if no requests were active when entering waitForQuiescence, but the quietPeriod has not yet elapsed
            // since the last request.
            long waitForNewRequests = lastRequestFinishedNanos.get() - System.nanoTime() + TimeUnit.NANOSECONDS.convert(quietPeriod, timeUnit);

            // if the quietPeriod has already elapsed since the last request, no need to wait any longer
            if (waitForNewRequests < 0) {
                return true;
            }

            // wait for new requests to come in. if a new request comes in, the loop will restart, waiting for active requests to complete.
            boolean requestsActive = monitor.enterWhenUninterruptibly(requestActive, waitForNewRequests, TimeUnit.NANOSECONDS);

            if (requestsActive) {
                // a request became active, so we need to wait for all requests to finish again
                monitor.leave();

                continue;
            } else {
                return true;
            }
        }

        return false;
    }
}
