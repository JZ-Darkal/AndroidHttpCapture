package net.lightbody.bmp.proxy.dns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

/**
 * An {@link net.lightbody.bmp.proxy.dns.AdvancedHostResolver} that provides native JVM lookup using {@link net.lightbody.bmp.proxy.dns.NativeResolver}
 * but also implements DNS cache manipulation functionality.
 * <p/>
 * <b>Important note:</b> The Oracle JVM does not provide any public facility to manipulate the JVM's DNS cache. This class uses reflection to forcibly
 * manipulate the cache, which includes access to private class members that are not part of the published Java specification. As such, this
 * implementation is brittle and may break in a future Java release, or may not work on non-Oracle JVMs. If this implementation cannot
 * perform any of its operations due to a failure to find or set the relevant field using reflection, it will log a warning but will not
 * throw an exception. You are using this class at your own risk! <b>JVM cache manipulation does not work on Windows</b> -- this class will behave exactly
 * the same as {@link net.lightbody.bmp.proxy.dns.NativeResolver} on that platform.
 */
public class NativeCacheManipulatingResolver extends NativeResolver {
    private static final Logger log = LoggerFactory.getLogger(NativeCacheManipulatingResolver.class);

    @Override
    public void clearDNSCache() {
        // clear the DNS cache but replacing the LinkedHashMaps that contain the positive and negative caches on the
        // private static InetAddress.Cache inner class with new, empty maps
        try {
            Field positiveCacheField = InetAddress.class.getDeclaredField("addressCache");
            positiveCacheField.setAccessible(true);
            Object positiveCacheInstance = positiveCacheField.get(null);

            Field negativeCacheField = InetAddress.class.getDeclaredField("negativeCache");
            negativeCacheField.setAccessible(true);
            Object negativeCacheInstance = positiveCacheField.get(null);

            Class<?> cacheClass = Class.forName("java.net.InetAddress$Cache");
            Field cacheField = cacheClass.getDeclaredField("cache");
            cacheField.setAccessible(true);

            cacheField.set(positiveCacheInstance, new LinkedHashMap());
            cacheField.set(negativeCacheInstance, new LinkedHashMap());
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            log.warn("Unable to clear native JVM DNS cache", e);
        }
    }

    @Override
    public void setPositiveDNSCacheTimeout(int timeout, TimeUnit timeUnit) {
        try {
            Class<?> inetAddressCachePolicyClass = Class.forName("sun.net.InetAddressCachePolicy");

            Field positiveCacheTimeoutSeconds = inetAddressCachePolicyClass.getDeclaredField("cachePolicy");
            positiveCacheTimeoutSeconds.setAccessible(true);

            if (timeout < 0) {
                positiveCacheTimeoutSeconds.setInt(null, -1);
                java.security.Security.setProperty("networkaddress.cache.ttl", "-1");
            } else {
                positiveCacheTimeoutSeconds.setInt(null, (int) TimeUnit.SECONDS.convert(timeout, timeUnit));
                java.security.Security.setProperty("networkaddress.cache.ttl", Long.toString(TimeUnit.SECONDS.convert(timeout, timeUnit)));
            }
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            log.warn("Unable to modify native JVM DNS cache timeouts", e);
        }
    }

    @Override
    public void setNegativeDNSCacheTimeout(int timeout, TimeUnit timeUnit) {
        try {
            Class<?> inetAddressCachePolicyClass = Class.forName("sun.net.InetAddressCachePolicy");

            Field negativeCacheTimeoutSeconds = inetAddressCachePolicyClass.getDeclaredField("negativeCachePolicy");
            negativeCacheTimeoutSeconds.setAccessible(true);

            if (timeout < 0) {
                negativeCacheTimeoutSeconds.setInt(null, -1);
                java.security.Security.setProperty("networkaddress.cache.negative.ttl", "-1");
            } else {
                negativeCacheTimeoutSeconds.setInt(null, (int) TimeUnit.SECONDS.convert(timeout, timeUnit));
                java.security.Security.setProperty("networkaddress.cache.negative.ttl", Long.toString(TimeUnit.SECONDS.convert(timeout, timeUnit)));
            }
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            log.warn("Unable to modify native JVM DNS cache timeouts", e);
        }
    }
}
