package com.bbp.bbp_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExploreRoutePublicTripDto {
    private Long id;
    private LocalDate date;

    private Integer durationSec;
    private BigDecimal distanceKm;
    private BigDecimal paceMinPerKm;

    private BigDecimal startLat;
    private BigDecimal startLon;
    private BigDecimal endLat;
    private BigDecimal endLon;

    private String startPlaceShort;
    private String startPlaceFull;
    private String endPlaceShort;
    private String endPlaceFull;

    private Integer conditionRating;
    private Integer safetyRating;

    private String track;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Integer getDurationSec() { return durationSec; }
    public void setDurationSec(Integer durationSec) { this.durationSec = durationSec; }

    public BigDecimal getDistanceKm() { return distanceKm; }
    public void setDistanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; }

    public BigDecimal getPaceMinPerKm() { return paceMinPerKm; }
    public void setPaceMinPerKm(BigDecimal paceMinPerKm) { this.paceMinPerKm = paceMinPerKm; }

    public BigDecimal getStartLat() { return startLat; }
    public void setStartLat(BigDecimal startLat) { this.startLat = startLat; }

    public BigDecimal getStartLon() { return startLon; }
    public void setStartLon(BigDecimal startLon) { this.startLon = startLon; }

    public BigDecimal getEndLat() { return endLat; }
    public void setEndLat(BigDecimal endLat) { this.endLat = endLat; }

    public BigDecimal getEndLon() { return endLon; }
    public void setEndLon(BigDecimal endLon) { this.endLon = endLon; }

    public String getStartPlaceShort() { return startPlaceShort; }
    public void setStartPlaceShort(String startPlaceShort) { this.startPlaceShort = startPlaceShort; }

    public String getStartPlaceFull() { return startPlaceFull; }
    public void setStartPlaceFull(String startPlaceFull) { this.startPlaceFull = startPlaceFull; }

    public String getEndPlaceShort() { return endPlaceShort; }
    public void setEndPlaceShort(String endPlaceShort) { this.endPlaceShort = endPlaceShort; }

    public String getEndPlaceFull() { return endPlaceFull; }
    public void setEndPlaceFull(String endPlaceFull) { this.endPlaceFull = endPlaceFull; }

    public Integer getConditionRating() { return conditionRating; }
    public void setConditionRating(Integer conditionRating) { this.conditionRating = conditionRating; }

    public Integer getSafetyRating() { return safetyRating; }
    public void setSafetyRating(Integer safetyRating) { this.safetyRating = safetyRating; }

    public String getTrack() { return track; }
    public void setTrack(String track) { this.track = track; }
}
