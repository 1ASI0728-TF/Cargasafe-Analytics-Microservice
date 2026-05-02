package com.cargasafe.analytics.interfaces.rest;

import com.cargasafe.analytics.domain.exceptions.TripAnalyticNotFoundException;
import com.cargasafe.analytics.domain.model.queries.*;
import com.cargasafe.analytics.domain.services.AnalyticsQueryService;
import com.cargasafe.analytics.interfaces.rest.resources.AlertAnalyticResource;
import com.cargasafe.analytics.interfaces.rest.resources.MonthlyIncidentSummaryResource;
import com.cargasafe.analytics.interfaces.rest.resources.TripAnalyticResource;
import com.cargasafe.analytics.interfaces.rest.transform.AlertAnalyticResourceFromEntityAssembler;
import com.cargasafe.analytics.interfaces.rest.transform.MonthlyIncidentSummaryResourceFromEntityAssembler;
import com.cargasafe.analytics.interfaces.rest.transform.TripAnalyticResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Dashboard analytics endpoints")
public class AnalyticsController {

    private final AnalyticsQueryService analyticsQueryService;

    @Operation(summary = "Get all trip analytics")
    @GetMapping("/trips")
    public ResponseEntity<List<TripAnalyticResource>> getAllTripsAnalytics() {
        var resources = analyticsQueryService.handle(new GetAllTripsAnalyticsQuery()).stream()
                .map(TripAnalyticResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Get trip analytics by ID")
    @GetMapping("/trips/{id}")
    public ResponseEntity<TripAnalyticResource> getTripAnalyticById(@PathVariable Long id) {
        return analyticsQueryService.handle(new GetTripAnalyticByIdQuery(id))
                .map(TripAnalyticResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get alert analytics, optionally filtered by tripId")
    @GetMapping("/alerts")
    public ResponseEntity<List<AlertAnalyticResource>> getAlertsAnalytics(
            @RequestParam(value = "tripId", required = false) Long tripId) {
        var analytics = tripId != null
                ? analyticsQueryService.handle(new GetAlertsByTripIdQuery(tripId))
                : analyticsQueryService.handle(new GetAllAlertsAnalyticsQuery());
        var resources = analytics.stream()
                .map(AlertAnalyticResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Get incidents grouped by month")
    @GetMapping("/incidents-by-month")
    public ResponseEntity<List<MonthlyIncidentSummaryResource>> getIncidentsByMonth() {
        var resources = analyticsQueryService.handle(new GetIncidentsByMonthQuery()).stream()
                .map(MonthlyIncidentSummaryResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @ExceptionHandler(TripAnalyticNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTripNotFound(TripAnalyticNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("TRIP_NOT_FOUND", ex.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unexpected error in analytics controller", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Unexpected error", System.currentTimeMillis()));
    }

    private record ErrorResponse(String code, String message, Long timestamp) {}
}
