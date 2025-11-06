package com.example.todak_server.controller;

import com.example.todak_server.service.ScenarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScenarioController {
    private final ScenarioService scenarioService;

    // 전체 카테고리 리스트
    @GetMapping("/categories")
    public Map<String, Object> getCategories() {
        return Map.of("categories", scenarioService.getCategories());
    }

    // 특정 카테고리의 장소 리스트
    @GetMapping("/categories/{categoryId}/places")
    public Map<String , Object> getPlaces(@PathVariable String categoryId) {
        return Map.of("places", scenarioService.getPlaces(categoryId));
    }

    @GetMapping("/places/{placeId}/scenarios")
    public Map<String, Object> getScenarios(@PathVariable String placeId) {
        return Map.of("scenarios", scenarioService.getScenarios(placeId));
    }
}
