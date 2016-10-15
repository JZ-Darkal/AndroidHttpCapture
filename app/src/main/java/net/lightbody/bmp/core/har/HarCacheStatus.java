package net.lightbody.bmp.core.har;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.lightbody.bmp.core.json.ISO8601DateFormatter;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HarCacheStatus {
    private volatile Date expires;
    private volatile Date lastAccess;
    private volatile String eTag;
    private volatile int hitCount;
    private volatile String comment = "";

    @JsonSerialize(using = ISO8601DateFormatter.class)
    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    @JsonSerialize(using = ISO8601DateFormatter.class)
    public Date getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
