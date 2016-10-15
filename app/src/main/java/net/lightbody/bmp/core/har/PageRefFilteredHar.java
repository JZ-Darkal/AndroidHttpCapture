package net.lightbody.bmp.core.har;

import java.util.Set;

/**
 * Created by Darkal on 2016/9/2.
 */

public class PageRefFilteredHar extends Har {
    public PageRefFilteredHar(Har har, Set<String> pageRef) {
        super(new PageRefFilteredHarLog(har.getLog(), pageRef));
    }

    public PageRefFilteredHar(Har har, String pageRef) {
        super(new PageRefFilteredHarLog(har.getLog(), pageRef));
    }
}