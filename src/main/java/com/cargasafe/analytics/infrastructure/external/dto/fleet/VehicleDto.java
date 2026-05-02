package com.cargasafe.analytics.infrastructure.external.dto.fleet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleDto {
    private Long id;
    private String plate;
    private String type;
    private String status;
}
