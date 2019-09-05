package net.lightbody.bmp.filters;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.net.HostAndPort;

import org.littleshoot.proxy.HttpFiltersAdapter;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Caches hostname resolutions reported by the {@link org.littleshoot.proxy.HttpFilters#proxyToServerResolutionSucceeded(String, InetSocketAddress)}
 * filter method. Allows access to the resolved IP address on subsequent requests, when the address is not re-resolved because
 * the connection has already been established.
 */
public class ResolvedHostnameCacheFilter extends HttpFiltersAdapter {
    /**
     * The maximum amount of time to save host name resolution information. This is done in order to populate the server IP address field in the
     * har. Unfortunately there is not currently any way to determine the remote IP address of a keep-alive connection in a filter, so caching the
     * resolved hostnames gives a generally-reasonable best guess.
     */
    private static final int RESOLVED_ADDRESSES_EVICTION_SECONDS = 600;

    /**
     * Concurrency of the resolvedAddresses map. Should be approximately equal to the maximum number of simultaneous connection
     * attempts (but not necessarily simultaneous connections). A lower value will inhibit performance.
     */
    private static final int RESOLVED_ADDRESSES_CONCURRENCY_LEVEL = 50;

    /**
     * A {@code Map<hostname, IP address>} that provides a reasonable estimate of the upstream server's IP address for keep-alive connections.
     * The expiration time is renewed after each access, rather than after each write, so if the connection is consistently kept alive and used,
     * the cached IP address will not be evicted.
     */
    private static final Cache<String, String> resolvedAddresses =
            CacheBuilder.newBuilder()
                    .expireAfterAccess(RESOLVED_ADDRESSES_EVICTION_SECONDS, TimeUnit.SECONDS)
                    .concurrencyLevel(RESOLVED_ADDRESSES_CONCURRENCY_LEVEL)
                    .build();

    public ResolvedHostnameCacheFilter(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        super(originalRequest, ctx);
    }

    /**
     * Returns the (cached) address that was previously resolved for the specified host.
     *
     * @param host hostname that was previously resolved (without a port)
     * @return the resolved IP address for the host, or null if the resolved address is not in the cache
     */
    public static String getPreviouslyResolvedAddressForHost(String host) {
        return resolvedAddresses.getIfPresent(host);
    }

    @Override
    public void proxyToServerResolutionSucceeded(String serverHostAndPort, InetSocketAddress resolvedRemoteAddress) {
        // the address *should* always be resolved at this point
        InetAddress resolvedAddress = resolvedRemoteAddress.getAddress();

        if (resolvedAddress != null) {
            // place the resolved host into the hostname cache, so subsequent requests will be able to identify the IP address
            HostAndPort parsedHostAndPort = HostAndPort.fromString(serverHostAndPort);
            String host = parsedHostAndPort.getHost();

            if (host != null && !host.isEmpty()) {
                resolvedAddresses.put(host, resolvedAddress.getHostAddress());
            }
        }
    }
}
