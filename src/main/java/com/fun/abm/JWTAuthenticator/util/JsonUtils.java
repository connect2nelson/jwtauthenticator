package com.fun.abm.JWTAuthenticator.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

public class JsonUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);
    private static ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static String toJson(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error converting to json [{}]", o, e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            LOGGER.error("Error converting json [{}] to type [{}]", json, type, e);
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, Object> jsonObjectToMap(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        try {
            String json = jsonObject.toString();
            HashMap<String, Object> map = mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {
            });
            return map;
        } catch (Exception e) {
            throw new RuntimeException("JSON to map conversion error", e);
        }
    }

}
