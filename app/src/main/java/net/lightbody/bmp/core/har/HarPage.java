package net.lightbody.bmp.core.har;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.lightbody.bmp.core.json.ISO8601WithTDZDateFormatter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HarPage {
    private volatile String id;
    private volatile Date startedDateTime;
    private volatile String title = "";
    private final HarPageTimings pageTimings = new HarPageTimings();
    private volatile String comment = "";

    public HarPage() {
    }

    public HarPage(String id) {
        this(id, "");
    }

    public HarPage(String id, String title) {
        this.id = id;
        this.title = title;
        startedDateTime = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonSerialize(using = ISO8601WithTDZDateFormatter.class)
    public Date getStartedDateTime() {
        return startedDateTime;
    }

    public void setStartedDateTime(Date startedDateTime) {
        this.startedDateTime = startedDateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HarPageTimings getPageTimings() {
        return pageTimings;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
