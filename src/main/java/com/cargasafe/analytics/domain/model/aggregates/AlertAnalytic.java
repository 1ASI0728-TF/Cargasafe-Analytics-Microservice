package com.cargasafe.analytics.domain.model.aggregates;

import com.cargasafe.analytics.domain.model.valueobjects.AlertSeverity;
import com.cargasafe.analytics.domain.model.valueobjects.LocationData;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class AlertAnalytic {

    private Long alertId;
    private Long tripId;
    private String deviceId;
    private String vehiclePlate;
    private String type;
    private AlertSeverity severity;
    private Instant timestamp;
    private Instant startDate;
    private Instant acknowledgedDate;
    private Instant endDate;
    private LocationData location;
    private Map<String, String> sensorData = new HashMap<>();
    private Instant sensorTimestamp;
    private Boolean resolved;
    private Long deliveryOrderId;

    public AlertAnalytic(Long alertId, Long tripId, String deviceId, String vehiclePlate,
                         String type, AlertSeverity severity, Instant timestamp,
                         LocationData location, Boolean resolved, Long deliveryOrderId,
                         Instant startDate, Instant acknowledgedDate, Instant endDate) {
        this.alertId = alertId;
        this.tripId = tripId;
        this.deviceId = deviceId;
        this.vehiclePlate = vehiclePlate;
        this.type = type;
        this.severity = severity;
        this.timestamp = timestamp;
        this.location = location;
        this.resolved = resolved;
        this.deliveryOrderId = deliveryOrderId;
        this.startDate = startDate;
        this.acknowledgedDate = acknowledgedDate;
        this.endDate = endDate;
        this.sensorData = new HashMap<>();
    }

    public void setSensorData(Map<String, String> sensorData, Instant sensorTimestamp) {
        this.sensorData = new HashMap<>(sensorData);
        this.sensorTimestamp = sensorTimestamp;
    }
}
