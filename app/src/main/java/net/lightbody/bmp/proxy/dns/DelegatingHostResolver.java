package net.lightbody.bmp.proxy.dns;

import com.google.common.collect.Iterables;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

/**
 * A LittleProxy HostResolver that delegates to the specified {@link net.lightbody.bmp.proxy.dns.AdvancedHostResolver} instance. This class
 * serves as a bridge between {@link AdvancedHostResolver} and {@link org.littleshoot.proxy.HostResolver}.
*/
public class DelegatingHostResolver implements org.littleshoot.proxy.HostResolver {
    private volatile AdvancedHostResolver resolver;

    /**
     * Creates a new resolver that will delegate to the specified resolver.
     *
     * @param resolver HostResolver to delegate to
     */
    public DelegatingHostResolver(AdvancedHostResolver resolver) {
        this.resolver = resolver;
    }

    public AdvancedHostResolver getResolver() {
        return resolver;
    }

    public void setResolver(AdvancedHostResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public InetSocketAddress resolve(String host, int port) throws UnknownHostException {
        Collection<InetAddress> resolvedAddresses = resolver.resolve(host);
        if (!resolvedAddresses.isEmpty()) {
            InetAddress resolvedAddress = Iterables.get(resolvedAddresses, 0);
            return new InetSocketAddress(resolvedAddress, port);
        }

        // no address found by the resolver
        throw new UnknownHostException(host);
    }
}
