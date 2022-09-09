package org.rpc.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author yangcong
 * 
 *      JSON工具类
 */
public class JSONUtils {
    
    /**
     * 获取json对应的值
     * @param <T>
     * @param json
     * @param key
     * @return
     */
    public static <T>T get(String json, String key){
        JsonNode jsonNode = null;
        try {
            jsonNode = new ObjectMapper().readTree(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if(jsonNode.isTextual()){
            return (T)jsonNode.asText();
        }else if(jsonNode.isInt()){
            return (T) Integer.valueOf(jsonNode.asInt());
        }else if(jsonNode.isBoolean()){
            return (T) Boolean.valueOf(jsonNode.asBoolean());
        }
        return null;
    }
}
