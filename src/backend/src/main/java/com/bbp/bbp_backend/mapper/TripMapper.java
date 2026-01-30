package com.bbp.bbp_backend.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface TripMapper {

    @Insert(
            "INSERT INTO trips " +
                    "(date, durationSec, distanceKm, paceMinPerKm, " +
                    " startLat, startLon, endLat, endLon, " +
                    " startPlaceShort, startPlaceFull, " +
                    " endPlaceShort, endPlaceFull, " +
                    " track, createdBy, conditionRating, safetyRating, notes, isPublic) " +
                    "VALUES " +
                    "(#{date}, #{durationSec}, #{distanceKm}, #{paceMinPerKm}, " +
                    " #{startLat}, #{startLon}, #{endLat}, #{endLon}, " +
                    " #{startPlaceShort}, #{startPlaceFull}, " +
                    " #{endPlaceShort}, #{endPlaceFull}, " +
                    " #{track}, #{createdBy}, #{conditionRating}, #{safetyRating}, #{notes}, #{isPublic})"
    )
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertTrip(Map<String, Object> trip);

    @Select(
            "SELECT id, date, durationSec, distanceKm, paceMinPerKm, " +
                    "       startPlaceShort, startPlaceFull, " +
                    "       endPlaceShort, endPlaceFull " +
                    "FROM trips " +
                    "WHERE createdBy = #{userId} " +
                    "ORDER BY date DESC, id DESC"
    )
    List<Map<String, Object>> findTripsByUser(@Param("userId") long userId);

    @Select("SELECT * FROM trips WHERE id = #{id}")
    Map<String, Object> findTripById(@Param("id") long id);

}
