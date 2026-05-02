package com.cargasafe.analytics.infrastructure.external;

import com.cargasafe.analytics.infrastructure.external.dto.monitoring.MonitoringSessionDto;
import com.cargasafe.analytics.infrastructure.external.dto.monitoring.TelemetryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ExternalMonitoringService {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ExternalMonitoringService(RestTemplate restTemplate,
                                     @Value("${integrations.monitoring-service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public Optional<MonitoringSessionDto> getSessionByTripId(Long tripId) {
        try {
            var session = restTemplate.getForObject(
                    baseUrl + "/api/v1/monitoring/sessions/trip/" + tripId,
                    MonitoringSessionDto.class);
            return Optional.ofNullable(session);
        } catch (Exception e) {
            log.warn("Monitoring session for trip {} not found: {}", tripId, e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<TelemetryDto> getLatestTelemetry(Long sessionId) {
        try {
            var response = restTemplate.exchange(
                    baseUrl + "/api/v1/telemetry/session/" + sessionId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<TelemetryDto>>() {}
            );
            List<TelemetryDto> list = response.getBody();
            if (list == null || list.isEmpty()) return Optional.empty();
            return Optional.of(list.get(0));
        } catch (Exception e) {
            log.warn("Telemetry for session {} not found: {}", sessionId, e.getMessage());
            return Optional.empty();
        }
    }
}
