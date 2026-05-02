package com.cargasafe.analytics.infrastructure.external;

import com.cargasafe.analytics.infrastructure.external.dto.profile.ProfileDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
public class ExternalProfileService {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ExternalProfileService(RestTemplate restTemplate,
                                  @Value("${integrations.profile-service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public Optional<String> getDriverName(Long userId) {
        if (userId == null) return Optional.empty();
        try {
            var profile = restTemplate.getForObject(
                    baseUrl + "/api/v1/profiles/user/" + userId, ProfileDto.class);
            if (profile == null) return Optional.empty();
            return Optional.of(profile.getFirstName() + " " + profile.getLastName());
        } catch (Exception e) {
            log.warn("Profile for user {} not found in profile-service: {}", userId, e.getMessage());
            return Optional.empty();
        }
    }
}
