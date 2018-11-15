package net.lightbody.bmp.core.har;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.net.URLDecoder;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HarCookie {
    private volatile String name;
    private volatile String value;
    private volatile String path;
    private volatile String domain;
    private volatile Date expires;
    private volatile Boolean httpOnly;
    private volatile Boolean secure;
    private volatile String comment = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getDecodeValue(){
        try {
            return URLDecoder.decode(value);
        }catch (Exception e){
            return value;
        }
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.ssZ")
    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public Boolean getHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(Boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public Boolean getSecure() {
        return secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "HarCookie{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", path='" + path + '\'' +
                ", domain='" + domain + '\'' +
                ", expires=" + expires +
                ", httpOnly=" + httpOnly +
                ", secure=" + secure +
                ", comment='" + comment + '\'' +
                '}';
    }
}
