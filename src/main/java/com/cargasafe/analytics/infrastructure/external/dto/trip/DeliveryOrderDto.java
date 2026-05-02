package com.cargasafe.analytics.infrastructure.external.dto.trip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeliveryOrderDto {
    private Long id;
    private Long tripId;
    private String address;
    private Double latitude;
    private Double longitude;
}
