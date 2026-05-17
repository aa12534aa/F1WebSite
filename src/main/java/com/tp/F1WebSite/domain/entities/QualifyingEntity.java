package com.tp.F1WebSite.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "qualifying",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"race_id", "driver_id"})
        })
public class QualifyingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qualifyId;

    @ManyToOne
    @JoinColumn(name = "race_id", nullable = false)
    private RaceEntity race;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private DriverEntity driver;


    @ManyToOne
    @JoinColumn(name = "constructor_id", nullable = false)
    private ConstructorEntity constructor;

    @Column(nullable = false)
    private Integer position;
}
