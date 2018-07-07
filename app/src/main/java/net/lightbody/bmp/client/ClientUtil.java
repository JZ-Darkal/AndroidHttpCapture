package net.lightbody.bmp.client;

import com.google.common.collect.ImmutableList;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.proxy.dns.AdvancedHostResolver;
import net.lightbody.bmp.proxy.dns.ChainedHostResolver;
import net.lightbody.bmp.proxy.dns.DnsJavaResolver;
import net.lightbody.bmp.proxy.dns.NativeCacheManipulatingResolver;
import net.lightbody.bmp.proxy.dns.NativeResolver;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * A utility class with convenience methods for clients using BrowserMob Proxy in embedded mode.
 */
public class ClientUtil {
    /**
     * Creates a {@link net.lightbody.bmp.proxy.dns.NativeCacheManipulatingResolver} instance that can be used when
     * calling {@link net.lightbody.bmp.BrowserMobProxy#setHostNameResolver(net.lightbody.bmp.proxy.dns.AdvancedHostResolver)}.
     *
     * @return a new NativeCacheManipulatingResolver
     */
    public static AdvancedHostResolver createNativeCacheManipulatingResolver() {
        return new NativeCacheManipulatingResolver();
    }

    /**
     * Creates a {@link net.lightbody.bmp.proxy.dns.NativeResolver} instance that <b>does not support cache manipulation</b> that can be used when
     * calling {@link net.lightbody.bmp.BrowserMobProxy#setHostNameResolver(net.lightbody.bmp.proxy.dns.AdvancedHostResolver)}.
     *
     * @return a new NativeResolver
     */
    public static AdvancedHostResolver createNativeResolver() {
        return new NativeResolver();
    }

    /**
     * Creates a {@link net.lightbody.bmp.proxy.dns.DnsJavaResolver} instance that can be used when
     * calling {@link net.lightbody.bmp.BrowserMobProxy#setHostNameResolver(net.lightbody.bmp.proxy.dns.AdvancedHostResolver)}.
     *
     * @return a new DnsJavaResolver
     * @deprecated The dnsjava resolver has been deprecated in favor of the standard JVM resolver and will be removed in BMP >2.1.
     */
    public static AdvancedHostResolver createDnsJavaResolver() {
        return new DnsJavaResolver();
    }

    /**
     * Creates a {@link net.lightbody.bmp.proxy.dns.ChainedHostResolver} instance that first attempts to resolve a hostname using a
     * {@link net.lightbody.bmp.proxy.dns.DnsJavaResolver}, then uses {@link net.lightbody.bmp.proxy.dns.NativeCacheManipulatingResolver}.
     * Can be used when calling {@link net.lightbody.bmp.BrowserMobProxy#setHostNameResolver(net.lightbody.bmp.proxy.dns.AdvancedHostResolver)}.
     *
     * @return a new ChainedHostResolver that resolves addresses first using a DnsJavaResolver, then using a NativeCacheManipulatingResolver
     * @deprecated The dnsjava resolver has been deprecated in favor of the standard JVM resolver and will be removed in BMP >2.1.
     */
    public static AdvancedHostResolver createDnsJavaWithNativeFallbackResolver() {
        return new ChainedHostResolver(ImmutableList.of(new DnsJavaResolver(), new NativeCacheManipulatingResolver()));
    }

    /**
     * Attempts to retrieve a "connectable" address for this device that other devices on the network can use to connect to a local proxy.
     * This is a "reasonable guess" that is suitable in many (but not all) common scenarios.
     * TODO: define the algorithm used to discover a "connectable" local host
     *
     * @return a "reasonable guess" at an address that can be used by other machines on the network to reach this host
     */
    public static InetAddress getConnectableAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Could not resolve localhost", e);
        }
    }
}
