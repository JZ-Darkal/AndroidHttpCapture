package net.lightbody.bmp.proxy;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A URL whitelist. This object is immutable and the list of matching patterns and the HTTP status code is unmodifiable
 * after creation. Enabling, disabling, or modifying the whitelist can be safely and easily accomplished by updating the
 * whitelist reference to a new whitelist.
 */
public class Whitelist {
    private final List<Pattern> patterns;
    private final int statusCode;
    private final boolean enabled;

    /**
     * A disabled Whitelist.
     */
    public static final Whitelist WHITELIST_DISABLED = new Whitelist();

    /**
     * Creates an empty, disabled Whitelist.
     */
    public Whitelist() {
        this.patterns = Collections.emptyList();
        this.statusCode = -1;
        this.enabled = false;
    }

    /**
     * Creates an empty, enabled whitelist with the specified response code.
     *
     * @param statusCode the response code that the (enabled) Whitelist will return for all URLs.
     */
    public Whitelist(int statusCode) {
        this.patterns = Collections.emptyList();
        this.statusCode = statusCode;
        this.enabled = true;
    }

    /**
     * @deprecated use {@link #Whitelist(java.util.Collection, int)}
     */
    @Deprecated
    public Whitelist(String[] patterns, int statusCode) {
        this(patterns == null ? null : Arrays.asList(patterns), statusCode);
    }

    /**
     * Creates a whitelist for the specified patterns, returning the given statusCode when a URL does not match one of the patterns.
     * A null or empty collection will result in an empty whitelist.
     *
     * @param patterns URL-matching regular expression patterns to whitelist
     * @param statusCode the HTTP status code to return when a request URL matches a whitelist pattern
     */
    public Whitelist(Collection<String> patterns, int statusCode) {
        if (patterns == null || patterns.isEmpty()) {
            this.patterns = Collections.emptyList();
        } else {
            ImmutableList.Builder<Pattern> builder = ImmutableList.builder();
            for (String pattern : patterns) {
                builder.add(Pattern.compile(pattern));
            }

            this.patterns = builder.build();
        }

        this.statusCode = statusCode;

        this.enabled = true;
    }

    /**
     * @return true if this whitelist is enabled, otherwise false
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @return regular expression patterns describing the URLs that should be whitelisted, or an empty collection if the whitelist is disabled
     */
    public Collection<Pattern> getPatterns() {
        return this.patterns;
    }

    /**
     * @return HTTP status code returned by the whitelist, or -1 if the whitelist is disabled
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @deprecated use {@link #getStatusCode()}
     */
    @Deprecated
    public int getResponseCode() {
        return getStatusCode();
    }

    /**
     * Returns true if the specified URL matches a whitelisted URL regular expression. If the whitelist is disabled, this
     * method always returns false.
     *
     * @param url URL to match against the whitelist
     * @return true if the whitelist is enabled and the URL matched an entry in the whitelist, otherwise false
     */
    public boolean matches(String url) {
        if (!enabled) {
            return false;
        }

        for (Pattern pattern : getPatterns()) {
            Matcher matcher = pattern.matcher(url);
            if (matcher.matches()) {
                return true;
            }
        }

        return false;
    }
}
