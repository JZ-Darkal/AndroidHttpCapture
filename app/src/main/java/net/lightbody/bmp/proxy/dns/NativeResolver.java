package net.lightbody.bmp.proxy.dns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * An {@link net.lightbody.bmp.proxy.dns.AdvancedHostResolver} that provides native JVM lookup using {@link java.net.InetAddress}.
 * <b>This implementation does not provide any cache manipulation.</b> Attempting to manipulate the DNS cache will result in a DEBUG-level
 * log statement and will not raise an exception. The {@link net.lightbody.bmp.proxy.dns.DnsJavaResolver} provides support for cache
 * manipulation. If you absolutely need to manipulate the native JVM DNS cache, see
 * {@link net.lightbody.bmp.proxy.dns.NativeCacheManipulatingResolver} for details.
 */
public class NativeResolver extends AbstractHostNameRemapper implements AdvancedHostResolver {
    private static final Logger log = LoggerFactory.getLogger(NativeResolver.class);

    @Override
    public void clearDNSCache() {
        log.debug("Cannot clear native JVM DNS Cache using this Resolver");
    }

    @Override
    public void setPositiveDNSCacheTimeout(int timeout, TimeUnit timeUnit) {
        log.debug("Cannot change native JVM DNS cache timeout using this Resolver");
    }

    @Override
    public void setNegativeDNSCacheTimeout(int timeout, TimeUnit timeUnit) {
        log.debug("Cannot change native JVM DNS cache timeout using this Resolver");
    }

    @Override
    public Collection<InetAddress> resolveRemapped(String remappedHost) {
        try {
            Collection<InetAddress> addresses = Arrays.asList(InetAddress.getAllByName(remappedHost));

            return addresses;
        } catch (UnknownHostException e) {
            return Collections.emptyList();
        }
    }
}
