package com.tp.F1WebSite.dto.constructor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConstructorWinsRacesDto {

    private Long constructorId;

    private String name;

    private Long numOfWins;

    private Long numOfRaces;
}
