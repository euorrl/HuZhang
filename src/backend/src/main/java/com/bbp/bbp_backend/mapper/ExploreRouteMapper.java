package com.bbp.bbp_backend.mapper;


import com.bbp.bbp_backend.dto.ExploreRoutePublicTripDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExploreRouteMapper {

    @Select({
            "SELECT",
            "  id,",
            "  date,",
            "  duration_sec     AS durationSec,",
            "  distance_km      AS distanceKm,",
            "  pace_min_per_km  AS paceMinPerKm,",
            "  start_lat        AS startLat,",
            "  start_lon        AS startLon,",
            "  end_lat          AS endLat,",
            "  end_lon          AS endLon,",
            "  start_place_short AS startPlaceShort,",
            "  start_place_full  AS startPlaceFull,",
            "  end_place_short   AS endPlaceShort,",
            "  end_place_full    AS endPlaceFull,",
            "  condition_rating  AS conditionRating,",
            "  safety_rating     AS safetyRating,",
            "  track",
            "FROM trips",
            "WHERE is_public = TRUE",
            "  AND (#{origin} IS NULL OR trim(#{origin}) = ''",
            "       OR start_place_short ILIKE '%' || #{origin} || '%'",
            "       OR start_place_full  ILIKE '%' || #{origin} || '%')",
            "  AND (#{destination} IS NULL OR trim(#{destination}) = ''",
            "       OR end_place_short ILIKE '%' || #{destination} || '%'",
            "       OR end_place_full  ILIKE '%' || #{destination} || '%')",
            "ORDER BY",
            "  CASE WHEN #{preference} = 'safer'   THEN safety_rating END DESC NULLS LAST,",
            "  CASE WHEN #{preference} = 'safer'   THEN distance_km   END ASC  NULLS LAST,",
            "  CASE WHEN #{preference} = 'shorter' THEN distance_km    END ASC  NULLS LAST,",
            "  CASE WHEN #{preference} = 'shorter' THEN safety_rating  END DESC NULLS LAST,",
            "  id DESC",
            "LIMIT 50"
    })
    List<ExploreRoutePublicTripDto> searchPublicTrips(
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("preference") String preference
    );
}
