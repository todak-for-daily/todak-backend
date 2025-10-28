package com.example.todak_server.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class AiSessionContext {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;
    private String emotionCard;
    private String situationCardId;
    private String selectedAction;

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onCreate() {
        this.updatedAt = LocalDateTime.now();
    }

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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
