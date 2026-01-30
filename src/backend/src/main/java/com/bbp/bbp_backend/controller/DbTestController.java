package com.bbp.bbp_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@RestController
public class DbTestController {

    private final DataSource dataSource;

    public DbTestController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/api/db-test")
    public Map<String, Object> dbTest() {
        Map<String, Object> res = new LinkedHashMap<>();
        List<Map<String, Object>> rows = new ArrayList<>();

        String sql = "select id, name, route_order, created_at from public.gps_routes order by route_order limit 5";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", rs.getObject("id"));
                row.put("name", rs.getString("name"));
                row.put("route_order", rs.getInt("route_order"));
                row.put("created_at", rs.getObject("created_at"));
                rows.add(row);
            }

            res.put("ok", true);
            res.put("count", rows.size());
            res.put("data", rows);
        } catch (Exception e) {
            res.put("ok", false);
            res.put("error", e.getMessage());
        }
        return res;
    }
}
