package com.example.todak_server.controller;

import com.example.todak_server.service.ScenarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "기능3: 낯선상황 시나리오", description = "기능3 시나리오 관련 조회 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScenarioController {
    private final ScenarioService scenarioService;

    @Operation(summary = "전체 카테고리 조회", description = "시나리오에 사용되는 모든 카테고리 리스트 반환")
    // 전체 카테고리 리스트
    @GetMapping("/categories")
    public Map<String, Object> getCategories() {
        return Map.of("categories", scenarioService.getCategories());
    }

    @Operation(summary = "카테고리별 장소 조회", description = "선택한 카테고리에 속한 장소 리스트 반환")
    // 특정 카테고리의 장소 리스트
    @GetMapping("/categories/{categoryId}/places")
    public Map<String , Object> getPlaces(@PathVariable String categoryId) {
        return Map.of("places", scenarioService.getPlaces(categoryId));
    }

    @Operation(summary = "장소별 시나리오 조회", description = "선택 장소에 해당하는 시나리오 리스트 반환")
    @GetMapping("/places/{placeId}/scenarios")
    public Map<String, Object> getScenarios(@PathVariable String placeId) {
        return Map.of("scenarios", scenarioService.getScenarios(placeId));
    }
}
