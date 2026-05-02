package com.cargasafe.analytics.interfaces.rest.transform;

import com.cargasafe.analytics.domain.model.aggregates.TripAnalytic;
import com.cargasafe.analytics.interfaces.rest.resources.TripAnalyticResource;

public class TripAnalyticResourceFromEntityAssembler {

    public static TripAnalyticResource toResourceFromEntity(TripAnalytic entity) {
        return new TripAnalyticResource(
                entity.getTripId(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getOrigin(),
                entity.getDestination(),
                entity.getVehiclePlate(),
                entity.getDriverName(),
                entity.getCargoType(),
                entity.getStatus(),
                entity.getDistance(),
                entity.getAlertIds()
        );
    }
}
