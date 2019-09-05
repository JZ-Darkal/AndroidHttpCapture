package net.lightbody.bmp.core.har;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HarPage {
    private final HarPageTimings pageTimings = new HarPageTimings();
    private volatile String id;
    private volatile Date startedDateTime;
    private volatile String title = "";
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.ssZ")
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
