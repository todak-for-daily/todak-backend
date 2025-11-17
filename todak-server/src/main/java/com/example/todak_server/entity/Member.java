package com.example.todak_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
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

    private String fcmToken;

    @Column(columnDefinition = "TEXT")
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    private Role role;  // EMPLOYEE, MANAGER

    @ManyToOne(fetch = FetchType.LAZY)
    private OrganizationUnit organizationUnit;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private AccessPermission accessPermission;

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

}
