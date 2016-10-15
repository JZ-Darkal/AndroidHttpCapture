package net.lightbody.bmp.mitm.stats;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks basic certificate generation statistics.
 */
public class CertificateGenerationStatistics {
    private AtomicLong certificateGenerationTimeMs = new AtomicLong();
    private AtomicInteger certificatesGenerated = new AtomicInteger();

    private AtomicLong firstCertificateGeneratedTimestamp = new AtomicLong();

    /**
     * Records a certificate generation that started at startTimeMs and completed at finishTimeMs.
     */
    public void certificateCreated(long startTimeMs, long finishTimeMs) {
        certificatesGenerated.incrementAndGet();
        certificateGenerationTimeMs.addAndGet(finishTimeMs - startTimeMs);

        // record the timestamp of the first certificate generation
        firstCertificateGeneratedTimestamp.compareAndSet(0L, System.currentTimeMillis());
    }

    /**
     * Returns the total number of certificates created.
     */
    public int getCertificatesGenerated() {
        return certificatesGenerated.get();
    }

    /**
     * Returns the total number of ms spent generating all certificates.
     */
    public long getTotalCertificateGenerationTimeMs() {
        return certificateGenerationTimeMs.get();
    }

    /**
     * Returns the average number of ms per certificate generated.
     */
    public long getAvgCertificateGenerationTimeMs() {
        if (certificatesGenerated.get() > 0) {
            return certificateGenerationTimeMs.get() / certificatesGenerated.get();
        } else {
            return 0L;
        }
    }

    /**
     * Returns the timestamp (ms since epoch) when the first certificate was generated, or 0 if none have been generated.
     */
    public long firstCertificateGeneratedTimestamp() {
        return firstCertificateGeneratedTimestamp.get();
    }
}
