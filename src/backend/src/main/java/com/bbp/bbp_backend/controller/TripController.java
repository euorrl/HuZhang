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

}
