package ru.demmy.keycloak.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public record ObjectMapperService(ObjectMapper objectMapper) {


    public String objectToJsonStringOrThrow(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public String objectToJsonStringOrToString(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            return o.toString();
        }
    }

    public <T> T stringToObjectOrNull(String s, Class<T> tClass) {
        try {
            return objectMapper.readValue(s, tClass);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> objectToMapOrThrow(Object o) {
        return objectMapper.convertValue(o, new TypeReference<Map<String, Object>>() {
        });
    }

    public Map<String, Object> objectToMapOrNull(Object o) {
        try {
            return objectMapper.convertValue(o, new TypeReference<Map<String, Object>>() {
            });
        } catch (Throwable t) {
            return null;
        }
    }

    public <T> T mapToObjectOrThrow(Map<String, Object> map, Class<T> tClass) {
        return objectMapper.convertValue(map, tClass);
    }

    public Map<String, Object> jsonStringToMapOrNull(String json) {
        try {
            MapType mapType = objectMapper.getTypeFactory()
                    .constructMapType(LinkedHashMap.class, String.class, Object.class);
            return objectMapper.readValue(json, mapType);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public <T> T stringToObjectOrThrow(String s, Class<T> tClass) {
        try {
            return objectMapper.readValue(s, tClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String findNodeValueByPathInJsonTree(String jsonString, String... path) {
        try {
            JsonNode root = objectMapper.readTree(jsonString);
            JsonNode locatedNode = appendPath(root, path);
            if (locatedNode == null || locatedNode.isEmpty()) {
                return null;
            }
            return objectMapper.writeValueAsString(locatedNode);
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T findValueFromJsonOrNull(@NonNull String jsonResponse,
                                          @NonNull Class<T> tClass,
                                          @NonNull List<String> includeTags,
                                          List<String> excludeTags) {

        try {
            Map<String, Object> responseMap = jsonStringToMapOrNull(jsonResponse);
            if (responseMap == null) {
                return null;
            }

            for (Map.Entry<String, Object> entry : responseMap.entrySet()) {
                boolean includeTagPresent = false;
                for (String includeTag : includeTags) {
                    if (entry.getKey().contains(includeTag)) {
                        includeTagPresent = true;
                        break;
                    }
                }
                if (!includeTagPresent) {
                    continue;
                }

                if (excludeTags != null && !excludeTags.isEmpty()) {
                    boolean excludeTagPresent = false;
                    for (String excludeTag : excludeTags) {
                        if (entry.getKey().contains(excludeTag)) {
                            excludeTagPresent = true;
                            break;
                        }
                    }
                    if (excludeTagPresent) {
                        continue;
                    }
                }
                return (T) entry.getValue();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private JsonNode appendPath(JsonNode node, String... path) {
        JsonNode located = null;
        for (String s : path) {
            located = node.path(s);
        }
        return located;
    }


}
