package net.lightbody.bmp.proxy;

import java.util.regex.Pattern;

/**
 * An entry in the Blacklist, consisting of a regular expression to match the URL, an HTTP status code, and a regular expression
 * to match the HTTP method.
 */
public class BlacklistEntry {
    private final Pattern urlPattern;
    private final int statusCode;
    private final Pattern httpMethodPattern;

    /**
     * Creates a new BlacklistEntry with no HTTP method matching (i.e. all methods will match).
     *
     * @param urlPattern URL pattern to blacklist
     * @param statusCode HTTP status code to return for blacklisted URL
     */
    public BlacklistEntry(String urlPattern, int statusCode) {
        this(urlPattern, statusCode, null);
    }

    /**
     * Creates a new BlacklistEntry which will match both a URL and an HTTP method
     *
     * @param urlPattern        URL pattern to blacklist
     * @param statusCode        status code to return for blacklisted URL
     * @param httpMethodPattern HTTP method to match (e.g. GET, PUT, PATCH, etc.)
     */
    public BlacklistEntry(String urlPattern, int statusCode, String httpMethodPattern) {
        this.urlPattern = Pattern.compile(urlPattern);
        this.statusCode = statusCode;
        if (httpMethodPattern == null || httpMethodPattern.isEmpty()) {
            this.httpMethodPattern = null;
        } else {
            this.httpMethodPattern = Pattern.compile(httpMethodPattern);
        }
    }

    /**
     * Determines if this BlacklistEntry matches the given URL. Attempts to match both the URL and the
     * HTTP method.
     *
     * @param url        possibly-blacklisted URL
     * @param httpMethod HTTP method this URL is being accessed with
     * @return true if the URL matches this BlacklistEntry
     */
    public boolean matches(String url, String httpMethod) {
        if (httpMethodPattern != null) {
            return urlPattern.matcher(url).matches() && httpMethodPattern.matcher(httpMethod).matches();
        } else {
            return urlPattern.matcher(url).matches();
        }
    }

    public Pattern getUrlPattern() {
        return urlPattern;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Pattern getHttpMethodPattern() {
        return httpMethodPattern;
    }

    @Deprecated
    /**
     * @deprecated use {@link #getUrlPattern()}
     */
    public Pattern getPattern() {
        return getUrlPattern();
    }

    @Deprecated
    /**
     * @deprecated use {@link #getStatusCode()}
     */
    public int getResponseCode() {
        return getStatusCode();
    }

    @Deprecated
    /**
     * @deprecated use {@link #getHttpMethodPattern()}
     */
    public Pattern getMethod() {
        return getHttpMethodPattern();
    }
}