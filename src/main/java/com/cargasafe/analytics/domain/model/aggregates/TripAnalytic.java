package com.cargasafe.analytics.domain.model.aggregates;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class TripAnalytic {

    private Long tripId;
    private Instant startDate;
    private Instant endDate;
    private String origin;
    private String destination;
    private String vehiclePlate;
    private String driverName;
    private String cargoType;
    private String status;
    private Integer distance;
    private List<String> alertIds = new ArrayList<>();
    private Long merchantId;
    private Long driverId;
    private Long deviceId;
    private Long vehicleId;

    public TripAnalytic(Long tripId, Instant startDate, Instant endDate, String origin,
                        String destination, String vehiclePlate, String driverName,
                        String cargoType, String status, Integer distance,
                        Long merchantId, Long driverId, Long deviceId, Long vehicleId) {
        this.tripId = tripId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.origin = origin;
        this.destination = destination;
        this.vehiclePlate = vehiclePlate;
        this.driverName = driverName;
        this.cargoType = cargoType;
        this.status = status;
        this.distance = distance;
        this.merchantId = merchantId;
        this.driverId = driverId;
        this.deviceId = deviceId;
        this.vehicleId = vehicleId;
        this.alertIds = new ArrayList<>();
    }

    public void addAlertId(String alertId) {
        if (!this.alertIds.contains(alertId)) {
            this.alertIds.add(alertId);
        }
    }
}
