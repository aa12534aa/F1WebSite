package com.tp.F1WebSite.dto.race;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RaceCreationDto {

    @NotBlank
    private String name;

    @NotNull
    private LocalDate date;

    @NotBlank
    private String circuitName;

    @NotNull
    private Boolean sprintAppearance;
}
