package vn.edu.hcmuaf.fit.demo1.util;

import java.util.List;
import java.util.Map;

public class JsonUtil {

    public static String toJson(Map<String, Object> map) {
        if (map == null) return "null";

        StringBuilder json = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;

            json.append("\"").append(entry.getKey()).append("\":");
            json.append(valueToJson(entry.getValue()));
        }

        json.append("}");
        return json.toString();
    }

    private static String valueToJson(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "\"" + escapeJson((String) value) + "\"";
        } else if (value instanceof Boolean) {
            return ((Boolean) value) ? "true" : "false";
        } else if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Map) {
            return toJson((Map<String, Object>) value);
        } else if (value instanceof List) {
            return listToJson((List<?>) value);
        } else if (value instanceof Object[]) {
            return arrayToJson((Object[]) value);
        } else {
            return "\"" + escapeJson(value.toString()) + "\"";
        }
    }

    private static String listToJson(List<?> list) {
        if (list == null) return "null";

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        for (Object item : list) {
            if (!first) {
                json.append(",");
            }
            first = false;

            json.append(valueToJson(item));
        }

        json.append("]");
        return json.toString();
    }

    private static String arrayToJson(Object[] array) {
        if (array == null) return "null";

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        for (Object item : array) {
            if (!first) {
                json.append(",");
            }
            first = false;

            json.append(valueToJson(item));
        }

        json.append("]");
        return json.toString();
    }

    private static String escapeJson(String str) {
        if (str == null) return "";

        StringBuilder escaped = new StringBuilder();
        for (char c : str.toCharArray()) {
            switch (c) {
                case '"': escaped.append("\\\""); break;
                case '\\': escaped.append("\\\\"); break;
                case '\b': escaped.append("\\b"); break;
                case '\f': escaped.append("\\f"); break;
                case '\n': escaped.append("\\n"); break;
                case '\r': escaped.append("\\r"); break;
                case '\t': escaped.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) c));
                    } else {
                        escaped.append(c);
                    }
            }
        }
        return escaped.toString();
    }

    // Helper để tạo JSON response nhanh
    public static String createResponse(boolean success, String message) {
        return "{\"success\":" + success + ",\"message\":\"" + escapeJson(message) + "\"}";
    }

    public static String createResponse(boolean success, String message, Object data) {
        return "{\"success\":" + success +
                ",\"message\":\"" + escapeJson(message) + "\"" +
                ",\"data\":" + valueToJson(data) + "}";
    }
}