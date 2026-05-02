package com.cargasafe.analytics.domain.exceptions;

public class TripAnalyticNotFoundException extends RuntimeException {
    public TripAnalyticNotFoundException(Long tripId) {
        super("Trip analytic not found for trip ID: " + tripId);
    }
}
