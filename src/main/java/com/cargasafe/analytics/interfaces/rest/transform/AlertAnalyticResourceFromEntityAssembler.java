package com.cargasafe.analytics.interfaces.rest.transform;

import com.cargasafe.analytics.domain.model.aggregates.AlertAnalytic;
import com.cargasafe.analytics.interfaces.rest.resources.AlertAnalyticResource;
import com.cargasafe.analytics.interfaces.rest.resources.LocationResource;

public class AlertAnalyticResourceFromEntityAssembler {

    public static AlertAnalyticResource toResourceFromEntity(AlertAnalytic entity) {
        LocationResource locationResource = null;
        if (entity.getLocation() != null) {
            locationResource = new LocationResource(
                    entity.getLocation().getLatitude(),
                    entity.getLocation().getLongitude(),
                    entity.getLocation().getAddress()
            );
        }
        return new AlertAnalyticResource(
                entity.getAlertId(),
                entity.getTripId() != null ? String.valueOf(entity.getTripId()) : null,
                entity.getDeviceId(),
                entity.getVehiclePlate(),
                entity.getType(),
                entity.getSeverity().name(),
                entity.getTimestamp(),
                locationResource,
                entity.getSensorData(),
                entity.getResolved()
        );
    }
}
