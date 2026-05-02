package com.cargasafe.analytics.interfaces.rest.resources;

import java.util.List;

public record MonthlyIncidentSummaryResource(
        Long id,
        String month,
        Integer year,
        Integer temperatureIncidents,
        Integer movementIncidents,
        Integer totalIncidents,
        List<IncidentDetailResource> incidents
) {}
