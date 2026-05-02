package com.cargasafe.analytics.infrastructure.external;

import com.cargasafe.analytics.infrastructure.external.dto.trip.TripDto;
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
public class ExternalTripService {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ExternalTripService(RestTemplate restTemplate,
                               @Value("${integrations.trip-service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public List<TripDto> getAllTrips() {
        try {
            var response = restTemplate.exchange(
                    baseUrl + "/api/v1/trips",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<TripDto>>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching trips from trip-service", e);
            return Collections.emptyList();
        }
    }

    public Optional<TripDto> getTripById(Long tripId) {
        try {
            var trip = restTemplate.getForObject(baseUrl + "/api/v1/trips/" + tripId, TripDto.class);
            return Optional.ofNullable(trip);
        } catch (Exception e) {
            log.warn("Trip {} not found in trip-service: {}", tripId, e.getMessage());
            return Optional.empty();
        }
    }
}
