package com.example.todak_server.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provider;
    private String providerId;

    private String email;
    private String nickname;

    private Integer emoPeriod;
    private Integer dayAlarm;

    // Habit 연결
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Habit> habits = new ArrayList<>();

    public List<GeneralSchedule> getGeneralSchedules() {
        return generalSchedules;
    }

    public void setGeneralSchedules(List<GeneralSchedule> generalSchedules) {
        this.generalSchedules = generalSchedules;
    }

    public List<Habit> getHabits() {
        return habits;
    }

    public void setHabits(List<Habit> habits) {
        this.habits = habits;
    }

    // GeneralSchedule 연결
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<GeneralSchedule> generalSchedules = new ArrayList<>();

    public Integer getEmoPeriod() {
        return emoPeriod;
    }

    public void setEmoPeriod(Integer emoPeriod) {
        this.emoPeriod = emoPeriod;
    }

    public Integer getDayAlarm() {
        return dayAlarm;
    }

    public void setDayAlarm(Integer dayAlarm) {
        this.dayAlarm = dayAlarm;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


}
