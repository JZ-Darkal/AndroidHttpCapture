package net.lightbody.bmp.proxy.dns;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * An {@link AdvancedHostResolver} that throws UnsupportedOperationException on all methods except {@link HostResolver#resolve(String)}.
 * Use this class to supply a {@link HostResolver} to {@link net.lightbody.bmp.BrowserMobProxy#setHostNameResolver(AdvancedHostResolver)}
 * if you do not need {@link AdvancedHostResolver} functionality.
 */
public abstract class AbstractBasicHostResolver implements AdvancedHostResolver {
    @Override
    public void remapHosts(Map<String, String> hostRemappings) {
        throw new UnsupportedOperationException(new Throwable().getStackTrace()[0].getMethodName() + " is not supported by this host resolver (" + this.getClass().getName() + ")");
    }

    @Override
    public void remapHost(String originalHost, String remappedHost) {
        throw new UnsupportedOperationException(new Throwable().getStackTrace()[0].getMethodName() + " is not supported by this host resolver (" + this.getClass().getName() + ")");
    }

    @Override
    public void removeHostRemapping(String originalHost) {
        throw new UnsupportedOperationException(new Throwable().getStackTrace()[0].getMethodName() + " is not supported by this host resolver (" + this.getClass().getName() + ")");
    }

    @Override
    public void clearHostRemappings() {
        throw new UnsupportedOperationException(new Throwable().getStackTrace()[0].getMethodName() + " is not supported by this host resolver (" + this.getClass().getName() + ")");
    }

    @Override
    public Map<String, String> getHostRemappings() {
        throw new UnsupportedOperationException(new Throwable().getStackTrace()[0].getMethodName() + " is not supported by this host resolver (" + this.getClass().getName() + ")");
    }

    @Override
    public Collection<String> getOriginalHostnames(String remappedHost) {
        throw new UnsupportedOperationException(new Throwable().getStackTrace()[0].getMethodName() + " is not supported by this host resolver (" + this.getClass().getName() + ")");
    }

    @Override
    public void clearDNSCache() {
        throw new UnsupportedOperationException(new Throwable().getStackTrace()[0].getMethodName() + " is not supported by this host resolver (" + this.getClass().getName() + ")");
    }

    @Override
    public void setPositiveDNSCacheTimeout(int timeout, TimeUnit timeUnit) {
        throw new UnsupportedOperationException(new Throwable().getStackTrace()[0].getMethodName() + " is not supported by this host resolver (" + this.getClass().getName() + ")");
    }

    @Override
    public void setNegativeDNSCacheTimeout(int timeout, TimeUnit timeUnit) {
        throw new UnsupportedOperationException(new Throwable().getStackTrace()[0].getMethodName() + " is not supported by this host resolver (" + this.getClass().getName() + ")");
    }
}
