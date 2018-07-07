package net.lightbody.bmp;

import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.mitm.TrustSource;
import net.lightbody.bmp.proxy.BlacklistEntry;
import net.lightbody.bmp.proxy.CaptureType;
import net.lightbody.bmp.proxy.auth.AuthType;
import net.lightbody.bmp.proxy.dns.AdvancedHostResolver;
import org.littleshoot.proxy.HttpFiltersSource;
import org.littleshoot.proxy.MitmManager;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface BrowserMobProxy {
    /**
     * Starts the proxy on port 0 (a JVM-selected open port). The proxy will bind the listener to the wildcard address (0:0:0:0 - all network interfaces).
     *
     * @throws java.lang.IllegalStateException if the proxy has already been started
     */
    void start();

    /**
     * Starts the proxy on the specified port. The proxy will bind the listener to the wildcard address (0:0:0:0 - all network interfaces).
     *
     * @param port port to listen on
     * @throws java.lang.IllegalStateException if the proxy has already been started
     */
    void start(int port);

    /**
     * Starts the proxy on the specified port. The proxy will listen for connections on the network interface specified by the bindAddress, and will
     * also initiate connections to upstream servers on the same network interface.
     *
     * @param port port to listen on
     * @param bindAddress address of the network interface on which the proxy will listen for connections and also attempt to connect to upstream servers.
     * @throws java.lang.IllegalStateException if the proxy has already been started
     */
    void start(int port, InetAddress bindAddress);

    /**
     * Starts the proxy on the specified port. The proxy will listen for connections on the network interface specified by the clientBindAddress, and will
     * initiate connections to upstream servers from the network interface specified by the serverBindAddress.
     *
     * @param port port to listen on
     * @param clientBindAddress address of the network interface on which the proxy will listen for connections
     * @param serverBindAddress address of the network interface on which the proxy will connect to upstream servers
     * @throws java.lang.IllegalStateException if the proxy has already been started
     */
    void start(int port, InetAddress clientBindAddress, InetAddress serverBindAddress);

    /**
     * Returns true if the proxy is started and listening for connections, otherwise false.
     */
    boolean isStarted();

    /**
     * Stops accepting new client connections and initiates a graceful shutdown of the proxy server, waiting up to 5 seconds for network
     * traffic to stop. If the proxy was previously stopped or aborted, this method has no effect.
     *
     * @throws java.lang.IllegalStateException if the proxy has not been started.
     */
    void stop();
    
    /**
     * Like {@link #stop()}, shuts down the proxy server and no longer accepts incoming connections, but does not wait for any existing
     * network traffic to cease. Any existing connections to clients or to servers may be force-killed immediately.
     * If the proxy was previously stopped or aborted, this method has no effect.
     *
     * @throws java.lang.IllegalStateException if the proxy has not been started
     */
    void abort();

    /**
     * Returns the address of the network interface on which the proxy is listening for client connections.
     *
     * @return the client bind address, or null if the proxy has not been started
     */
    InetAddress getClientBindAddress();

    /**
     * Returns the actual port on which the proxy is listening for client connections.
     *
     * @throws java.lang.IllegalStateException if the proxy has not been started
     */
    int getPort();

    /**
     * Returns the address address of the network interface the proxy will use to initiate upstream connections. If no server bind address
     * has been set, this method returns null, even if the proxy has been started.
     *
     * @return server bind address if one has been set, otherwise null
     */
    InetAddress getServerBindAddress();

    /**
     * Retrieves the current HAR.
     *
     * @return current HAR, or null if HAR capture is not enabled
     */
    Har getHar();

    Har getHar(Set<String> pageRef);

    Har getHar(String pageRef);

    /**
     * Starts a new HAR file with the default page name (see {@link #newPage()}. Enables HAR capture if it was not previously enabled.
     *
     * @return existing HAR file, or null if none exists or HAR capture was disabled
     */
    Har newHar();

    /**
     * Starts a new HAR file with the specified initialPageRef as the page name and page title. Enables HAR capture if it was not previously enabled.
     *
     * @param initialPageRef initial page name of the new HAR file
     * @return existing HAR file, or null if none exists or HAR capture was disabled
     */
    Har newHar(String initialPageRef);

    /**
     * Starts a new HAR file with the specified page name and page title. Enables HAR capture if it was not previously enabled.
     *
     * @param initialPageRef initial page name of the new HAR file
     * @param initialPageTitle initial page title of the new HAR file
     * @return existing HAR file, or null if none exists or HAR capture was disabled
     */
    Har newHar(String initialPageRef, String initialPageTitle);

    /**
     * Sets the data types that will be captured in the HAR file for future requests. Replaces any existing capture types with the specified
     * capture types. A null or empty set will not disable HAR capture, but will disable collection of
     * additional {@link net.lightbody.bmp.proxy.CaptureType} data types. {@link net.lightbody.bmp.proxy.CaptureType} provides several
     * convenience methods to retrieve commonly-used capture settings.
     * <p/>
     * <b>Note:</b> HAR capture must still be explicitly enabled via {@link #newHar()} or {@link #newHar(String)} to begin capturing
     * any request and response contents.
     *
     * @param captureTypes HAR data types to capture
     */
    void setHarCaptureTypes(Set<CaptureType> captureTypes);

    /**
     * Sets the data types that will be captured in the HAR file for future requests. Replaces any existing capture types with the specified
     * capture types. A null or empty set will not disable HAR capture, but will disable collection of
     * additional {@link net.lightbody.bmp.proxy.CaptureType} data types. {@link net.lightbody.bmp.proxy.CaptureType} provides several
     * convenience methods to retrieve commonly-used capture settings.
     * <p/>
     * <b>Note:</b> HAR capture must still be explicitly enabled via {@link #newHar()} or {@link #newHar(String)} to begin capturing
     * any request and response contents.
     *
     * @param captureTypes HAR data types to capture
     */
    void setHarCaptureTypes(CaptureType... captureTypes);

    /**
     * @return A copy of HAR capture types currently in effect. The EnumSet cannot be used to modify the HAR capture types currently in effect.
     */
    EnumSet<CaptureType> getHarCaptureTypes();

    /**
     * Enables the specified HAR capture types. Does not replace or disable any other capture types that may already be enabled.
     *
     * @param captureTypes capture types to enable
     */
    void enableHarCaptureTypes(Set<CaptureType> captureTypes);

    /**
     * Enables the specified HAR capture types. Does not replace or disable any other capture types that may already be enabled.
     *
     * @param captureTypes capture types to enable
     */
    void enableHarCaptureTypes(CaptureType... captureTypes);

    /**
     * Disables the specified HAR capture types. Does not replace or disable any other capture types that may already be enabled.
     *
     * @param captureTypes capture types to disable
     */
    void disableHarCaptureTypes(Set<CaptureType> captureTypes);

    /**
     * Disables the specified HAR capture types. Does not replace or disable any other capture types that may already be enabled.
     *
     * @param captureTypes capture types to disable
     */
    void disableHarCaptureTypes(CaptureType... captureTypes);

    /**
     * Starts a new HAR page using the default page naming convention. The default page naming convention is "Page #", where "#" resets to 1
     * every time {@link #newHar()} or {@link #newHar(String)} is called, and increments on every subsequent call to {@link #newPage()} or
     * {@link #newHar(String)}. Populates the {@link net.lightbody.bmp.core.har.HarPageTimings#onLoad} value based on the amount of time
     * the current page has been captured.
     *
     * @return the HAR as it existed immediately after ending the current page
     * @throws java.lang.IllegalStateException if HAR capture has not been enabled via {@link #newHar()} or {@link #newHar(String)}
     */
    Har newPage();

    /**
     * Starts a new HAR page using the specified pageRef as the page name and the page title. Populates the
     * {@link net.lightbody.bmp.core.har.HarPageTimings#onLoad} value based on the amount of time the current page has been captured.
     *
     * @param pageRef name of the new page
     * @return the HAR as it existed immediately after ending the current page
     * @throws java.lang.IllegalStateException if HAR capture has not been enabled via {@link #newHar()} or {@link #newHar(String)}
     */
    Har newPage(String pageRef);

    /**
     * Starts a new HAR page using the specified pageRef as the page name and the pageTitle as the page title. Populates the
     * {@link net.lightbody.bmp.core.har.HarPageTimings#onLoad} value based on the amount of time the current page has been captured.
     *
     * @param pageRef name of the new page
     * @param pageTitle title of the new page
     * @return the HAR as it existed immediately after ending the current page
     * @throws java.lang.IllegalStateException if HAR capture has not been enabled via {@link #newHar()} or {@link #newHar(String)}
     */
    Har newPage(String pageRef, String pageTitle);

    /**
     * Stops capturing traffic in the HAR. Populates the {@link net.lightbody.bmp.core.har.HarPageTimings#onLoad} value for the current page
     * based on the amount of time it has been captured.
     *
     * @return the existing HAR
     */
    Har endHar();

    /**
     * Sets the maximum bandwidth to consume when reading server responses.
     *
     * @param bytesPerSecond maximum bandwidth, in bytes per second
     */
    void setReadBandwidthLimit(long bytesPerSecond);

    /**
     * Returns the current bandwidth limit for reading, in bytes per second.
     */
    long getReadBandwidthLimit();

    /**
     * Sets the maximum bandwidth to consume when sending requests to servers.
     *
     * @param bytesPerSecond maximum bandwidth, in bytes per second
     */
    void setWriteBandwidthLimit(long bytesPerSecond);

    /**
     * Returns the current bandwidth limit for writing, in bytes per second.
     */
    long getWriteBandwidthLimit();

    /**
     * The minimum amount of time that will elapse between the time the proxy begins receiving a response from the server and the time the
     * proxy begins sending the response to the client.
     *
     * @param latency minimum latency, or 0 for no minimum
     * @param timeUnit TimeUnit for the latency
     */
    void setLatency(long latency, TimeUnit timeUnit);

    /**
     * Maximum amount of time to wait to establish a connection to a remote server. If the connection has not been established within the
     * specified time, the proxy will respond with an HTTP 502 Bad Gateway. The default value is 60 seconds.
     *
     * @param connectionTimeout maximum time to wait to establish a connection to a server, or 0 to wait indefinitely
     * @param timeUnit TimeUnit for the connectionTimeout
     */
    void setConnectTimeout(int connectionTimeout, TimeUnit timeUnit);

    /**
     * Maximum amount of time to allow a connection to remain idle. A connection becomes idle when it has not received data from a server
     * within the the specified timeout. If the proxy has not yet begun to forward the response to the client, the proxy
     * will respond with an HTTP 504 Gateway Timeout. If the proxy has already started forwarding the response to the client, the
     * connection to the client <i>may</i> be closed abruptly. The default value is 60 seconds.
     *
     * @param idleConnectionTimeout maximum time to allow a connection to remain idle, or 0 to wait indefinitely.
     * @param timeUnit TimeUnit for the idleConnectionTimeout
     */
    void setIdleConnectionTimeout(int idleConnectionTimeout, TimeUnit timeUnit);

    /**
     * Maximum amount of time to wait for an HTTP response from the remote server after the request has been sent in its entirety. The HTTP
     * request must complete within the specified time. If the proxy has not yet begun to forward the response to the client, the proxy
     * will respond with an HTTP 504 Gateway Timeout. If the proxy has already started forwarding the response to the client, the
     * connection to the client <i>may</i> be closed abruptly. The default value is 0 (wait indefinitely).
     *
     * @param requestTimeout maximum time to wait for an HTTP response, or 0 to wait indefinitely
     * @param timeUnit TimeUnit for the requestTimeout
     */
    void setRequestTimeout(int requestTimeout, TimeUnit timeUnit);

    /**
     * Enables automatic authorization for the specified domain and auth type. Every request sent to the specified domain will contain the
     * specified authorization information.
     *
     * @param domain domain automatically send authorization information to
     * @param username authorization username
     * @param password authorization password
     * @param authType authorization type
     */
    void autoAuthorization(String domain, String username, String password, AuthType authType);

    /**
     * Stops automatic authorization for the specified domain.
     *
     * @param domain domain to stop automatically sending authorization information to
     */
    void stopAutoAuthorization(String domain);

    /**
     * Enables chained proxy authorization using the Proxy-Authorization header described in RFC 7235, section 4.4 (https://tools.ietf.org/html/rfc7235#section-4.4).
     * Currently, only {@link AuthType#BASIC} authentication is supported.
     *
     * @param username the username to use to authenticate with the chained proxy
     * @param password the password to use to authenticate with the chained proxy
     * @param authType the auth type to use (currently, must be BASIC)
     */
    void chainedProxyAuthorization(String username, String password, AuthType authType);

    /**
     * Adds a rewrite rule for the specified URL-matching regular expression. If there are any existing rewrite rules, the new rewrite
     * rule will be applied last, after all other rewrite rules are applied. The specified urlPattern will be replaced with the specified
     * replacement expression. The urlPattern is treated as a Java regular expression and must be properly escaped (see {@link java.util.regex.Pattern}).
     * The replacementExpression may consist of capture groups specified in the urlPattern, denoted
     * by a $ (see {@link java.util.regex.Matcher#appendReplacement(StringBuffer, String)}.
     * <p/>
     * For HTTP requests (not HTTPS), if the hostname and/or port is changed as a result of a rewrite rule, the Host header of the request will be modified
     * to reflect the updated hostname/port. For HTTPS requests, the host and port cannot be changed by rewrite rules
     * (use {@link #getHostNameResolver()} and {@link AdvancedHostResolver#remapHost(String, String)} to direct HTTPS requests
     * to a different host).
     * <p/>
     * <b>Note:</b> The rewriting applies to the entire URL, including scheme (http:// or https://), hostname/address, port, and query string. Note that this means
     * a urlPattern of {@code "http://www\.website\.com/page"} will NOT match {@code http://www.website.com:80/page}.
     * <p/>
     * For example, the following rewrite rule:
     *
     * <pre>   {@code proxy.rewriteUrl("http://www\\.(yahoo|bing)\\.com/\\?(\\w+)=(\\w+)", "http://www.google.com/?originalDomain=$1&$2=$3");}</pre>
     *
     * will match an HTTP request (but <i>not</i> HTTPS!) to www.yahoo.com or www.bing.com with exactly 1 query parameter,
     * and replace it with a call to www.google.com with an 'originalDomain' query parameter, as well as the original query parameter.
     * <p/>
     * When applied to the URL:
     * <pre>   {@code http://www.yahoo.com?theFirstParam=someValue}</pre>
     * will result in the proxy making a request to:
     * <pre>   {@code http://www.google.com?originalDomain=yahoo&theFirstParam=someValue}</pre>
     * When applied to the URL:
     * <pre>   {@code http://www.bing.com?anotherParam=anotherValue}</pre>
     * will result in the proxy making a request to:
     * <pre>   {@code http://www.google.com?originalDomain=bing&anotherParam=anotherValue}</pre>
     *
     * @param urlPattern URL-matching regular expression
     * @param replacementExpression an expression, which may optionally contain capture groups, which will replace any URL which matches urlPattern
     */
    void rewriteUrl(String urlPattern, String replacementExpression);

    /**
     * Replaces existing rewrite rules with the specified patterns and replacement expressions. The rules will be applied in the order
     * specified by the Map's iterator.
     * <p/>
     * See {@link #rewriteUrl(String, String)} for details on the format of the rewrite rules.
     *
     * @param rewriteRules {@code Map<urlPattern, replacementExpression>}
     */
    void rewriteUrls(Map<String, String> rewriteRules);

    /**
     * Returns all rewrite rules currently in effect. Iterating over the returned Map is guaranteed to return rewrite rules
     * in the order in which the rules are actually applied.
     *
     * @return {@code Map<URL-matching regex, replacement expression>}
     */
    Map<String, String> getRewriteRules();

    /**
     * Removes an existing rewrite rule whose urlPattern matches the specified pattern.
     *
     * @param urlPattern rewrite rule pattern to remove
     */
    void removeRewriteRule(String urlPattern);

    /**
     * Clears all existing rewrite rules.
     */
    void clearRewriteRules();

    /**
     * Adds a URL-matching regular expression to the blacklist. Requests that match a blacklisted URL will return the specified HTTP
     * statusCode for all HTTP methods. If there are existing patterns on the blacklist, the urlPattern will be evaluated last,
     * after the URL is checked against all other blacklist entries.
     * <p/>
     * The urlPattern matches the full URL of the request, including scheme, host, and port, path, and query parameters
     * for both HTTP and HTTPS requests. For example, to blacklist both HTTP and HTTPS requests to www.google.com,
     * use a urlPattern of "https?://www\\.google\\.com/.*".
     *
     * @param urlPattern URL-matching regular expression to blacklist
     * @param statusCode HTTP status code to return
     */
    void blacklistRequests(String urlPattern, int statusCode);

    /**
     * Adds a URL-matching regular expression to the blacklist. Requests that match a blacklisted URL will return the specified HTTP
     * statusCode only when the request's HTTP method (GET, POST, PUT, etc.) matches the specified httpMethodPattern regular expression.
     * If there are existing patterns on the blacklist, the urlPattern will be evaluated last, after the URL is checked against all
     * other blacklist entries.
     * <p/>
     * See {@link #blacklistRequests(String, int)} for details on the URL the urlPattern will match.
     *
     * @param urlPattern URL-matching regular expression to blacklist
     * @param statusCode HTTP status code to return
     * @param httpMethodPattern regular expression matching a request's HTTP method
     */
    void blacklistRequests(String urlPattern, int statusCode, String httpMethodPattern);

    /**
     * Replaces any existing blacklist with the specified blacklist. URLs will be evaluated against the blacklist in the order
     * specified by the Collection's iterator.
     *
     * @param blacklist new blacklist entries
     */
    void setBlacklist(Collection<BlacklistEntry> blacklist);

    /**
     * Returns all blacklist entries currently in effect. Iterating over the returned Collection is guaranteed to return
     * blacklist entries in the order in which URLs are actually evaluated against the blacklist.
     *
     * @return blacklist entries, or an empty collection if none exist
     */
    Collection<BlacklistEntry> getBlacklist();

    /**
     * Clears any existing blacklist.
     */
    void clearBlacklist();

    /**
     * Whitelists URLs matching the specified regular expression patterns. Replaces any existing whitelist.
     * The urlPattern matches the full URL of the request, including scheme, host, and port, path, and query parameters
     * for both HTTP and HTTPS requests. For example, to whitelist both HTTP and HTTPS requests to www.google.com, use a urlPattern
     * of "https?://www\\.google\\.com/.*".
     * <p/>
     * <b>Note:</b> All HTTP CONNECT requests are automatically whitelisted and cannot be short-circuited using the
     * whitelist response code.
     *
     * @param urlPatterns URL-matching regular expressions to whitelist; null or an empty collection will enable an empty whitelist
     * @param statusCode HTTP status code to return to clients when a URL matches a pattern
     */
    void whitelistRequests(Collection<String> urlPatterns, int statusCode);

    /**
     * Adds a URL-matching regular expression to an existing whitelist.
     *
     * @param urlPattern URL-matching regular expressions to whitelist
     * @throws java.lang.IllegalStateException if the whitelist is not enabled
     */
    void addWhitelistPattern(String urlPattern);

    /**
     * Enables the whitelist, but with no matching URLs. All requests will generated the specified HTTP statusCode.
     *
     * @param statusCode HTTP status code to return to clients on all requests
     */
    void enableEmptyWhitelist(int statusCode);

    /**
     * Clears any existing whitelist and disables whitelisting.
     */
    void disableWhitelist();

    /**
     * Returns the URL-matching regular expressions currently in effect. If the whitelist is disabled, this method always returns an empty collection.
     * If the whitelist is enabled but empty, this method return an empty collection.
     *
     * @return whitelist currently in effect, or an empty collection if the whitelist is disabled or empty
     */
    Collection<String> getWhitelistUrls();

    /**
     * Returns the status code returned for all URLs that do not match the whitelist. If the whitelist is not currently enabled, returns -1.
     *
     * @return HTTP status code returned for non-whitelisted URLs, or -1 if the whitelist is disabled.
     */
    int getWhitelistStatusCode();

    /**
     * Returns true if the whitelist is enabled, otherwise false.
     */
    boolean isWhitelistEnabled();

    /**
     * Adds the specified HTTP headers to every request. Replaces any existing additional headers with the specified headers.
     *
     * @param headers {@code Map<header name, header value>} to append to every request.
     */
    void addHeaders(Map<String, String> headers);

    /**
     * Adds a new HTTP header to every request. If the header already exists on the request, it will be replaced with the specified header.
     *
     * @param name name of the header to add
     * @param value new header's value
     */
    void addHeader(String name, String value);

    /**
     * Removes a header previously added with {@link #addHeader(String name, String value)}.
     *
     * @param name previously-added header's name
     */
    void removeHeader(String name);

    /**
     * Removes all headers previously added with {@link #addHeader(String name, String value)}.
     */
    void removeAllHeaders();

    /**
     * Returns all headers previously added with {@link #addHeader(String name, String value)}.
     *
     * @return {@code Map<header name, header value>}
     */
    Map<String, String> getAllHeaders();

    /**
     * Sets the resolver that will be used to look up host names. To chain multiple resolvers, wrap a list
     * of resolvers in a {@link net.lightbody.bmp.proxy.dns.ChainedHostResolver}.
     *
     * @param resolver host name resolver
     */
    void setHostNameResolver(AdvancedHostResolver resolver);

    /**
     * Returns the current host name resolver.
     *
     * @return current host name resolver
     */
    AdvancedHostResolver getHostNameResolver();

    /**
     * Waits for existing network traffic to stop, and for the specified quietPeriod to elapse. Returns true if there is no network traffic
     * for the quiet period within the specified timeout, otherwise returns false.
     *
     * @param quietPeriod amount of time after which network traffic will be considered "stopped"
     * @param timeout maximum amount of time to wait for network traffic to stop
     * @param timeUnit TimeUnit for the quietPeriod and timeout
     * @return true if network traffic is stopped, otherwise false
     */
    boolean waitForQuiescence(long quietPeriod, long timeout, TimeUnit timeUnit);

    /**
     * Instructs this proxy to route traffic through an upstream proxy.
     *
     * <b>Note:</b> A chained proxy must be set before the proxy is started, though it can be changed after the proxy is started.
     *
     * @param chainedProxyAddress address of the upstream proxy
     */
    void setChainedProxy(InetSocketAddress chainedProxyAddress);

    /**
     * Returns the address and port of the upstream proxy.
     *
     * @return address and port of the upstream proxy, or null of there is none.
     */
    InetSocketAddress getChainedProxy();

    /**
     * Adds a new filter factory (request/response interceptor) to the beginning of the HttpFilters chain.
     * <p/>
     * <b>Usage note:</b> The actual filter (interceptor) instance is created on every request by implementing the
     * {@link HttpFiltersSource#filterRequest(io.netty.handler.codec.http.HttpRequest, io.netty.channel.ChannelHandlerContext)} method and returning an
     * {@link org.littleshoot.proxy.HttpFilters} instance (typically, a subclass of {@link org.littleshoot.proxy.HttpFiltersAdapter}).
     * To disable or bypass a filter on a per-request basis, the filterRequest() method may return null.
     *
     * @param filterFactory factory to generate HttpFilters
     */
    void addFirstHttpFilterFactory(HttpFiltersSource filterFactory);

    /**
     * Adds a new filter factory (request/response interceptor) to the end of the HttpFilters chain.
     * <p/>
     * <b>Usage note:</b> The actual filter (interceptor) instance is created on every request by implementing the
     * {@link HttpFiltersSource#filterRequest(io.netty.handler.codec.http.HttpRequest, io.netty.channel.ChannelHandlerContext)} method and returning an
     * {@link org.littleshoot.proxy.HttpFilters} instance (typically, a subclass of {@link org.littleshoot.proxy.HttpFiltersAdapter}).
     * To disable or bypass a filter on a per-request basis, the filterRequest() method may return null.
     *
     *  @param filterFactory factory to generate HttpFilters
     */
    void addLastHttpFilterFactory(HttpFiltersSource filterFactory);

    /**
     * Adds a new ResponseFilter that can be used to examine and manipulate the response before sending it to the client.
     *
     * @param filter filter instance
     */
    void addResponseFilter(ResponseFilter filter);

    /**
     * Adds a new RequestFilter that can be used to examine and manipulate the request before sending it to the server.
     *
     * @param filter filter instance
     */
    void addRequestFilter(RequestFilter filter);

    /**
     * Completely disables MITM for this proxy server. The proxy will no longer intercept HTTPS requests, but they will
     * still be pass-through proxied. This option must be set before the proxy is started; otherwise an IllegalStateException will be thrown.
     *
     * @param mitmDisabled when true, MITM capture will be disabled
     * @throws java.lang.IllegalStateException if the proxy is already started
     */
    void setMitmDisabled(boolean mitmDisabled);

    /**
     * Sets the MITM manager, which is responsible for generating forged SSL certificates to present to clients. By default,
     * BrowserMob Proxy uses the ca-certificate-rsa.cer root certificate for impersonation. See the documentation at
     * {@link net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager} and {@link net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager.Builder}
     * for details on customizing the root and server certificate generation.
     *
     * @param mitmManager MITM manager to use
     */
    void setMitmManager(MitmManager mitmManager);

    /**
     * Disables verification of all upstream servers' SSL certificates. All upstream servers will be trusted, even if they
     * do not present valid certificates signed by certification authorities in the JDK's trust store. <b>This option
     * exposes the proxy to MITM attacks and should only be used when testing in trusted environments.</b>
     *
     * @param trustAllServers when true, disables upstream server certificate verification
     */
    void setTrustAllServers(boolean trustAllServers);

    /**
     * Sets the {@link TrustSource} that contains trusted root certificate authorities that will be used to validate
     * upstream servers' certificates. When null, disables certificate validation (see warning at {@link #setTrustAllServers(boolean)}).
     *
     * @param trustSource TrustSource containing root CAs, or null to disable upstream server validation
     */
    void setTrustSource(TrustSource trustSource);
}
