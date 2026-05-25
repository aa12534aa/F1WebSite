package com.tp.F1WebSite.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "races")
@SQLRestriction("is_deleted = false")
public class RaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long raceId;

    @ManyToOne
    @JoinColumn(name = "circuit_id", nullable = false)
    private CircuitEntity circuit;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "race")
    private List<ResultEntity> results;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isDeleted = false;
}
