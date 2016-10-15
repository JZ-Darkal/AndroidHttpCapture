package net.lightbody.bmp.core.har;

import com.fasterxml.jackson.annotation.JsonInclude;

import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.util.BrowserMobProxyUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Modify by xuzhou on 2016/9/2.
 * 添加了删除page的功能
 * 添加了clearAllEntries
 * 1000请求自动分页
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HarLog {
    private final String version = "1.2";
    private volatile HarNameVersion creator = new HarNameVersion("BrowserMob Proxy", BrowserMobProxyUtil.getVersionString());
    private volatile HarNameVersion browser;
    private List<HarPage> pages = new CopyOnWriteArrayList<HarPage>();
    private List<HarEntry> entries = new CopyOnWriteArrayList<HarEntry>();
    private volatile String comment = "";
    private BrowserMobProxyServer server;

    public HarLog() {
    }

    public HarLog(HarNameVersion creator,BrowserMobProxyServer server) {
        this.creator = creator;
        this.server = server;
    }

    public void addPage(HarPage page) {
        pages.add(page);
    }

    public Boolean deletePage(HarPage page){
        return pages.remove(page);
    }

    public synchronized void addEntry(HarEntry entry) {
        int count = 0;
        for (HarEntry har:entries) {
            if (entry.getPageref().equals(har.getPageref())) {
                count++;
            }
        }
        if(count >= 999) {
            if(server!=null){
                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                        .format(new Date(System.currentTimeMillis()));

                // 检查是否存在重复添加
                Boolean repeatAdd = false;
                for (HarPage page:pages) {
                    if(page.getId().equals(time)){
                        repeatAdd = true;
                    }
                }
                if(!repeatAdd) {
                    server.newPage(time);
                }
            }
        }
        entries.add(entry);
    }

    public void clearAllEntries(){
        entries.clear();
    }

    public String getVersion() {
        return version;
    }

    public HarNameVersion getCreator() {
        return creator;
    }

    public void setCreator(HarNameVersion creator) {
        this.creator = creator;
    }

    public HarNameVersion getBrowser() {
        return browser;
    }

    public void setBrowser(HarNameVersion browser) {
        this.browser = browser;
    }

    public List<HarPage> getPages() {
        return pages;
    }

    public List<HarEntry> getEntries() {
        return entries;
    }

    public void setPages(List<HarPage> pages) {
        this.pages = pages;
    }

    public void setEntries(List<HarEntry> entries) {
        this.entries = entries;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
