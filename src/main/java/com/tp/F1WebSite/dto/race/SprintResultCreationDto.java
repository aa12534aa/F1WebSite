package com.tp.F1WebSite.dto.race;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SprintResultCreationDto {

    @NotBlank
    private String driverUrl;

    @NotBlank
    private String constructorName;

    @NotNull
    private Integer grid;

    @NotNull
    private Integer position;

    @NotNull
    private Double points;
}
