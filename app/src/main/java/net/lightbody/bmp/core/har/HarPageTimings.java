package net.lightbody.bmp.core.har;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HarPageTimings {
    private volatile Long onContentLoad;
    private volatile Long onLoad;
    private volatile String comment = "";

    public HarPageTimings() {
    }

    public HarPageTimings(Long onContentLoad, Long onLoad) {
        this.onContentLoad = onContentLoad;
        this.onLoad = onLoad;
    }

    public Long getOnContentLoad() {
        return onContentLoad;
    }

    public void setOnContentLoad(Long onContentLoad) {
        this.onContentLoad = onContentLoad;
    }

    public Long getOnLoad() {
        return onLoad;
    }

    public void setOnLoad(Long onLoad) {
        this.onLoad = onLoad;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
