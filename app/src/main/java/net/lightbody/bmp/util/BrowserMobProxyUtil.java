package net.lightbody.bmp.util;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarLog;
import net.lightbody.bmp.core.har.HarPage;
import net.lightbody.bmp.mitm.exception.UncheckedIOException;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

/**
 * General utility class for functionality and classes used mostly internally by BrowserMob Proxy.
 */
public class BrowserMobProxyUtil {
    private static final Logger log = LoggerFactory.getLogger(BrowserMobProxyUtil.class);

    /**
     * Classpath resource containing this build's version string.
     */
    private static final String VERSION_CLASSPATH_RESOURCE = "/net/lightbody/bmp/version";

    /**
     * Default value if the version string cannot be read.
     */
    private static final String UNKNOWN_VERSION_STRING = "UNKNOWN-VERSION";

    /**
     * Singleton User Agent parser.
     */
    private static volatile UserAgentStringParser parser;

    /**
     * Singleton version string loader.
     */
    private static final Supplier<String> version = Suppliers.memoize(new Supplier<String>() {
        @Override
        public String get() {
            return readVersionFileOnClasspath();
        }
    });

    private static final Object PARSER_INIT_LOCK = new Object();

    /**
     * Retrieve the User Agent String Parser. Create the parser if it has not yet been initialized.
     * 
     * @return singleton UserAgentStringParser object
     */
    public static UserAgentStringParser getUserAgentStringParser() {
        if (parser == null) {
            synchronized (PARSER_INIT_LOCK) {
                if (parser == null) {
                    // using resourceModuleParser for now because user-agent-string.info no longer exists. the updating
                    // parser will get incorrect data and wipe out its entire user agent repository.
                    parser = UADetectorServiceFactory.getResourceModuleParser();
                }
            }
        }

        return parser;
    }

    /**
     * Copies {@link HarEntry} and {@link HarPage} references from the specified har to a new har copy, up to and including
     * the specified pageRef. Does not perform a "deep copy", so any subsequent modification to the entries or pages will
     * be reflected in the copied har.
     *
     * @param har existing har to copy
     * @param pageRef last page ID to copy
     * @return copy of a {@link Har} with entries and pages from the original har, or null if the input har is null
     */
    public static Har copyHarThroughPageRef(Har har, String pageRef) {
        if (har == null) {
            return null;
        }

        if (har.getLog() == null) {
            return new Har();
        }

        // collect the page refs that need to be copied to new har copy.
        Set<String> pageRefsToCopy = new HashSet<String>();

        for (HarPage page : har.getLog().getPages()) {
            pageRefsToCopy.add(page.getId());

            if (pageRef.equals(page.getId())) {
                break;
            }
        }

        HarLog logCopy = new HarLog();

        // copy every entry and page in the HarLog that matches a pageRefToCopy. since getEntries() and getPages() return
        // lists, we are guaranteed that we will iterate through the pages and entries in the proper order
        for (HarEntry entry : har.getLog().getEntries()) {
            if (pageRefsToCopy.contains(entry.getPageref())) {
                logCopy.addEntry(entry);
            }
        }

        for (HarPage page : har.getLog().getPages()) {
            if (pageRefsToCopy.contains(page.getId())) {
                logCopy.addPage(page);
            }
        }

        Har harCopy = new Har();
        harCopy.setLog(logCopy);

        return harCopy;
    }

    /**
     * Returns the version of BrowserMob Proxy, e.g. "2.1.0".
     *
     * @return BMP version string
     */
    public static String getVersionString() {
        return version.get();
    }

    /**
     * Reads the version of this build from the classpath resource specified by {@link #VERSION_CLASSPATH_RESOURCE}.
     *
     * @return version string from the classpath version resource
     */
    private static String readVersionFileOnClasspath() {
        String versionString;
        try {
            versionString = ClasspathResourceUtil.classpathResourceToString(VERSION_CLASSPATH_RESOURCE, Charset.forName("UTF-8"));
        } catch (UncheckedIOException e) {
            log.debug("Unable to load version from classpath resource: {}", VERSION_CLASSPATH_RESOURCE, e);
            return UNKNOWN_VERSION_STRING;
        }

        if (versionString.isEmpty()) {
            log.debug("Version file on classpath was empty or could not be read. Resource: {}", VERSION_CLASSPATH_RESOURCE);
            return UNKNOWN_VERSION_STRING;
        }

        return versionString;
    }
}
