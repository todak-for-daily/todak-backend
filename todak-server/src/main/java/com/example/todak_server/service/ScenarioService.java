package com.example.todak_server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class ScenarioService {

    private final ResourceLoader loader;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Object> data;

    public ScenarioService(ResourceLoader loader) throws IOException {
        this.loader = loader;
        var resource = loader.getResource("classpath:data/scenario-data.json");
        try (InputStream in = resource.getInputStream()) {
            this.data = objectMapper.readValue(in, new TypeReference<>() {});
        }

    }
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getCategories() {
        return (List<Map<String, Object>>) data.get("categories");
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getPlaces(String categoryId) {
        List<Map<String, Object>> places = (List<Map<String, Object>>) data.get("places");
        if(categoryId == null) return places;
        return places.stream()
                .filter(p -> categoryId.equals(p.get("categoryId")))
                .toList();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getScenarios(String placeId) {
        List<Map<String, Object>> all = (List<Map<String, Object>>) data.get("scenarios");
        return all.stream()
                .filter(s -> placeId.equals(s.get("placeId")))
                .toList();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getGroupedPlaces() {
        List<Map<String, Object>> categories = (List<Map<String, Object>>) data.get("categories");
        List<Map<String, Object>> places = (List<Map<String, Object>>) data.get("places");

        return categories.stream().map(cat -> {
            String cid = (String) cat.get("id");
            List<String> items = places.stream()
                    .filter(p -> cid.equals(p.get("categoryId")))
                    .map(p -> (String) p.get("name"))
                    .toList();
            return Map.of("category", cat.get("name"), "itmes", items);
        }).toList();

    }
}
