package com.cargasafe.analytics.domain.model.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
public class IncidentDetail {

    private Instant timestamp;
    private String vehiclePlate;
    private String deviceId;
    private String type;

    public IncidentDetail(Instant timestamp, String vehiclePlate, String deviceId, String type) {
        this.timestamp = timestamp;
        this.vehiclePlate = vehiclePlate;
        this.deviceId = deviceId;
        this.type = type;
    }
}
