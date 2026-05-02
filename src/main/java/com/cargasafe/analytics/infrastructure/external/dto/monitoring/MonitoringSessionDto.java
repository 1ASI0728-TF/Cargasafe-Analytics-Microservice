package com.cargasafe.analytics.infrastructure.external.dto.monitoring;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitoringSessionDto {
    private Long id;
    private String deviceId;
    private String tripId;
    private String status;
}
