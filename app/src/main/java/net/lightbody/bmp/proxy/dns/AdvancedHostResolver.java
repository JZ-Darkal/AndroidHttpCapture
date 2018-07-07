package net.lightbody.bmp.proxy.dns;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This interface defines the "core" DNS-manipulation functionality that BrowserMob Proxy supports, in addition to the basic name resolution
 * capability defined in {@link net.lightbody.bmp.proxy.dns.HostResolver}. AdvancedHostResolvers should apply any remappings before attempting
 * to resolve the hostname in the {@link HostResolver#resolve(String)} method.
 */
public interface AdvancedHostResolver extends HostResolver {
    /**
     * Replaces the host remappings in the existing list of remappings (if any) with the specified remappings. The remappings will be
     * applied in the order specified by the Map's iterator.
     * <p/>
     * <b>Note:</b> The original hostnames must exactly match the requested hostname. It is not a domain or regular expression match.
     *
     * @param hostRemappings Map of {@code <original hostname, remapped hostname>}
     */
    void remapHosts(Map<String, String> hostRemappings);

    /**
     * Remaps an individual host. If there are any existing remappings, the new remapping will be applied last, after all existing
     * remappings are applied. If there is already a remapping for the specified originalHost, it will be removed before
     * the new remapping is added to the end of the host remapping list (and will therefore be the last remapping applied).
     *
     * @param originalHost Original host to remap. Must exactly match the requested hostname (not a domain or regular expression match).
     * @param remappedHost hostname that will replace originalHost
     */
    void remapHost(String originalHost, String remappedHost);

    /**
     * Removes the specified host remapping. If the remapping does not exist, this method has no effect.
     *
     * @param originalHost currently-remapped hostname
     */
    void removeHostRemapping(String originalHost);

    /**
     * Removes all hostname remappings.
     */
    void clearHostRemappings();

    /**
     * Returns all host remappings in effect. Iterating over the returned Map is guaranteed to return remappings in the order in which the
     * remappings are actually applied.
     *
     * @return Map of {@code <original hostname, remapped hostname>}
     */
    Map<String, String> getHostRemappings();

    /**
     * Returns the original address or addresses that are remapped to the specified remappedHost. Iterating over the returned Collection is
     * guaranteed to return original mappings in the order in which the remappings are applied.
     *
     * @param remappedHost remapped hostname
     * @return original hostnames that are remapped to the specified remappedHost, or an empty Collection if no remapping is defined to the remappedHost
     */
    Collection<String> getOriginalHostnames(String remappedHost);

    /**
     * Clears both the positive (successful DNS lookups) and negative (failed DNS lookups) cache.
     */
    void clearDNSCache();

    /**
     * Sets the positive (successful DNS lookup) timeout when making DNS lookups.
     * <p/>
     * <b>Note:</b> The timeUnit parameter does not guarantee the specified precision; implementations may need to reduce precision, depending on the underlying
     * DNS implementation. For example, the Oracle JVM's DNS cache only supports timeouts in whole seconds, so specifying a timeout of 1200ms will result
     * in a timeout of 1 second.
     *
     * @param timeout maximum lookup time
     * @param timeUnit units of the timeout value
     */
    void setPositiveDNSCacheTimeout(int timeout, TimeUnit timeUnit);

    /**
     * Sets the negative (failed DNS lookup) timeout when making DNS lookups.
     * <p/>
     * <b>Note:</b> The timeUnit parameter does not guarantee the specified precision; implementations may need to reduce precision, depending on the underlying
     * DNS implementation. For example, the Oracle JVM's DNS cache only supports timeouts in whole seconds, so specifying a timeout of 1200ms will result
     * in a timeout of 1 second.
     *
     * @param timeout maximum lookup time
     * @param timeUnit units of the timeout value
     */
    void setNegativeDNSCacheTimeout(int timeout, TimeUnit timeUnit);
}
