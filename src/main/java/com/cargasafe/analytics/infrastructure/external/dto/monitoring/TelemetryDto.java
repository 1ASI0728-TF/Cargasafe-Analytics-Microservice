package com.cargasafe.analytics.infrastructure.external.dto.monitoring;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TelemetryDto {
    private Long id;
    private Float temperature;
    private Float humidity;
    private Float vibration;
    private Float latitude;
    private Float longitude;
}
