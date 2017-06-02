package net.lightbody.bmp.filters;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.lightbody.bmp.BrowserMobProxyServer;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The filter "driver" that delegates to all chained filters specified by the proxy server.
 */
public class BrowserMobHttpFilterChain extends HttpFiltersAdapter {
    private static final Logger log = LoggerFactory.getLogger(BrowserMobHttpFilterChain.class);

    private final BrowserMobProxyServer proxyServer;

    private final List<HttpFilters> filters;

    public BrowserMobHttpFilterChain(BrowserMobProxyServer proxyServer, HttpRequest originalRequest, ChannelHandlerContext ctx) {
        super(originalRequest, ctx);

        this.proxyServer = proxyServer;

        if (proxyServer.getFilterFactories() != null) {
            filters = new ArrayList<>(proxyServer.getFilterFactories().size());

            // instantiate all HttpFilters using the proxy's filter factories
            for (HttpFiltersSource filterFactory : proxyServer.getFilterFactories()) {
                HttpFilters filter = filterFactory.filterRequest(originalRequest, ctx);
                // allow filter factories to avoid adding a filter on a per-request basis by returning a null
                // HttpFilters instance
                if (filter != null) {
                    filters.add(filter);
                }
            }
        } else {
            filters = Collections.emptyList();
        }
    }

    @Override
    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
        if (proxyServer.isStopped()) {
            log.warn("Aborting request to {} because proxy is stopped", originalRequest.getUri());
            HttpResponse abortedResponse = new DefaultFullHttpResponse(originalRequest.getProtocolVersion(), HttpResponseStatus.SERVICE_UNAVAILABLE);
            HttpHeaders.setContentLength(abortedResponse, 0L);
            return abortedResponse;
        }

        for (HttpFilters filter : filters) {
            try {
                HttpResponse filterResponse = filter.clientToProxyRequest(httpObject);
                if (filterResponse != null) {
                    // if we are short-circuiting the response to an HttpRequest, update ModifiedRequestAwareFilter instances
                    // with this (possibly) modified HttpRequest before returning the short-circuit response
                    if (httpObject instanceof HttpRequest) {
                        updateFiltersWithModifiedResponse((HttpRequest) httpObject);
                    }

                    return filterResponse;
                }
            } catch (RuntimeException e) {
                log.warn("Filter in filter chain threw exception. Filter method may have been aborted.", e);
            }
        }

        // if this httpObject is the HTTP request, set the modified request object on all ModifiedRequestAwareFilter
        // instances, so they have access to all modifications the request filters made while filtering
        if (httpObject instanceof HttpRequest) {
            updateFiltersWithModifiedResponse((HttpRequest) httpObject);
        }

