package org.northstar.servers.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.northstar.servers.exceptions.GenericServerProcessingException;

public class JSON {

    private JSON(){}

    public static <T> T toObject(Class<T> type,String payload){
        try {
            return Constants.OBJECT_MAPPER.readValue(payload,type);
        } catch (JsonProcessingException e) {
            throw new GenericServerProcessingException(e);
        }
    }

    public static byte[] toBytes(Object payload){
        try {
            return Constants.OBJECT_MAPPER.writeValueAsBytes(payload);
        } catch (JsonProcessingException e) {
            throw new GenericServerProcessingException(e);
        }
    }
    public static String toString(Object payload){
        try {
            return Constants.OBJECT_MAPPER.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new GenericServerProcessingException(e);
        }
    }
}
