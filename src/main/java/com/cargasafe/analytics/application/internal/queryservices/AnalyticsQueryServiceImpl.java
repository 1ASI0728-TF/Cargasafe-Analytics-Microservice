package com.cargasafe.analytics.application.internal.queryservices;

import com.cargasafe.analytics.domain.exceptions.AnalyticsDataAggregationException;
import com.cargasafe.analytics.domain.exceptions.TripAnalyticNotFoundException;
import com.cargasafe.analytics.domain.model.aggregates.AlertAnalytic;
import com.cargasafe.analytics.domain.model.aggregates.TripAnalytic;
import com.cargasafe.analytics.domain.model.entities.IncidentDetail;
import com.cargasafe.analytics.domain.model.entities.MonthlyIncidentSummary;
import com.cargasafe.analytics.domain.model.queries.*;
import com.cargasafe.analytics.domain.model.valueobjects.AlertSeverity;
import com.cargasafe.analytics.domain.model.valueobjects.LocationData;
import com.cargasafe.analytics.domain.services.AnalyticsQueryService;
import com.cargasafe.analytics.infrastructure.external.*;
import com.cargasafe.analytics.infrastructure.external.dto.alert.AlertDto;
import com.cargasafe.analytics.infrastructure.external.dto.alert.IncidentDto;
import com.cargasafe.analytics.infrastructure.external.dto.monitoring.MonitoringSessionDto;
import com.cargasafe.analytics.infrastructure.external.dto.monitoring.TelemetryDto;
import com.cargasafe.analytics.infrastructure.external.dto.trip.DeliveryOrderDto;
import com.cargasafe.analytics.infrastructure.external.dto.trip.TripDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsQueryServiceImpl implements AnalyticsQueryService {

    private final ExternalTripService tripService;
    private final ExternalAlertService alertService;
    private final ExternalFleetService fleetService;
    private final ExternalProfileService profileService;
    private final ExternalMonitoringService monitoringService;

    @Override
    public List<TripAnalytic> handle(GetAllTripsAnalyticsQuery query) {
        try {
            List<TripDto> trips = tripService.getAllTrips();
            List<AlertDto> allAlerts = alertService.getAllAlerts();
            return trips.stream()
                    .map(trip -> buildTripAnalytic(trip, allAlerts))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error aggregating trip analytics", e);
            throw new AnalyticsDataAggregationException(e.getMessage());
        }
    }

    @Override
    public Optional<TripAnalytic> handle(GetTripAnalyticByIdQuery query) {
        TripDto trip = tripService.getTripById(query.tripId())
                .orElseThrow(() -> new TripAnalyticNotFoundException(query.tripId()));
        List<AlertDto> allAlerts = alertService.getAllAlerts();
        return buildTripAnalytic(trip, allAlerts);
    }

    @Override
    public List<AlertAnalytic> handle(GetAllAlertsAnalyticsQuery query) {
        try {
            List<AlertDto> alerts = alertService.getAllAlerts();
            List<TripDto> allTrips = tripService.getAllTrips();
            return alerts.stream()
                    .map(alert -> buildAlertAnalytic(alert, allTrips))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error aggregating alert analytics", e);
            throw new AnalyticsDataAggregationException(e.getMessage());
        }
    }

    @Override
    public List<AlertAnalytic> handle(GetAlertsByTripIdQuery query) {
        try {
            TripDto trip = tripService.getTripById(query.tripId()).orElse(null);
            if (trip == null || trip.getDeliveryOrders() == null) return Collections.emptyList();

            Set<Long> deliveryOrderIds = trip.getDeliveryOrders().stream()
                    .map(DeliveryOrderDto::getId)
                    .collect(Collectors.toSet());

            List<AlertDto> allAlerts = alertService.getAllAlerts();
            List<TripDto> allTrips = tripService.getAllTrips();

            return allAlerts.stream()
                    .filter(a -> a.getDeliveryOrderId() != null && deliveryOrderIds.contains(a.getDeliveryOrderId()))
                    .map(alert -> buildAlertAnalytic(alert, allTrips))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error aggregating alert analytics for trip {}", query.tripId(), e);
            throw new AnalyticsDataAggregationException(e.getMessage());
        }
    }

    @Override
    public List<MonthlyIncidentSummary> handle(GetIncidentsByMonthQuery query) {
        try {
            List<AlertDto> alerts = alertService.getAllAlerts();
            Map<String, Map<Integer, List<AlertDto>>> byMonthAndYear = new HashMap<>();

            for (AlertDto alert : alerts) {
                if (alert.getIncidents() == null || alert.getIncidents().isEmpty()) continue;
                IncidentDto first = alert.getIncidents().get(0);
                if (first.getCreatedAt() == null) continue;

                Instant ts = first.getCreatedAt().atZone(ZoneOffset.UTC).toInstant();
                int year = ts.atZone(ZoneOffset.UTC).getYear();
                String month = ts.atZone(ZoneOffset.UTC).getMonth()
                        .getDisplayName(TextStyle.FULL, Locale.of("es", "ES"));

                byMonthAndYear
                        .computeIfAbsent(month, k -> new HashMap<>())
                        .computeIfAbsent(year, k -> new ArrayList<>())
                        .add(alert);
            }

            List<MonthlyIncidentSummary> summaries = new ArrayList<>();
            for (var monthEntry : byMonthAndYear.entrySet()) {
                String month = monthEntry.getKey();
                for (var yearEntry : monthEntry.getValue().entrySet()) {
                    Integer year = yearEntry.getKey();
                    List<AlertDto> monthAlerts = yearEntry.getValue();

                    long temperatureIncidents = monthAlerts.stream()
                            .filter(a -> "TEMPERATURE".equals(a.getAlertType()))
                            .mapToLong(a -> a.getIncidents() == null ? 0 : a.getIncidents().size())
                            .sum();
                    long movementIncidents = monthAlerts.stream()
                            .filter(a -> "VIBRATION".equals(a.getAlertType()) || "TILT".equals(a.getAlertType()))
                            .mapToLong(a -> a.getIncidents() == null ? 0 : a.getIncidents().size())
                            .sum();
                    int total = monthAlerts.stream()
                            .mapToInt(a -> a.getIncidents() == null ? 0 : a.getIncidents().size())
                            .sum();

                    MonthlyIncidentSummary summary = new MonthlyIncidentSummary(
                            month, year, (int) temperatureIncidents, (int) movementIncidents, total);

                    for (AlertDto alert : monthAlerts) {
                        if (alert.getIncidents() == null) continue;
                        for (IncidentDto inc : alert.getIncidents()) {
                            Instant incTs = inc.getCreatedAt() != null
                                    ? inc.getCreatedAt().atZone(ZoneOffset.UTC).toInstant() : Instant.now();
                            summary.addIncident(new IncidentDetail(incTs, "Unknown", "Unknown", alert.getAlertType()));
                        }
                    }
                    summaries.add(summary);
                }
            }
            return summaries;
        } catch (Exception e) {
            log.error("Error aggregating monthly incidents", e);
            throw new AnalyticsDataAggregationException(e.getMessage());
        }
    }

    private Optional<TripAnalytic> buildTripAnalytic(TripDto trip, List<AlertDto> allAlerts) {
        try {
            String vehiclePlate = fleetService.getVehiclePlate(trip.getVehicleId()).orElse("Unknown");
            String driverName = profileService.getDriverName(trip.getDriverId()).orElse("Unknown Driver");

            String origin = (trip.getOriginPoint() != null) ? trip.getOriginPoint().getAddress() : "Unknown";
            String destination = "Unknown";
            if (trip.getDeliveryOrders() != null && !trip.getDeliveryOrders().isEmpty()) {
                DeliveryOrderDto last = trip.getDeliveryOrders().get(trip.getDeliveryOrders().size() - 1);
                if (last.getAddress() != null) destination = last.getAddress();
            }

            Instant startDate = trip.getStartedAt() != null
                    ? trip.getStartedAt().atZone(ZoneOffset.UTC).toInstant() : null;
            Instant endDate = trip.getCompletedAt() != null
                    ? trip.getCompletedAt().atZone(ZoneOffset.UTC).toInstant() : null;

            TripAnalytic analytic = new TripAnalytic(
                    trip.getId(), startDate, endDate, origin, destination,
                    vehiclePlate, driverName, "General", trip.getStatus(),
                    0, trip.getMerchantId(), trip.getDriverId(), trip.getDeviceId(), trip.getVehicleId()
            );

            if (trip.getDeliveryOrders() != null) {
                Set<Long> doIds = trip.getDeliveryOrders().stream()
                        .map(DeliveryOrderDto::getId).collect(Collectors.toSet());
                allAlerts.stream()
                        .filter(a -> a.getDeliveryOrderId() != null && doIds.contains(a.getDeliveryOrderId()))
                        .forEach(a -> analytic.addAlertId(String.valueOf(a.getId())));
            }

            return Optional.of(analytic);
        } catch (Exception e) {
            log.error("Error building TripAnalytic for trip {}", trip.getId(), e);
            return Optional.empty();
        }
    }

    private Optional<AlertAnalytic> buildAlertAnalytic(AlertDto alert, List<TripDto> allTrips) {
        try {
            Long tripId = null;
            String vehiclePlate = "Unknown";

            if (alert.getDeliveryOrderId() != null) {
                for (TripDto trip : allTrips) {
                    if (trip.getDeliveryOrders() == null) continue;
                    boolean found = trip.getDeliveryOrders().stream()
                            .anyMatch(d -> d.getId().equals(alert.getDeliveryOrderId()));
                    if (found) {
                        tripId = trip.getId();
                        vehiclePlate = fleetService.getVehiclePlate(trip.getVehicleId()).orElse("Unknown");
                        break;
                    }
                }
            }

            String deviceId = "Unknown";
            LocationData location = null;
            Map<String, String> sensorDataMap = new HashMap<>();

            if (tripId != null) {
                Optional<MonitoringSessionDto> sessionOpt = monitoringService.getSessionByTripId(tripId);
                if (sessionOpt.isPresent()) {
                    MonitoringSessionDto session = sessionOpt.get();
                    deviceId = session.getDeviceId() != null ? session.getDeviceId() : "Unknown";

                    Optional<TelemetryDto> telemetryOpt = monitoringService.getLatestTelemetry(session.getId());
                    if (telemetryOpt.isPresent()) {
                        TelemetryDto t = telemetryOpt.get();
                        location = new LocationData(
                                t.getLatitude() != null ? t.getLatitude().doubleValue() : null,
                                t.getLongitude() != null ? t.getLongitude().doubleValue() : null,
                                "Unknown Address"
                        );
                        sensorDataMap.put("temperature", String.valueOf(t.getTemperature()));
                        sensorDataMap.put("humidity", String.valueOf(t.getHumidity()));
                        sensorDataMap.put("vibration", String.valueOf(t.getVibration()));
                    }
                }
            }

            AlertSeverity severity = mapSeverity(alert.getAlertType(), alert.getAlertStatus());
            boolean resolved = "CLOSED".equals(alert.getAlertStatus());
            Instant timestamp = Instant.now();
            Instant startDate = null, acknowledgedDate = null, endDate = null;

            if (alert.getIncidents() != null && !alert.getIncidents().isEmpty()) {
                IncidentDto first = alert.getIncidents().get(0);
                if (first.getCreatedAt() != null)
                    startDate = first.getCreatedAt().atZone(ZoneOffset.UTC).toInstant();
                if (first.getAcknowledgeAt() != null)
                    acknowledgedDate = first.getAcknowledgeAt().atZone(ZoneOffset.UTC).toInstant();
                if (first.getClosedAt() != null)
                    endDate = first.getClosedAt().atZone(ZoneOffset.UTC).toInstant();
                if (startDate != null) timestamp = startDate;
            }

            AlertAnalytic analytic = new AlertAnalytic(
                    alert.getId(), tripId, deviceId, vehiclePlate,
                    alert.getAlertType(), severity, timestamp,
                    location, resolved, alert.getDeliveryOrderId(),
                    startDate, acknowledgedDate, endDate
            );

            if (!sensorDataMap.isEmpty()) {
                analytic.setSensorData(sensorDataMap, timestamp);
            }

            return Optional.of(analytic);
        } catch (Exception e) {
            log.error("Error building AlertAnalytic for alert {}", alert.getId(), e);
            return Optional.empty();
        }
    }

    private AlertSeverity mapSeverity(String alertType, String alertStatus) {
        if ("OPEN".equals(alertStatus)) {
            return ("TEMPERATURE".equals(alertType) || "HUMIDITY".equals(alertType))
                    ? AlertSeverity.CRITICAL : AlertSeverity.HIGH;
        }
        if ("ACKNOWLEDGED".equals(alertStatus)) return AlertSeverity.MEDIUM;
        if ("CLOSED".equals(alertStatus)) return AlertSeverity.LOW;
        return AlertSeverity.MEDIUM;
    }
}
