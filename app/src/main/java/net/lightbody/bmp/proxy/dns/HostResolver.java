package net.lightbody.bmp.proxy.dns;

import java.net.InetAddress;
import java.util.Collection;

/**
 * Defines the basic functionality that {@link net.lightbody.bmp.BrowserMobProxy} implementations require when resolving hostnames.
 */
public interface HostResolver {
    /**
     * Resolves a hostname to one or more IP addresses. The iterator over the returned Collection is recommended to reflect the ordering
     * returned by the underlying name lookup service. For example, if a DNS server returns three IP addresses, 1.1.1.1, 2.2.2.2, and
     * 3.3.3.3, corresponding to www.somehost.com, the returned Collection iterator is recommended to iterate in
     * the order [1.1.1.1, 2.2.2.2, 3.3.3.3].
     *
     * @param host host to resolve
     * @return resolved InetAddresses, or an empty collection if no addresses were found
     */
    Collection<InetAddress> resolve(String host);
}
