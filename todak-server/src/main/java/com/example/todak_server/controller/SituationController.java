package com.example.todak_server.controller;

import com.example.todak_server.service.SituationCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/situations")
@RequiredArgsConstructor
public class SituationController {

    private final SituationCardService situationCardService;

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
