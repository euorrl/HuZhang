package com.bbp.bbp_backend.controller;

import com.bbp.bbp_backend.mapper.TripMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trips")
@CrossOrigin
public class TripController {

    private final TripMapper tripMapper;

    public TripController(TripMapper tripMapper) {
        this.tripMapper = tripMapper;
    }

    @PostMapping
    public Map<String, Object> createTrip(@RequestBody Map<String, Object> trip) {
        tripMapper.insertTrip(trip);
        return java.util.Collections.singletonMap("id", trip.get("id"));
    }

    @GetMapping
    public List<Map<String, Object>> getTrips(@RequestParam long userId) {
        return tripMapper.findTripsByUser(userId);
    }

    @GetMapping("/{id}")
    public Map<String, Object> getTrip(@PathVariable long id) {
        return tripMapper.findTripById(id);
    }

    @PutMapping("/{id}/report")
    public Map<String, Object> updateTripReport(@PathVariable long id,
                                                @RequestBody Map<String, Object> body) {

        int conditionRating = ((Number) body.getOrDefault("conditionRating", 0)).intValue();
        int safetyRating = ((Number) body.getOrDefault("safetyRating", 0)).intValue();
        String notes = (String) body.getOrDefault("notes", "");
        int isPublic = ((Number) body.getOrDefault("isPublic", 0)).intValue();

        int updated = tripMapper.updateTripReport(id, conditionRating, safetyRating, notes, isPublic);
        if (updated == 0) {
            throw new RuntimeException("Trip not found: " + id);
        }

        // 返回更新后的 trip，前端更方便
        return tripMapper.findTripById(id);
    }

    @GetMapping("/reports")
    public List<Map<String, Object>> getReports(@RequestParam long userId) {
        return tripMapper.findReportsByUser(userId);
    }

    @GetMapping("/public")
    public List<Map<String, Object>> getPublicTrips() {
        return tripMapper.findPublicTrips();
    }



}
