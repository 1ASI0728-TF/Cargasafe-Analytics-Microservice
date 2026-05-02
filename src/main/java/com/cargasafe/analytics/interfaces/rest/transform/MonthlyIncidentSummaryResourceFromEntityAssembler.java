package com.cargasafe.analytics.interfaces.rest.transform;

import com.cargasafe.analytics.domain.model.entities.MonthlyIncidentSummary;
import com.cargasafe.analytics.interfaces.rest.resources.IncidentDetailResource;
import com.cargasafe.analytics.interfaces.rest.resources.MonthlyIncidentSummaryResource;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class MonthlyIncidentSummaryResourceFromEntityAssembler {

    private static final AtomicLong idCounter = new AtomicLong(1);

    public static MonthlyIncidentSummaryResource toResourceFromEntity(MonthlyIncidentSummary entity) {
        List<IncidentDetailResource> incidents = entity.getIncidents().stream()
                .map(d -> new IncidentDetailResource(d.getTimestamp(), d.getVehiclePlate(), d.getDeviceId(), d.getType()))
                .toList();
        return new MonthlyIncidentSummaryResource(
                idCounter.getAndIncrement(),
                entity.getMonth(),
                entity.getYear(),
                entity.getTemperatureIncidents(),
                entity.getMovementIncidents(),
                entity.getTotalIncidents(),
                incidents
        );
    }
}
