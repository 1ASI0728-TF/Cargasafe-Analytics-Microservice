package com.cargasafe.analytics.interfaces.rest.resources;

import java.time.Instant;
import java.util.Map;

public record AlertAnalyticResource(
        Long id,
        String tripId,
        String deviceId,
        String vehiclePlate,
        String type,
        String severity,
        Instant timestamp,
        LocationResource location,
        Map<String, String> sensorData,
        Boolean resolved
) {}
