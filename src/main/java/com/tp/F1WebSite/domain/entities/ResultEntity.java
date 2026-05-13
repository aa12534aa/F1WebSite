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
@Table(name = "results")
public class ResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    @ManyToOne
    @JoinColumn(name = "race_Id")
    private RaceEntity race;

    @ManyToOne
    @JoinColumn(name = "driver_Id")
    private DriverEntity driver;

    @ManyToOne
    @JoinColumn(name = "constructor_Id")
    private ConstructorEntity constructor;

    private Integer grid;

    private  Integer position;

    private Double points;
}
