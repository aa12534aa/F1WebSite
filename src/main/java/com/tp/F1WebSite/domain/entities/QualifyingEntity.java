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
@Table(name = "qualifying")
public class QualifyingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qualifyId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "race_id")
    private RaceEntity race;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "driver_id")
    private DriverEntity driver;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "constructor_id")
    private ConstructorEntity constructor;

    private Integer position;
}
