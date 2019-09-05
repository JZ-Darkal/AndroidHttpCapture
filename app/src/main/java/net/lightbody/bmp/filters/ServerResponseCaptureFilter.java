package net.lightbody.bmp.filters;

import net.lightbody.bmp.util.BrowserMobHttpUtil;

import org.littleshoot.proxy.HttpFiltersAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;

/**
 * This filter captures responses from the server (headers and content). The filter can also decompress contents if desired.
 * <p/>
 * The filter can be used in one of three ways: (1) directly, by adding the filter to the filter chain; (2) by subclassing
 * the filter and overriding its filter methods; or (3) by invoking the filter directly from within another filter (see
 * {@link net.lightbody.bmp.filters.HarCaptureFilter} for an example of the latter).
 */
public class ServerResponseCaptureFilter extends HttpFiltersAdapter {
    private static final Logger log = LoggerFactory.getLogger(ServerResponseCaptureFilter.class);
    /**
     * Populated by serverToProxyResponse() as it receives HttpContent responses. If the response is chunked, it will
     * be populated across multiple calls to proxyToServerResponse().
     */
    private final ByteArrayOutputStream rawResponseContents = new ByteArrayOutputStream();
    /**
     * User option indicating compressed content should be uncompressed.
     */
    private final boolean decompressEncodedContent;
    /**
     * Populated by serverToProxyResponse() when processing the HttpResponse object
     */
    private volatile HttpResponse httpResponse;
    /**
     * Populated when processing the LastHttpContent. If the response is compressed and decompression is requested,
     * this contains the entire decompressed response. Otherwise it contains the raw response.
     */
    private volatile byte[] fullResponseContents;
    /**
     * Populated by serverToProxyResponse() when it processes the LastHttpContent object.
     */
    private volatile HttpHeaders trailingHeaders;
    /**
     * Set to true when processing the LastHttpContent if the server indicates there is a content encoding.
     */
    private volatile boolean responseCompressed;
    /**
     * Set to true when processing the LastHttpContent if decompression was requested and successful.
     */
    private volatile boolean decompressionSuccessful;
    /**
     * Populated when processing the LastHttpContent.
     */
    private volatile String contentEncoding;

    public ServerResponseCaptureFilter(HttpRequest originalRequest, boolean decompressEncodedContent) {
        super(originalRequest);

        this.decompressEncodedContent = decompressEncodedContent;
    }

    public ServerResponseCaptureFilter(HttpRequest originalRequest, ChannelHandlerContext ctx, boolean decompressEncodedContent) {
        super(originalRequest, ctx);

        this.decompressEncodedContent = decompressEncodedContent;
    }

    @Override
    public HttpObject serverToProxyResponse(HttpObject httpObject) {
        if (httpObject instanceof HttpResponse) {
            httpResponse = (HttpResponse) httpObject;
            captureContentEncoding(httpResponse);
        }

        if (httpObject instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) httpObject;

            storeResponseContent(httpContent);

            if (httpContent instanceof LastHttpContent) {
                LastHttpContent lastContent = (LastHttpContent) httpContent;
                captureTrailingHeaders(lastContent);

                captureFullResponseContents();
            }
        }

        return super.serverToProxyResponse(httpObject);
    }

    protected void captureFullResponseContents() {
        // start by setting fullResponseContent to the raw, (possibly) compressed byte stream. replace it
        // with the decompressed bytes if decompression is successful.
        fullResponseContents = getRawResponseContents();

        // if the content is compressed, we need to decompress it. but don't use
        // the netty HttpContentCompressor/Decompressor in the pipeline because we don't actually want it to
        // change the message sent to the client
        if (contentEncoding != null) {
            responseCompressed = true;

            if (decompressEncodedContent) {
                decompressContents();
            } else {
                // will not decompress response
            }
        } else {
            // no compression
            responseCompressed = false;
        }
    }

    protected void decompressContents() {
        if (contentEncoding.equalsIgnoreCase(HttpHeaders.Values.GZIP) || contentEncoding.equalsIgnoreCase(HttpHeaders.Values.DEFLATE)) {
            try {
                fullResponseContents = BrowserMobHttpUtil.decompressContents(getRawResponseContents(), contentEncoding);
                decompressionSuccessful = true;
            } catch (RuntimeException e) {
                log.warn("Failed to decompress response with encoding type " + contentEncoding + " when decoding request from " + originalRequest.getUri(), e);
            }
        } else {
            log.warn("Cannot decode unsupported content encoding type {}", contentEncoding);
        }
    }

    protected void captureContentEncoding(HttpResponse httpResponse) {
        contentEncoding = HttpHeaders.getHeader(httpResponse, HttpHeaders.Names.CONTENT_ENCODING);
    }

    protected void captureTrailingHeaders(LastHttpContent lastContent) {
        trailingHeaders = lastContent.trailingHeaders();

        // technically, the Content-Encoding header can be in a trailing header, although this is excruciatingly uncommon
        if (trailingHeaders != null) {
            String trailingContentEncoding = trailingHeaders.get(HttpHeaders.Names.CONTENT_ENCODING);
            if (trailingContentEncoding != null) {
                contentEncoding = trailingContentEncoding;
            }
        }

    }

    protected void storeResponseContent(HttpContent httpContent) {
        ByteBuf bufferedContent = httpContent.content();
        byte[] content = BrowserMobHttpUtil.extractReadableBytes(bufferedContent);

        try {
            rawResponseContents.write(content);
        } catch (IOException e) {
            // can't happen
        }
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    /**
     * Returns the contents of the entire response. If the contents were compressed, <code>decompressEncodedContent</code> is true, and
     * decompression was successful, this method returns the decompressed contents.
     *
     * @return entire response contents, decompressed if possible
     */
    public byte[] getFullResponseContents() {
        return fullResponseContents;
    }

    /**
     * Returns the raw contents of the entire response, without decompression.
     *
     * @return entire response contents, without decompression
     */
    public byte[] getRawResponseContents() {
        return rawResponseContents.toByteArray();
    }

    public HttpHeaders getTrailingHeaders() {
        return trailingHeaders;
    }

    public boolean isResponseCompressed() {
        return responseCompressed;
    }

    /**
     * @return true if decompression is both enabled and successful
     */
    public boolean isDecompressionSuccessful() {
        if (!decompressEncodedContent) {
            return false;
        }

        return decompressionSuccessful;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

}
