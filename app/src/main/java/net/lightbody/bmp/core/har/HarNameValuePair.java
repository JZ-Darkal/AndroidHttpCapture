package net.lightbody.bmp.core.har;

import java.net.URLDecoder;

public final class HarNameValuePair {
    private final String name;
    private final String value;

    public HarNameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
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

    public String toString() {
        return name + "=" + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HarNameValuePair that = (HarNameValuePair) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
