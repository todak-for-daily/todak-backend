package com.example.todak_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AccessPermission {

    @Id @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private boolean canViewSchedule;
    private boolean canViewWarning;
    private boolean canViewHealth;

}
