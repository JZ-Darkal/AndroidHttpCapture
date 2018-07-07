package net.lightbody.bmp.filters.util;

import net.lightbody.bmp.core.har.HarResponse;

/**
 * Static utility methods for {@link net.lightbody.bmp.filters.HarCaptureFilter} and {@link net.lightbody.bmp.filters.HttpConnectHarCaptureFilter}.
 */
public class HarCaptureUtil {
    /**
     * The HTTP version string in the {@link HarResponse} for failed requests.
     */
    public static final String HTTP_VERSION_STRING_FOR_FAILURE = "unknown";

    /**
     * The HTTP status code in the {@link HarResponse} for failed requests.
     */
    public static final int HTTP_STATUS_CODE_FOR_FAILURE = 0;

    /**
     * The HTTP status text/reason phrase in the {@link HarResponse} for failed requests.
     */
    public static final String HTTP_REASON_PHRASE_FOR_FAILURE = "";

    /**
     * The error message that will be populated in the _error field of the {@link HarResponse} due to a name
     * lookup failure.
     */
    private static final String RESOLUTION_FAILED_ERROR_MESSAGE = "Unable to resolve host: ";

    /**
     * The error message that will be populated in the _error field of the {@link HarResponse} due to a
     * connection failure.
     */
    private static final String CONNECTION_FAILED_ERROR_MESSAGE = "Unable to connect to host";

    /**
     * The error message that will be populated in the _error field of the {@link HarResponse} when the proxy fails to
     * receive a response in a timely manner.
     */
    private static final String RESPONSE_TIMED_OUT_ERROR_MESSAGE = "Response timed out";

    /**
     * The error message that will be populated in the _error field of the {@link HarResponse} when no response is received
     * from the server for any reason other than a server response timeout.
     */
    private static final String NO_RESPONSE_RECEIVED_ERROR_MESSAGE = "No response received";

    /**
     * Creates a HarResponse object for failed requests. Normally the HarResponse is populated when the response is received
     * from the server, but if the request fails due to a name resolution issue, connection problem, timeout, etc., no
     * HarResponse would otherwise be created.
     *
     * @return a new HarResponse object with invalid HTTP status code (0) and version string ("unknown")
     */
    public static HarResponse createHarResponseForFailure() {
        return new HarResponse(HTTP_STATUS_CODE_FOR_FAILURE, HTTP_REASON_PHRASE_FOR_FAILURE, HTTP_VERSION_STRING_FOR_FAILURE);
    }

    /**
     * Returns the error message for the HAR response when DNS resolution fails.
     *
     * @param hostAndPort the host and port of the address lookup that failed
     * @return the resolution failed error message
     */
    public static String getResolutionFailedErrorMessage(String hostAndPort) {
        return RESOLUTION_FAILED_ERROR_MESSAGE + hostAndPort;
    }

    /**
     * Returns the error message for the HAR response when the connection fails.
     *
     * @return the connection failed error message
     */
    public static String getConnectionFailedErrorMessage() {
        return CONNECTION_FAILED_ERROR_MESSAGE;
    }

    /**
     * Returns the error message for the HAR response when the response from the server times out.
     *
     * @return the response timed out error message
     */
    public static String getResponseTimedOutErrorMessage() {
        return RESPONSE_TIMED_OUT_ERROR_MESSAGE;
    }

    /**
     * Returns the error message for the HAR response when no response was received from the server (e.g. when the
     * browser is closed).
     * 
     * @return the no response received error message
     */
    public static String getNoResponseReceivedErrorMessage() {
        return NO_RESPONSE_RECEIVED_ERROR_MESSAGE;
    }
}
