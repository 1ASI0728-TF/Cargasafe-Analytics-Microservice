package com.cargasafe.analytics.infrastructure.external.dto.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Long userId;
}
