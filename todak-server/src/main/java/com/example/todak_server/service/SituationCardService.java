package com.example.todak_server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SituationCardService {

    private final List<Map<String, Object>> situationCards = new ArrayList<>();

    @PostConstruct
    public void loadSituationCards() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("data/situation_cards.json");

        try (InputStream is = resource.getInputStream()) {
            List<Map<String, Object>> list = mapper.readValue(is, new TypeReference<>() {});
            situationCards.addAll(list);
        }

        //System.out.println(" Situation cards loaded: " + situationCards.size() + " items");
    }

    // 특정 카테고리의 상황 리스트 반환
    public List<Map<String, Object>> getSituationsByCategory(String category) {
        return situationCards.stream()
                .filter(card -> category.equals(card.get("category")))
                .toList();
    }

    // ID로 특정 상황 조회
    public Map<String, Object> getSituationById(String id) {
        return situationCards.stream()
                .filter(card -> id.equals(card.get("id")))
                .findFirst()
                .orElse(null);
    }

    // ID로 상황의 text 반환
    public String getSituationText(String id) {
        Map<String, Object> card = getSituationById(id);
        return card == null ? null : (String) card.get("text");
    }
}
