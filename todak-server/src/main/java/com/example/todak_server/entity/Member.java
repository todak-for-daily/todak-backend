package com.example.todak_server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
