package com.cargasafe.analytics.infrastructure.external.dto.trip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TripDto {
    private Long id;
    private Long merchantId;
    private Long driverId;
    private Long deviceId;
    private Long vehicleId;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private OriginPointDto originPoint;
    private List<DeliveryOrderDto> deliveryOrders;
}
