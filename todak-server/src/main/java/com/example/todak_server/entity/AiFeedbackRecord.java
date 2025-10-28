package com.example.todak_server.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class AiFeedbackRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;
    private String emotionCard;
    private String situationCardId;
    private String selectedAction;

    private String beforeEmotion;
    private String afterEmotion;
    private int emotionChangeScore; // after - before :
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getEmotionCard() {
        return emotionCard;
    }

    public void setEmotionCard(String emotionCard) {
        this.emotionCard = emotionCard;
    }

    public String getSituationCardId() {
        return situationCardId;
    }

    public void setSituationCardId(String situationCardId) {
        this.situationCardId = situationCardId;
    }

    public String getSelectedAction() {
        return selectedAction;
    }

    public void setSelectedAction(String selectedAction) {
        this.selectedAction = selectedAction;
    }

    public String getBeforeEmotion() {
        return beforeEmotion;
    }

    public void setBeforeEmotion(String beforeEmotion) {
        this.beforeEmotion = beforeEmotion;
    }

    public String getAfterEmotion() {
        return afterEmotion;
    }

    public void setAfterEmotion(String afterEmotion) {
        this.afterEmotion = afterEmotion;
    }

    public int getEmotionChangeScore() {
        return emotionChangeScore;
    }

    public void setEmotionChangeScore(int emotionChangeScore) {
        this.emotionChangeScore = emotionChangeScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
