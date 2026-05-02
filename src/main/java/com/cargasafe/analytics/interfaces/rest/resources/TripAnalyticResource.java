package com.cargasafe.analytics.interfaces.rest.resources;

import java.time.Instant;
import java.util.List;

public record TripAnalyticResource(
        Long id,
        Instant startDate,
        Instant endDate,
        String origin,
        String destination,
        String vehiclePlate,
        String driverName,
        String cargoType,
        String status,
        Integer distance,
        List<String> alerts
) {}
