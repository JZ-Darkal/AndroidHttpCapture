package net.lightbody.bmp.proxy.dns;

import com.google.common.net.InetAddresses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Cache;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * An {@link net.lightbody.bmp.proxy.dns.AdvancedHostResolver} that uses dnsjava to perform DNS lookups. This implementation provides full
 * cache manipulation capabilities.
 *
 * @deprecated The dnsjava resolver has been deprecated in favor of the standard JVM resolver and will be removed in BMP >2.1.
 */
public class DnsJavaResolver extends AbstractHostNameRemapper implements AdvancedHostResolver {
    private static final Logger log = LoggerFactory.getLogger(DnsJavaResolver.class);
    /**
     * Maximum number of times to retry a DNS lookup due to a failure to connect to the DNS server.
     */
    private static final int DNS_NETWORK_FAILURE_RETRY_COUNT = 5;
    /**
     * DNS cache used for dnsjava lookups.
     */
    private final Cache cache = new Cache();

    @Override
    public void clearDNSCache() {
        cache.clearCache();
    }

    @Override
    public void setPositiveDNSCacheTimeout(int timeout, TimeUnit timeUnit) {
        cache.setMaxCache((int) TimeUnit.SECONDS.convert(timeout, timeUnit));
    }

    @Override
    public void setNegativeDNSCacheTimeout(int timeout, TimeUnit timeUnit) {
        cache.setMaxNCache((int) TimeUnit.SECONDS.convert(timeout, timeUnit));
    }

    @Override
    public Collection<InetAddress> resolveRemapped(String remappedHost) {
        // special case for IP literals: return the InetAddress without doing a dnsjava lookup. dnsjava seems to handle ipv4 literals
        // reasonably well, but does not handle ipv6 literals (with or without [] brackets) correctly.
        // note this does not work properly for ipv6 literals with a scope identifier, which is a known issue for InetAddresses.isInetAddress().
        // (dnsjava also handles the situation incorrectly)
        if (InetAddresses.isInetAddress(remappedHost)) {
            return Collections.singletonList(InetAddresses.forString(remappedHost));
        }

        // retrieve IPv4 addresses, then retrieve IPv6 addresses only if no IPv4 addresses are found. the current implementation always uses the
        // first returned address, so there is no need to look for IPv6 addresses if an IPv4 address is found.
        Collection<InetAddress> ipv4addresses = resolveHostByType(remappedHost, Type.A);

        if (!ipv4addresses.isEmpty()) {
            return ipv4addresses;
        } else {
            return resolveHostByType(remappedHost, Type.AAAA);
        }
    }

    /**
     * Resolves the specified host using dnsjava, retrieving addresses of the specified type.
     *
     * @param host hostname to resolve
     * @param type one of {@link org.xbill.DNS.Type}, typically {@link org.xbill.DNS.Type#A} (IPv4) or {@link org.xbill.DNS.Type#AAAA} (IPv6).
     * @return resolved addresses, or an empty collection if no addresses could be resolved
     */
    protected Collection<InetAddress> resolveHostByType(String host, int type) {
        Lookup lookup;
        try {
            lookup = new Lookup(host, type, DClass.IN);
        } catch (TextParseException e) {
            return Collections.emptyList();
        }

        lookup.setCache(cache);

        // we set the retry count to -1 because we want the first execution not be counted as a retry.
        int retryCount = -1;
        Record[] records;

        // we iterate while the status is TRY_AGAIN and DNS_NETWORK_FAILURE_RETRY_COUNT is not exceeded
        do {
            records = lookup.run();
            retryCount++;
        } while (lookup.getResult() == Lookup.TRY_AGAIN && retryCount < DNS_NETWORK_FAILURE_RETRY_COUNT);

        if (records == null) {
            // no records found, so could not resolve host
            return Collections.emptyList();
        }

        // convert the records we found into IPv4/IPv6 InetAddress objects
        List<InetAddress> addrList = new ArrayList<InetAddress>(records.length);

        // the InetAddresses returned by dnsjava include the trailing dot, e.g. "www.google.com." -- use the passed-in (or remapped) host value instead
        for (Record record : records) {
            if (record instanceof ARecord) {
                ARecord ipv4Record = (ARecord) record;

                try {
                    InetAddress resolvedAddress = InetAddress.getByAddress(host, ipv4Record.getAddress().getAddress());

                    addrList.add(resolvedAddress);
                } catch (UnknownHostException e) {
                    // this should never happen, unless there is a bug in dnsjava
                    log.warn("dnsjava resolver returned an invalid InetAddress for host: " + host, e);
                    continue;
                }
            } else if (record instanceof AAAARecord) {
                AAAARecord ipv6Record = (AAAARecord) record;

                try {
                    InetAddress resolvedAddress = InetAddress.getByAddress(host, ipv6Record.getAddress().getAddress());

                    addrList.add(resolvedAddress);
                } catch (UnknownHostException e) {
                    // this should never happen, unless there is a bug in dnsjava
                    log.warn("dnsjava resolver returned an invalid InetAddress for host: " + host, e);
                    continue;
                }
            }
        }

        return addrList;
    }
}
