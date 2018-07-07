package net.lightbody.bmp.proxy.dns;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Base class that provides host name remapping capabilities for AdvancedHostResolvers. Subclasses must implement {@link #resolveRemapped(String)}
 * instead of {@link net.lightbody.bmp.proxy.dns.HostResolver#resolve(String)}, which takes the remapped host as the input parameter.
 */
public abstract class AbstractHostNameRemapper implements AdvancedHostResolver {
    /**
     * Host name remappings, maintained as a reference to an ImmutableMap. The ImmutableMap type is specified explicitly because ImmutableMap
     * guarantees the iteration order of the map's entries. Specifying ImmutableMap also makes clear that the underlying map will never change,
     * and that any modifications to the host name remappings will result in an entirely new map.
     *
     * The current implementation does not actually use any of the special features of AtomicReference, but it does rely on synchronizing on
     * the AtomicReference when performing write operations. It could be replaced by a volatile reference to a Map and separate lock object.
     */
    private final AtomicReference<ImmutableMap<String, String>> remappedHostNames = new AtomicReference<>(ImmutableMap.<String, String>of());

    @Override
    public void remapHosts(Map<String, String> hostRemappings) {
        synchronized (remappedHostNames) {
            ImmutableMap<String, String> newRemappings = ImmutableMap.copyOf(hostRemappings);

            remappedHostNames.set(newRemappings);
        }
    }

    @Override
    public void remapHost(String originalHost, String remappedHost) {
        synchronized (remappedHostNames) {
            Map<String, String> currentHostRemappings = remappedHostNames.get();

            // use a LinkedHashMap to build the new remapping, to avoid duplicate key issues if the originalHost is already in the map
            Map<String, String> builderMap = Maps.newLinkedHashMap(currentHostRemappings);
            builderMap.remove(originalHost);
            builderMap.put(originalHost, remappedHost);

            ImmutableMap<String, String> newRemappings = ImmutableMap.copyOf(builderMap);

            remappedHostNames.set(newRemappings);
        }
    }

    @Override
    public void removeHostRemapping(String originalHost) {
        synchronized (remappedHostNames) {
            Map<String, String> currentHostRemappings = remappedHostNames.get();
            if (currentHostRemappings.containsKey(originalHost)) {
                // use a LinkedHashMap to build the new remapping, to take advantage of the remove() method
                Map<String, String> builderMap = Maps.newLinkedHashMap(currentHostRemappings);
                builderMap.remove(originalHost);

                ImmutableMap<String, String> newRemappings = ImmutableMap.copyOf(builderMap);

                remappedHostNames.set(newRemappings);
            }
        }
    }

    @Override
    public void clearHostRemappings() {
        synchronized (remappedHostNames) {
            remappedHostNames.set(ImmutableMap.<String, String>of());
        }
    }

    @Override
    public Map<String, String> getHostRemappings() {
        return remappedHostNames.get();
    }

    @Override
    public Collection<String> getOriginalHostnames(String remappedHost) {
        //TODO: implement this using a reverse mapping multimap that is guarded by the same lock as remappedHostNames, since this method will likely be called
        // very often when forging certificates
        List<String> originalHostnames = new ArrayList<>();

        Map<String, String> currentRemappings = remappedHostNames.get();
        for (Map.Entry<String, String> entry : currentRemappings.entrySet()) {
            if (entry.getValue().equals(remappedHost)) {
                originalHostnames.add(entry.getKey());
            }
        }

        return originalHostnames;
    }

    /**
     * Applies this class's host name remappings to the specified original host, returning the remapped host name (if any), or the originalHost
     * if there is no remapped host name.
     *
     * @param originalHost original host name to resolve
     * @return a remapped host, or the original host if no mapping exists
     */
    public String applyRemapping(String originalHost) {
        String remappedHost = remappedHostNames.get().get(originalHost);

        if (remappedHost != null) {
            return remappedHost;
        } else {
            return originalHost;
        }
    }

    /**
     * Resolves the specified remapped host. Subclasses should provide resolution by implementing this method, rather than overriding
     * {@link net.lightbody.bmp.proxy.dns.HostResolver#resolve(String)}.
     *
     * @param remappedHost remapped hostname to resolve
     * @return resolved InetAddresses, or an empty list if no addresses were found
     */
    public abstract Collection<InetAddress> resolveRemapped(String remappedHost);

    /**
     * Retrieves the remapped hostname and resolves it using {@link #resolveRemapped(String)}.
     *
     * @param originalHost original hostname to resolve
     * @return InetAddresses resolved from the remapped hostname, or an empty list if no addresses were found
     */
    @Override
    public Collection<InetAddress> resolve(String originalHost) {
        String remappedHost = applyRemapping(originalHost);

        return resolveRemapped(remappedHost);
    }
}
