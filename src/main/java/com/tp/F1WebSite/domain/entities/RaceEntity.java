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
@Table(name = "races")
public class RaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long raceId;

    private Integer year;

    private String name;
}
