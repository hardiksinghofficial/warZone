package com.warzone.common;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApiResponse {

    public static Map<String, Object> ok(Object data) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("timestamp", LocalDateTime.now().toString());
        result.put("data", data);
        return result;
    }

    public static Map<String, Object> list(Object data, int count) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("count", count);
        result.put("timestamp", LocalDateTime.now().toString());
        result.put("data", data);
        return result;
    }

    public static Map<String, Object> error(String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", false);
        result.put("timestamp", LocalDateTime.now().toString());
        result.put("error", message);
        return result;
    }
}
