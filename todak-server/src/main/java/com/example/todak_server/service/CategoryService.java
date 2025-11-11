package com.example.todak_server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.util.LinkedHashSet;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final List<Map<String, Object>> situations = new ArrayList<>();
    private final Map<String, String> categoryNameMap = Map.of(
            "ENV", "주변 때문에 힘들어요",
            "BODY", "내 몸이 힘들어요",
            "ACT", "해야 하는 일이 힘들어요",
            "COMM", "대화가 힘들어요",
            "MIND", "마음이 힘들어요"
    );

    @PostConstruct
    public void loadSituations() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = new ClassPathResource("data/situation_cards.json").getInputStream()) {
            List<Map<String, Object>> list = mapper.readValue(inputStream, new TypeReference<>() {});
            situations.addAll(list);
        }
    }

    // 카테고리 목록 반환
    public List<Map<String, String>> getCategories() {
        Set<String> categoryCodes = situations.stream()
                .map(s -> (String) s.get("category"))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<Map<String, String>> result = new ArrayList<>();
        for (String code : categoryCodes) {
            result.add(Map.of(
                    "id", code,
                    "text", categoryNameMap.getOrDefault(code, code)
            ));
        }
        return result;
    }
}
