package com.example.todak_server.controller;

import com.example.todak_server.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/situations/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    
    @GetMapping
    public ResponseEntity<List<Map<String, String>>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }
}
