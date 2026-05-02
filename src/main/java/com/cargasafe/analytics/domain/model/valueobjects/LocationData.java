package com.cargasafe.analytics.domain.model.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LocationData {
    private Double latitude;
    private Double longitude;
    private String address;
}
