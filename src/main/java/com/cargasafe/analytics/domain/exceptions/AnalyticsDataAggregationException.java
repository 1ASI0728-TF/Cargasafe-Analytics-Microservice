package com.cargasafe.analytics.domain.exceptions;

public class AnalyticsDataAggregationException extends RuntimeException {
    public AnalyticsDataAggregationException(String message) {
        super("Analytics data aggregation error: " + message);
    }
}
