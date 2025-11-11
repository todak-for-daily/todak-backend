package com.example.todak_server.service;

import com.example.todak_server.dto.response.EmotionCardResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmotionCardService {

    private final Map<String, Integer> emotionLevelMap = new HashMap<>();
    private final List<EmotionCardResponse> emotionCards = new ArrayList<>();

    @PostConstruct
    public void loadEmotionCards() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new ClassPathResource("data/emotion_cards.json").getFile();

        List<Map<String, Object>> list = mapper.readValue(file, new TypeReference<>() {});
        for (Map<String, Object> item : list) {
            String id = (String) item.get("id");
            String text = (String) item.get("text");
            Integer level = (Integer) item.get("level");
            emotionCards.add(new EmotionCardResponse(id,text));
            emotionLevelMap.put(text, level);
        }

        //System.out.println(" Emotion cards loaded: " + emotionLevelMap);
    }



    /** 감정카드 전체 리스트 반환 */
    public List<EmotionCardResponse> getEmotionCards() {
        return emotionCards;
    }

    /** 감정의 레벨 반환 */
    public int getLevel(String text) {
        return emotionLevelMap.getOrDefault(text, 0);
    }

    /** 힘든 상태인지 판별 (레벨이 3 이하인 경우) */
    public boolean isHardEmotion(String text) {
        int level = getLevel(text);
        return level <= 3;
    }


}

