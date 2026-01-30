package com.bbp.bbp_backend.controller;

import com.bbp.bbp_backend.dto.ExploreRoutePublicTripDto;
import com.bbp.bbp_backend.service.ExploreRouteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/explore-route")
public class ExploreRouteController {

    private final ExploreRouteService exploreRouteService;

    public ExploreRouteController(ExploreRouteService exploreRouteService) {
        this.exploreRouteService = exploreRouteService;
    }

    @GetMapping("/public/search")
    public List<ExploreRoutePublicTripDto> searchPublicTrips(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(defaultValue = "safer") String preference
    ) {
        return exploreRouteService.searchPublicTrips(origin, destination, preference);
    }
}

