package com.example.todak_server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationUnit {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // 조직의 상위 조직
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private OrganizationUnit parent;

    // 조직의 하위 조직들
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<OrganizationUnit> children = new ArrayList<>();

    public void addChild(OrganizationUnit child) {
        children.add(child);
        child.setParent(this);
    }
}

