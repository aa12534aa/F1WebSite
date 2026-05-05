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
@Table(name = "constructors")
public class ConstructorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long constructorId;

    private String name;
}
