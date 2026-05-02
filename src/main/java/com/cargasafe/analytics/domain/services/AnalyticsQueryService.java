package com.cargasafe.analytics.domain.services;

import com.cargasafe.analytics.domain.model.aggregates.AlertAnalytic;
import com.cargasafe.analytics.domain.model.aggregates.TripAnalytic;
import com.cargasafe.analytics.domain.model.entities.MonthlyIncidentSummary;
import com.cargasafe.analytics.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

public interface AnalyticsQueryService {
    List<TripAnalytic> handle(GetAllTripsAnalyticsQuery query);
    Optional<TripAnalytic> handle(GetTripAnalyticByIdQuery query);
    List<AlertAnalytic> handle(GetAllAlertsAnalyticsQuery query);
    List<AlertAnalytic> handle(GetAlertsByTripIdQuery query);
    List<MonthlyIncidentSummary> handle(GetIncidentsByMonthQuery query);
}
