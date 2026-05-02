package com.cargasafe.analytics.infrastructure.external;

import com.cargasafe.analytics.infrastructure.external.dto.alert.AlertDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ExternalAlertService {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ExternalAlertService(RestTemplate restTemplate,
                                @Value("${integrations.alert-service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public List<AlertDto> getAllAlerts() {
        try {
            var response = restTemplate.exchange(
                    baseUrl + "/api/v1/alerts",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<AlertDto>>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching alerts from alert-service", e);
            return Collections.emptyList();
        }
    }
}
