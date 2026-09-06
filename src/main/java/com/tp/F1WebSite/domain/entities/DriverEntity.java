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
@Table(name = "drivers")
@SQLRestriction("is_deleted = false")
public class DriverEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long driverId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nationality;

    @Column(nullable = false, unique = true)
    private String url;

    @OneToMany(mappedBy = "driver")
    private List<ResultEntity> results;

    @OneToMany(mappedBy = "driver")
    private List<QualifyingEntity> qualifying;

    @OneToMany(mappedBy = "driver")
    private List<SprintResultEntity> sprintResults;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isDeleted = false;
}
