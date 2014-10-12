package com.elusive_code.tserver.jackson;

import com.elusive_code.tserver.base.Context;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.SimpleType;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author Vladislav Dolgikh
 */
public class ContextDeserializer extends JsonDeserializer<Context> {

    private static final String CHILDREN_FIELD = "children";
    private static final String PARAMS_FIELD = "params";
    private static final String FINALS_FIELD = "finals";

    @Override
    public Context deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        JsonToken token = jp.getCurrentToken();
        if (JsonToken.FIELD_NAME.equals(token)) {
            token = jp.nextToken();
        }

        if (!JsonToken.START_OBJECT.equals(token)) {
            throw new JsonParseException("Unexpected token: " + token + ", expecting " + JsonToken.START_OBJECT,
                                         jp.getCurrentLocation());
        }

        token = jp.nextToken();
        Context result = new Context();

        while (!JsonToken.END_OBJECT.equals(token)) {
            if (!JsonToken.FIELD_NAME.equals(token)){
                throw new JsonMappingException("Expected " + JsonToken.FIELD_NAME + " found " + token,
                                               jp.getCurrentLocation());
            }
            boolean processed = processChildren(result, jp, ctxt);
            processed = processed || processParams(result, jp, ctxt);
            processed = processed || processFinals(result, jp, ctxt);
            if (processed){
                token = jp.nextToken();
            } else {
                token = jp.nextToken();
                jp.skipChildren();
                token = jp.nextToken();
            }
        }

        return result;
    }

    private boolean processChildren(Context result,JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken token = jp.getCurrentToken();
        if (!JsonToken.FIELD_NAME.equals(token) || !CHILDREN_FIELD.equals(jp.getCurrentName())) return false ;

        token = jp.nextToken();
        if (!JsonToken.START_ARRAY.equals(token)){
            throw new JsonMappingException(
                    "Failed to process context children, expecting " + JsonToken.START_ARRAY + " found " + token,
                    jp.getCurrentLocation());
        }

        token = jp.nextToken();
        while (!JsonToken.END_ARRAY.equals(token)){
            if (!JsonToken.START_OBJECT.equals(token)){
                throw new JsonMappingException(
                        "Failed to process context child, expecting " + JsonToken.START_OBJECT + " found " + token,
                        jp.getCurrentLocation());
            }

            Context child = deserialize(jp, ctxt);
            result.getChildren().add(child);

            token = jp.nextToken();
        }
        return true;
    }

    private boolean processParams(Context result,JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken token = jp.getCurrentToken();
        if (!JsonToken.FIELD_NAME.equals(token) || !PARAMS_FIELD.equals(jp.getCurrentName())) return false;

        token = jp.nextToken();
        Map<String,Object> params = jp.readValueAs(Map.class);
        result.putAll(params);
        return true;
    }

    private boolean processFinals(Context result,JsonParser jp, DeserializationContext ctxt) throws IOException{
        JsonToken token = jp.getCurrentToken();
        if (!JsonToken.FIELD_NAME.equals(token) || !FINALS_FIELD.equals(jp.getCurrentName())) return false;

        token = jp.nextToken();
        Set<String> finals = jp.readValueAs(Set.class);
        result.getFinals().addAll(finals);
        return true;
    }

}
