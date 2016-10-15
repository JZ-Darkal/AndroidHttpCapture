package net.lightbody.bmp.proxy;

import java.util.EnumSet;

/**
 * Data types that the proxy can capture. Data types are organized into two broad categories, <code>REQUEST_*</code> and
 * <code>RESPONSE_*</code>, corresponding to client requests and server responses.
 */
public enum CaptureType {
    /**
     * HTTP request headers, including trailing headers.
     */
    REQUEST_HEADERS,

    /**
     * HTTP Cookies sent with the request.
     */
    REQUEST_COOKIES,

    /**
     * Non-binary HTTP request content, such as post data or other text-based request payload.
     * See {@link net.lightbody.bmp.util.BrowserMobHttpUtil#hasTextualContent(String)} for a list of Content-Types that
     * are considered non-binary.
     *
     */
    REQUEST_CONTENT,

    /**
     * Binary HTTP request content, such as file uploads, or any unrecognized request payload.
     */
    REQUEST_BINARY_CONTENT,

    /**
     * HTTP response headers, including trailing headers.
     */
    RESPONSE_HEADERS,

    /**
     * Set-Cookie headers sent with the response.
     */
    RESPONSE_COOKIES,

    /**
     * Non-binary HTTP response content (typically, HTTP body content).
     * See {@link net.lightbody.bmp.util.BrowserMobHttpUtil#hasTextualContent(String)} for a list of Content-Types that
     * are considered non-binary.
     */
    RESPONSE_CONTENT,

    /**
     * Binary HTTP response content, such as image files, or any unrecognized response payload.
     */
    RESPONSE_BINARY_CONTENT;

    // the following groups of capture types are private so that clients do not accidentally modify these sets (EnumSets are not immutable)
    private static final EnumSet<CaptureType> REQUEST_CAPTURE_TYPES = EnumSet.of(REQUEST_HEADERS, REQUEST_CONTENT, REQUEST_BINARY_CONTENT, REQUEST_COOKIES);
    private static final EnumSet<CaptureType> RESPONSE_CAPTURE_TYPES = EnumSet.of(RESPONSE_HEADERS, RESPONSE_CONTENT, RESPONSE_BINARY_CONTENT, RESPONSE_COOKIES);
    private static final EnumSet<CaptureType> HEADER_CAPTURE_TYPES = EnumSet.of(REQUEST_HEADERS, RESPONSE_HEADERS);
    private static final EnumSet<CaptureType> NON_BINARY_CONTENT_CAPTURE_TYPES = EnumSet.of(REQUEST_CONTENT, RESPONSE_CONTENT);
    private static final EnumSet<CaptureType> BINARY_CONTENT_CAPTURE_TYPES = EnumSet.of(REQUEST_BINARY_CONTENT, RESPONSE_BINARY_CONTENT);
    private static final EnumSet<CaptureType> ALL_CONTENT_CAPTURE_TYPES = EnumSet.of(REQUEST_CONTENT, RESPONSE_CONTENT, REQUEST_BINARY_CONTENT, RESPONSE_BINARY_CONTENT);
    private static final EnumSet<CaptureType> COOKIE_CAPTURE_TYPES = EnumSet.of(REQUEST_COOKIES, RESPONSE_COOKIES);

    /**
     * @return Set of CaptureTypes for requests.
     */
    public static EnumSet<CaptureType> getRequestCaptureTypes() {
        return EnumSet.copyOf(REQUEST_CAPTURE_TYPES);
    }

    /**
     * @return Set of CaptureTypes for responses.
     */
    public static EnumSet<CaptureType> getResponseCaptureTypes() {
        return EnumSet.copyOf(RESPONSE_CAPTURE_TYPES);
    }

    /**
     * @return Set of CaptureTypes for headers.
     */
    public static EnumSet<CaptureType> getHeaderCaptureTypes() {
        return EnumSet.copyOf(HEADER_CAPTURE_TYPES);
    }

    /**
     * @return Set of CaptureTypes for non-binary content.
     */
    public static EnumSet<CaptureType> getNonBinaryContentCaptureTypes() {
        return EnumSet.copyOf(NON_BINARY_CONTENT_CAPTURE_TYPES);
    }

    /**
     * @return Set of CaptureTypes for binary content.
     */
    public static EnumSet<CaptureType> getBinaryContentCaptureTypes() {
        return EnumSet.copyOf(BINARY_CONTENT_CAPTURE_TYPES);
    }

    /**
     * @return Set of CaptureTypes for both binary and non-binary content.
     */
    public static EnumSet<CaptureType> getAllContentCaptureTypes() {
        return EnumSet.copyOf(ALL_CONTENT_CAPTURE_TYPES);
    }

    /**
     * @return Set of CaptureTypes for cookies.
     */
    public static EnumSet<CaptureType> getCookieCaptureTypes() {
        return EnumSet.copyOf(COOKIE_CAPTURE_TYPES);
    }

}
