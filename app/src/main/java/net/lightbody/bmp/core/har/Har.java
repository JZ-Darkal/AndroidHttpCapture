package net.lightbody.bmp.core.har;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class Har {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private volatile HarLog log;

    public Har() {
    }

    public Har(HarLog log) {
        this.log = log;
    }

    public HarLog getLog() {
        return log;
    }

    public void setLog(HarLog log) {
        this.log = log;
    }

    public void writeTo(Writer writer) throws IOException {
        OBJECT_MAPPER.writeValue(writer, this);
    }

    public void writeTo(OutputStream os) throws IOException {
        OBJECT_MAPPER.writeValue(os, this);
    }

    public void writeTo(File file) throws IOException {
        OBJECT_MAPPER.writeValue(file, this);
    }
}
