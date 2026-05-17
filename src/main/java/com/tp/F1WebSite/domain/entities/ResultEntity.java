package com.tp.F1WebSite.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "results",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"race_id", "driver_id"})
        })
public class ResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    @ManyToOne
    @JoinColumn(name = "race_Id", nullable = false)
    private RaceEntity race;

    @ManyToOne
    @JoinColumn(name = "driver_Id", nullable = false)
    private DriverEntity driver;

    @ManyToOne
    @JoinColumn(name = "constructor_Id", nullable = false)
    private ConstructorEntity constructor;

    @Column(nullable = false)
    private Integer grid;

    @Column(nullable = false)
    private  Integer position;

    @Column(nullable = false)
    private Double points;
}
