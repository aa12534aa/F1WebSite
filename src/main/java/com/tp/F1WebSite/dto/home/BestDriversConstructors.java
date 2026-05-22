package com.tp.F1WebSite.dto.home;

import com.tp.F1WebSite.dto.constructor.ConstructorWinsRacesDto;
import com.tp.F1WebSite.dto.driver.DriverWinsRacesDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BestDriversConstructors {

    private List<DriverWinsRacesDto> bestDrivers;

    private List<ConstructorWinsRacesDto> bestConstructors;
}
