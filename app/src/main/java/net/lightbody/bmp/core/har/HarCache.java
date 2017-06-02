package net.lightbody.bmp.core.har;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HarCache {
    private volatile HarCacheStatus beforeRequest;
    private volatile HarCacheStatus afterRequest;

    public HarCacheStatus getBeforeRequest() {
        return beforeRequest;
    }

    public void setBeforeRequest(HarCacheStatus beforeRequest) {
        this.beforeRequest = beforeRequest;
    }

    public HarCacheStatus getAfterRequest() {
        return afterRequest;
    }

    public void setAfterRequest(HarCacheStatus afterRequest) {
        this.afterRequest = afterRequest;
    }
}
