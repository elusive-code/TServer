package com.elusive_code.tserver.jackson;

import com.elusive_code.tserver.base.Context;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author Vladislav Dolgikh
 */
public class ContextSerializer extends JsonSerializer<Context> {

    @Override
    public void serialize(Context ctx, JsonGenerator jgen, SerializerProvider provider) throws
                                                                                         IOException,
                                                                                         JsonProcessingException {
        jgen.writeStartObject();
        if (jgen.canWriteTypeId()){
            jgen.writeTypeId(ctx.getClass().getName());
        }
        jgen.writeObjectField("children",ctx.getChildren());
        jgen.writeObjectField("params",ctx.getParams());
        jgen.writeObjectField("finals",ctx.getFinals());
        jgen.writeEndObject();
    }
}
