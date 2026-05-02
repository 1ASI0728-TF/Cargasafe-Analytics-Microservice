package com.cargasafe.analytics.infrastructure.external.dto.alert;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertDto {
    private Long id;
    private Long deliveryOrderId;
    private String alertType;
    private String alertStatus;
    private List<IncidentDto> incidents;
}
