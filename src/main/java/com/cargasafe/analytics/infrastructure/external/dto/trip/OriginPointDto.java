package com.cargasafe.analytics.infrastructure.external.dto.trip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OriginPointDto {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
}
