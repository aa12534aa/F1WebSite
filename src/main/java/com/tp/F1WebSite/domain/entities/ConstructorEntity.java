package com.tp.F1WebSite.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "constructors")
@SQLRestriction("is_deleted = false")
public class ConstructorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long constructorId;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "constructor")
    private List<ResultEntity> results;

    @OneToMany(mappedBy = "constructor")
    private List<QualifyingEntity> qualifying;

    @OneToMany(mappedBy = "constructor")
    private List<SprintResultEntity> sprintResults;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isDeleted = false;
}
