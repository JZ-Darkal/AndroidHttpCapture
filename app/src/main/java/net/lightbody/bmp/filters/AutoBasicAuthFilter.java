package net.lightbody.bmp.filters;

import org.littleshoot.proxy.impl.ProxyUtils;

import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * A filter that adds Basic authentication information to non-CONNECT requests. Takes a map of domain names to base64-encoded
 * Basic auth credentials as a constructor parameter. If a key in the map matches the hostname of a filtered request, an Authorization
 * header will be added to the request.
 * <p/>
 * The Authorization header itself is specified in RFC 7235, section 4.2: https://tools.ietf.org/html/rfc7235#section-4.2
 * The Basic authentication scheme is specified in RFC 2617, section 2: https://tools.ietf.org/html/rfc2617#section-2
 */
public class AutoBasicAuthFilter extends HttpsAwareFiltersAdapter {
    private final Map<String, String> credentialsByHostname;

    public AutoBasicAuthFilter(HttpRequest originalRequest, ChannelHandlerContext ctx, Map<String, String> credentialsByHostname) {
        super(originalRequest, ctx);

        this.credentialsByHostname = credentialsByHostname;
    }

    @Override
    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
        if (credentialsByHostname.isEmpty()) {
            return null;
        }

        if (httpObject instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) httpObject;

            // providing authorization during a CONNECT is generally not useful
            if (ProxyUtils.isCONNECT(httpRequest)) {
                return null;
            }

            String hostname = getHost(httpRequest);

            // if there is an entry in the credentials map matching this hostname, add the credentials to the request
            String base64CredentialsForHostname = credentialsByHostname.get(hostname);
            if (base64CredentialsForHostname != null) {
                httpRequest.headers().add(HttpHeaders.Names.AUTHORIZATION, "Basic " + base64CredentialsForHostname);
            }
        }

        return null;
    }
}
