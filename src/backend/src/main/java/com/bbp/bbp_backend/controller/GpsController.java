package com.bbp.bbp_backend.controller;

import com.bbp.bbp_backend.service.GpsSimulationService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/gps")
public class GpsController {

    private final GpsSimulationService service;

    public GpsController(GpsSimulationService service) {
        this.service = service;
    }

    @GetMapping("/start")
    public Map<String, Object> start() {
        return service.start();
    }

    @GetMapping("/next")
    public Map<String, Object> next() {
        return service.next();
    }

    @GetMapping("/stop")
    public Map<String, Object> stop() {
        return service.stop();
    }
}
