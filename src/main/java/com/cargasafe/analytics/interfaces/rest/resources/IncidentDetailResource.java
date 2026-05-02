package com.cargasafe.analytics.interfaces.rest.resources;

import java.time.Instant;

public record IncidentDetailResource(Instant timestamp, String vehiclePlate, String deviceId, String type) {}
