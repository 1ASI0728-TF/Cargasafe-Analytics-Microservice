package com.cargasafe.analytics.domain.model.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class MonthlyIncidentSummary {

    private String month;
    private Integer year;
    private Integer temperatureIncidents;
    private Integer movementIncidents;
    private Integer totalIncidents;
    private List<IncidentDetail> incidents = new ArrayList<>();

    public MonthlyIncidentSummary(String month, Integer year, Integer temperatureIncidents,
                                  Integer movementIncidents, Integer totalIncidents) {
        this.month = month;
        this.year = year;
        this.temperatureIncidents = temperatureIncidents;
        this.movementIncidents = movementIncidents;
        this.totalIncidents = totalIncidents;
        this.incidents = new ArrayList<>();
    }

    public void addIncident(IncidentDetail incident) {
        this.incidents.add(incident);
    }
}
