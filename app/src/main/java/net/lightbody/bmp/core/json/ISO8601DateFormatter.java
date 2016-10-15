package net.lightbody.bmp.core.json;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Date;

public class ISO8601DateFormatter extends JsonSerializer<Date> {
    public final static ISO8601DateFormatter instance = new ISO8601DateFormatter();

    @Override
    public void serialize(java.util.Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        DateFormat df = (DateFormat) provider.getConfig().getDateFormat().clone();
        jgen.writeString(df.format(value));
    }

}
