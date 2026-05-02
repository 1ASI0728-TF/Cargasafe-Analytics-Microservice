package com.cargasafe.analytics.infrastructure.external;

import com.cargasafe.analytics.infrastructure.external.dto.fleet.VehicleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
public class ExternalFleetService {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ExternalFleetService(RestTemplate restTemplate,
                                @Value("${integrations.fleet-service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public Optional<String> getVehiclePlate(Long vehicleId) {
        if (vehicleId == null) return Optional.empty();
        try {
            var vehicle = restTemplate.getForObject(
                    baseUrl + "/api/v1/fleet/vehicles/" + vehicleId, VehicleDto.class);
            return vehicle != null ? Optional.ofNullable(vehicle.getPlate()) : Optional.empty();
        } catch (Exception e) {
            log.warn("Vehicle {} not found in fleet-service: {}", vehicleId, e.getMessage());
            return Optional.empty();
        }
    }
}
