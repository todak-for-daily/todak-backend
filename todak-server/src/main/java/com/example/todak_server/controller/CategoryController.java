package com.example.todak_server.controller;

import com.example.todak_server.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "기능2: 상황 카테고리 리스트 반환")
@RestController
@RequestMapping("/api/situations/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "상황 카테고리 리스트 반환", description = "[{\"id\":\"ENV\",\"text\":\"주변 때문에 힘들어요\"},{\"id\":\"BODY\",\"text\":\"내 몸이 힘들어요\"}, .. ")
    @GetMapping
    public ResponseEntity<List<Map<String, String>>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }
}
