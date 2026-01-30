package com.bbp.bbp_backend.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface TripMapper {

    @Insert(
            "INSERT INTO trips " +
                    "(date, duration_sec, distance_km, pace_min_per_km, " +
                    " start_lat, start_lon, end_lat, end_lon, " +
                    " start_place_short, start_place_full, " +
                    " end_place_short, end_place_full, " +
                    " track, created_by, condition_rating, safety_rating, notes, is_public) " +
                    "VALUES " +
                    "(CAST(#{date} AS date), #{durationSec}, #{distanceKm}, #{paceMinPerKm}, " +
                    " #{startLat}, #{startLon}, #{endLat}, #{endLon}, " +
                    " #{startPlaceShort}, #{startPlaceFull}, " +
                    " #{endPlaceShort}, #{endPlaceFull}, " +
                    " #{track}, #{createdBy}, #{conditionRating}, #{safetyRating}, #{notes}, " +
                    " CASE WHEN #{isPublic} = 1 THEN TRUE ELSE FALSE END)"
    )
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertTrip(Map<String, Object> trip);

    @Select(
            "SELECT id, date, " +
                    "       duration_sec AS \"durationSec\", " +
                    "       distance_km AS \"distanceKm\", " +
                    "       pace_min_per_km AS \"paceMinPerKm\", " +
                    "       start_place_short AS \"startPlaceShort\", " +
                    "       start_place_full AS \"startPlaceFull\", " +
                    "       end_place_short AS \"endPlaceShort\", " +
                    "       end_place_full AS \"endPlaceFull\" " +
                    "FROM trips " +
                    "WHERE created_by = #{userId} " +
                    "ORDER BY date DESC, id DESC"
    )
    List<Map<String, Object>> findTripsByUser(@Param("userId") long userId);

    @Select(
            "SELECT id, date, " +
                    "       duration_sec AS \"durationSec\", " +
                    "       distance_km AS \"distanceKm\", " +
                    "       pace_min_per_km AS \"paceMinPerKm\", " +
                    "       start_lat AS \"startLat\", " +
                    "       start_lon AS \"startLon\", " +
                    "       end_lat AS \"endLat\", " +
                    "       end_lon AS \"endLon\", " +
                    "       start_place_short AS \"startPlaceShort\", " +
                    "       start_place_full AS \"startPlaceFull\", " +
                    "       end_place_short AS \"endPlaceShort\", " +
                    "       end_place_full AS \"endPlaceFull\", " +
                    "       track, " +
                    "       created_by AS \"createdBy\", " +
                    "       condition_rating AS \"conditionRating\", " +
                    "       safety_rating AS \"safetyRating\", " +
                    "       notes, " +
                    "       is_public AS \"isPublic\" " +
                    "FROM trips " +
                    "WHERE id = #{id}"
    )
    Map<String, Object> findTripById(@Param("id") long id);

    @Update(
            "UPDATE trips SET " +
                    "  condition_rating = #{conditionRating}, " +
                    "  safety_rating = #{safetyRating}, " +
                    "  notes = #{notes}, " +
                    "  is_public = CASE WHEN #{isPublic} = 1 THEN TRUE ELSE FALSE END " +
                    "WHERE id = #{id}"
    )
    int updateTripReport(@Param("id") long id,
                         @Param("conditionRating") int conditionRating,
                         @Param("safetyRating") int safetyRating,
                         @Param("notes") String notes,
                         @Param("isPublic") int isPublic);

    @Select(
            "SELECT id, date, " +
                    "       start_place_short AS \"startPlaceShort\", " +
                    "       end_place_short AS \"endPlaceShort\", " +
                    "       condition_rating AS \"conditionRating\", " +
                    "       safety_rating AS \"safetyRating\", " +
                    "       notes, " +
                    "       is_public AS \"isPublic\", " +
                    "       distance_km AS \"distanceKm\", " +
                    "       duration_sec AS \"durationSec\" " +
                    "FROM trips " +
                    "WHERE created_by = #{userId} AND is_public = TRUE " +
                    "ORDER BY date DESC, id DESC"
    )
    List<Map<String, Object>> findReportsByUser(@Param("userId") long userId);

    @Select(
            "SELECT id, date, " +
                    "       start_place_short AS \"startPlaceShort\", " +
                    "       end_place_short AS \"endPlaceShort\", " +
                    "       condition_rating AS \"conditionRating\", " +
                    "       safety_rating AS \"safetyRating\", " +
                    "       notes, " +
                    "       is_public AS \"isPublic\", " +
                    "       distance_km AS \"distanceKm\", " +
                    "       duration_sec AS \"durationSec\", " +
                    "       track, " +
                    "       created_by AS \"createdBy\" " +
                    "FROM trips " +
                    "WHERE is_public = TRUE " +
                    "ORDER BY date DESC, id DESC"
    )
    List<Map<String, Object>> findPublicTrips();

}
