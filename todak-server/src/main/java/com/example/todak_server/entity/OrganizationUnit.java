package com.example.todak_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class OrganizationUnit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private OrganizationUnit parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<OrganizationUnit> children = new ArrayList<>();

    public void addChild(OrganizationUnit child) {
        children.add(child);
        child.parent = this;
    }
}
