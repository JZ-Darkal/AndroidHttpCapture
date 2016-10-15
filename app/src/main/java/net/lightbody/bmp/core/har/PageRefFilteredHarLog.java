package net.lightbody.bmp.core.har;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by xuzhou on 2016/9/2.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PageRefFilteredHarLog extends HarLog {
    public PageRefFilteredHarLog(HarLog log, Set<String> pageRef) {
        super();
        setBrowser(log.getBrowser());
        setPages(getFilteredPages(log.getPages(), pageRef));
        setEntries(getFilteredEntries(log.getEntries(), pageRef));
        setComment(log.getComment());
    }

    public PageRefFilteredHarLog(HarLog log, String pageRef) {
        super();
        setBrowser(log.getBrowser());
        setPages(getFilteredPages(log.getPages(), pageRef));
        setEntries(getFilteredEntries(log.getEntries(), pageRef));
        setComment(log.getComment());
    }

    private static List<HarPage> getFilteredPages(List<HarPage> pages, Set<String> pageRef) {
        List<HarPage> filteredPages = new CopyOnWriteArrayList<HarPage>();
        for (HarPage page : pages) {
            if (pageRef.contains(page.getId())) {
                filteredPages.add(page);
            }
        }
        return filteredPages;
    }

    private static List<HarEntry> getFilteredEntries(List<HarEntry> entries, Set<String> pageRef) {
        List<HarEntry> filteredEntries = new CopyOnWriteArrayList<HarEntry>();
        for (HarEntry entry : entries) {
            if (pageRef.contains(entry.getPageref())) {
                filteredEntries.add(entry);
            }
        }
        return filteredEntries;
    }

    private static List<HarPage> getFilteredPages(List<HarPage> pages, String pageRef) {
        List<HarPage> filteredPages = new CopyOnWriteArrayList<HarPage>();
        for (HarPage page : pages) {
            if (pageRef.contains(page.getId())) {
                filteredPages.add(page);
            }
        }
        return filteredPages;
    }

    private static List<HarEntry> getFilteredEntries(List<HarEntry> entries, String pageRef) {
        List<HarEntry> filteredEntries = new CopyOnWriteArrayList<HarEntry>();
        for (HarEntry entry : entries) {
            if (pageRef.contains(entry.getPageref())) {
                filteredEntries.add(entry);
            }
        }
        return filteredEntries;
    }
}
