package com.example.todak_server.controller;

import com.example.todak_server.service.SituationCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "기능2: 감정 및 카테고리 선택 후 상황 리스트 관련", description = "카테고리별 전체 상황 리스트")
@RestController
@RequestMapping("/api/situations")
@RequiredArgsConstructor
public class SituationController {

    private final SituationCardService situationCardService;

    @Operation(summary = "카테고리별 전체 상황 리스트 조회", description = "예) [환경] 관련된 모든 상황 리스트 (불빛이 밝아요 등) 조회")
    // 카테고리별 상태 전체 리스트 조회
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getSituations(@RequestParam String category) {
        return ResponseEntity.ok(situationCardService.getSituationsByCategory(category));
    }

    // 상황 id로 텍스트 조회 (테스트용!)
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSituationById(@PathVariable String id) {
        Map<String, Object> situation = situationCardService.getSituationById(id);
        if(situation == null) {return ResponseEntity.notFound().build();}
        return ResponseEntity.ok(situation);
    }

}
