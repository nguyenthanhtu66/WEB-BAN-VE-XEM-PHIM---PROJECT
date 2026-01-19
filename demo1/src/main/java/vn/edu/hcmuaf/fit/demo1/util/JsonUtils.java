package vn.edu.hcmuaf.fit.demo1.util;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Collection;

public class JsonUtils {

    // Convert Map to JSON string
    public static String toJson(Map<String, Object> map) {
        if (map == null) return "null";

        StringBuilder json = new StringBuilder();
        json.append("{");

        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;

            json.append("\"").append(escapeJson(entry.getKey())).append("\":");
            json.append(valueToJson(entry.getValue()));
        }

        json.append("}");
        return json.toString();
    }

    // Convert value to JSON
    private static String valueToJson(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "\"" + escapeJson((String) value) + "\"";
        } else if (value instanceof Number) {
            // Check if it's integer or floating point
            if (value instanceof Integer || value instanceof Long ||
                    value instanceof Short || value instanceof Byte) {
                return value.toString();
            } else {
                // Double, Float
                return String.format("%.2f", ((Number) value).doubleValue());
            }
        } else if (value instanceof Boolean) {
            return value.toString();
        } else if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) value;
            return toJson(map);
        } else if (value instanceof List) {
            return listToJson((List<?>) value);
        } else if (value instanceof Collection) {
            return collectionToJson((Collection<?>) value);
        } else if (value instanceof Object[]) {
            return arrayToJson((Object[]) value);
        } else {
            return "\"" + escapeJson(value.toString()) + "\"";
        }
    }

    // Convert List to JSON
    private static String listToJson(List<?> list) {
        if (list == null) return "null";

        StringBuilder json = new StringBuilder();
        json.append("[");

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

    // Convert Collection to JSON
    private static String collectionToJson(Collection<?> collection) {
        if (collection == null) return "null";

        StringBuilder json = new StringBuilder();
        json.append("[");

        boolean first = true;
        for (Object item : collection) {
            if (!first) {
                json.append(",");
            }
            first = false;

            json.append(valueToJson(item));
        }

        json.append("]");
        return json.toString();
    }

    // Convert Array to JSON
    private static String arrayToJson(Object[] array) {
        if (array == null) return "null";

        StringBuilder json = new StringBuilder();
        json.append("[");

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

    // Escape special JSON characters
    private static String escapeJson(String text) {
        if (text == null) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    // Kiểm tra ký tự control
                    if (c < ' ') {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    // Helper method để tạo JSON response nhanh
    public static String createSuccessJson(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("message", message);
        return toJson(map);
    }

    public static String createErrorJson(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", message);
        return toJson(map);
    }

    public static String createSuccessJson(String message, Map<String, Object> additionalData) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("message", message);
        if (additionalData != null) {
            map.putAll(additionalData);
        }
        return toJson(map);
    }
}