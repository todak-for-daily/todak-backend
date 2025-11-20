package com.example.todak_server.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 공통 필드

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HabitType type;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(length = 500)
    private String soothingAction; // 힘들 때 도움이 되는 행동 (선택)

    // 감각 (SENSE) 일 때
    @Enumerated(EnumType.STRING)
    private HabitSenseType senseType; // 시각, 청각, 미각, 후각, 촉각, 운동감각

    // 인지 (COGNITOMN)일 때
    private String time;

    private String place;

    private String target;

    // 공통 : 생성/수정시간
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public HabitType getType() {
        return type;
    }

    public void setType(HabitType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSoothingAction() {
        return soothingAction;
    }

    public void setSoothingAction(String soothingAction) {
        this.soothingAction = soothingAction;
    }

    public HabitSenseType getSenseType() {
        return senseType;
    }

    public void setSenseType(HabitSenseType senseType) {
        this.senseType = senseType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
