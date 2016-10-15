package net.lightbody.bmp.core.har;

public class HarNameVersion {
    private final String name;
    private final String version;
    private volatile String comment = "";

    public HarNameVersion(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
