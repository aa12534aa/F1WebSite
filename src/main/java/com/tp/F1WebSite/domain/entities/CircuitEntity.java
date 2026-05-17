package com.tp.F1WebSite.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "circuits")
public class CircuitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long circuitId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String url;

    @OneToMany(mappedBy = "circuit")
    private List<RaceEntity> races;
}
