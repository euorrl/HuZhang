package com.bbp.bbp_backend.service;


import com.bbp.bbp_backend.dto.ExploreRoutePublicTripDto;
import com.bbp.bbp_backend.mapper.ExploreRouteMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExploreRouteService {

    private final ExploreRouteMapper exploreRouteMapper;

    public ExploreRouteService(ExploreRouteMapper exploreRouteMapper) {
        this.exploreRouteMapper = exploreRouteMapper;
    }

    public List<ExploreRoutePublicTripDto> searchPublicTrips(String origin, String destination, String preference) {
        String p = normalizePreference(preference);
        return exploreRouteMapper.searchPublicTrips(origin, destination, p);
    }

    private String normalizePreference(String preference) {
        if ("shorter".equals(preference)) return "shorter";
        return "safer";
    }
}