        return null;
    }

    @Override
    public HttpResponse proxyToServerRequest(HttpObject httpObject) {
        for (HttpFilters filter : filters) {
            try {
                HttpResponse filterResponse = filter.proxyToServerRequest(httpObject);
                if (filterResponse != null) {
                    return filterResponse;
                }
            } catch (RuntimeException e) {
                log.warn("Filter in filter chain threw exception. Filter method may have been aborted.", e);
            }
        }

        return null;
    }

    @Override
    public void proxyToServerRequestSending() {
        for (HttpFilters filter : filters) {
            try {
                filter.proxyToServerRequestSending();
            } catch (RuntimeException e) {
                log.warn("Filter in filter chain threw exception. Filter method may have been aborted.", e);
            }
        }
    }


    @Override
    public HttpObject serverToProxyResponse(HttpObject httpObject) {
        HttpObject processedHttpObject = httpObject;

        for (HttpFilters filter : filters) {
            try {
                processedHttpObject = filter.serverToProxyResponse(processedHttpObject);
                if (processedHttpObject == null) {
                    return null;
                }
            } catch (RuntimeException e) {
                log.warn("Filter in filter chain threw exception. Filter method may have been aborted.", e);
            }
        }

        return processedHttpObject;
    }

    @Override
    public void serverToProxyResponseTimedOut() {
        for (HttpFilters filter : filters) {
            try {
                filter.serverToProxyResponseTimedOut();
            } catch (RuntimeException e) {
                log.warn("Filter in filter chain threw exception. Filter method may have been aborted.", e);
            }
        }
    }

    @Override
    public void serverToProxyResponseReceiving() {
        for (HttpFilters filter : filters) {
            try {
                filter.serverToProxyResponseReceiving();
            } catch (RuntimeException e) {
                log.warn("Filter in filter chain threw exception. Filter method may have been aborted.", e);
            }
        }
    }

    @Override
    public InetSocketAddress proxyToServerResolutionStarted(String resolvingServerHostAndPort) {
        InetSocketAddress overrideAddress = null;
        String newServerHostAndPort = resolvingServerHostAndPort;

        for (HttpFilters filter : filters) {
            try {
                InetSocketAddress filterResult = filter.proxyToServerResolutionStarted(newServerHostAndPort);
                if (filterResult != null) {
                    overrideAddress = filterResult;
                    newServerHostAndPort = filterResult.getHostString() + ":" + filterResult.getPort();
                }
            } catch (RuntimeException e) {
                log.warn("Filter in filter chain threw exception. Filter method may have been aborted.", e);
            }
        }

        return overrideAddress;
    }

    @Override
    public void proxyToServerResolutionFailed(String hostAndPort) {
        for (HttpFilters filter : filters) {
            try {
                filter.proxyToServerResolutionFailed(hostAndPort);
            } catch (RuntimeException e) {
                log.warn("Filter in filter chain threw exception. Filter method may have been aborted.", e);
            }
        }
    }

    @Override
    public void proxyToServerResolutionSucceeded(String serverHostAndPort, InetSocketAddress resolvedRemoteAddress) {
        for (HttpFilters filter : filters) {
            try {
                filter.proxyToServerResolutionSucceeded(serverHostAndPort, resolvedRemoteAddress);
            } catch (RuntimeException e) {
                log.warn("Filter in filter chain threw exception. Filter method may have been aborted.", e);
            }
        }

        super.proxyToServerResolutionSucceeded(serverHostAndPort, resolvedRemoteAddress);
    }

    @Override
    public void proxyToServerConnectionStarted() {
        for (HttpFilters filter : filters) {
            try {
                filter.proxyToServerConnectionStarted();
            } catch (RuntimeException e) {
                log.warn("Filter in filter chain threw exception. Filter method may have been aborted.", e);
            }
        }
    }

    @Override
    public void proxyToServerConnectionSSLHandshakeStarted() {
        for (HttpFilters filter : filters) {
            try {
                filter.proxyToServerConnectionSSLHandshakeStarted();
            } catch (RuntimeException e) {
                log.warn("Filter in filter chain threw exception. Filter method may have been aborted.", e);
            }
        }
    }

    @Override
    public void proxyToServerConnectionFailed() {
        for (HttpFilters filter : filters) {
            try {
                filter.proxyToServerConnectionFailed();
            } catch (RuntimeException e) {
                log.warn("Filter in filter chain threw exception. Filter method may have been aborted.", e);
            }
        }
    }

    @Override
    public void proxyToServerConnectionSucceeded(ChannelHandlerContext serverCtx) {
        for (HttpFilters filter : filters) {
            try {
                filter.proxyToServerConnectionSucceeded(serverCtx);
            } catch (RuntimeException e) {
                log.warn("Filter in filter chain threw exception. Filter method may have been aborted.", e);
            }
        }
    }

    @Override
    public void proxyToServerRequestSent() {
        for (HttpFilters filter : filters) {
            try {
                filter.proxyToServerRequestSent();
            } catch (RuntimeException e) {
                log.warn("Filter in filter chain threw exception. Filter method may have been aborted.", e);
            }
        }
    }

    @Override
    public void serverToProxyResponseReceived() {
        for (HttpFilters filter : filters) {
            try {
                filter.serverToProxyResponseReceived();
            } catch (RuntimeException e) {
                log.warn("Filter in filter chain threw exception. Filter method may have been aborted.", e);
            }
        }
    }

    @Override
    public HttpObject proxyToClientResponse(HttpObject httpObject) {
        HttpObject processedHttpObject = httpObject;
        for (HttpFilters filter : filters) {
            try {
                processedHttpObject = filter.proxyToClientResponse(processedHttpObject);
                if (processedHttpObject == null) {
                    return null;
                }
            } catch (RuntimeException e) {
                log.warn("Filter in filter chain threw exception. Filter method may have been aborted.", e);
            }
        }

        return processedHttpObject;
    }

    @Override
    public void proxyToServerConnectionQueued() {
        for (HttpFilters filter : filters) {
            try {
                filter.proxyToServerConnectionQueued();
            } catch (RuntimeException e) {
                log.warn("Filter in filter chain threw exception. Filter method may have been aborted.", e);
            }
        }
    }

    /**
     * Updates {@link ModifiedRequestAwareFilter} filters with the final, modified request after all request filters have
     * processed the request.
     *
     * @param modifiedRequest the modified HttpRequest after all filters have finished processing it
     */
    private void updateFiltersWithModifiedResponse(HttpRequest modifiedRequest) {
        for (HttpFilters filter : filters) {
            if (filter instanceof ModifiedRequestAwareFilter) {
                ModifiedRequestAwareFilter requestCaptureFilter = (ModifiedRequestAwareFilter) filter;
                try {
                    requestCaptureFilter.setModifiedHttpRequest(modifiedRequest);
                } catch (RuntimeException e) {
                    log.warn("ModifiedRequestAwareFilter in filter chain threw exception while setting modified HTTP request.", e);
                }
            }
        }
    }
}
