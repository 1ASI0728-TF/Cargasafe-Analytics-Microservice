package com.cargasafe.analytics.infrastructure.external.dto.alert;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncidentDto {
    private Long id;
    private Long alertId;
    private LocalDateTime createdAt;
    private LocalDateTime acknowledgeAt;
    private LocalDateTime closedAt;
}
