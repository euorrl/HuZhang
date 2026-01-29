package com.bbp.bbp_backend.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GpsSimulationService {

    // 6 routes = 3 (pair1) + 2 (pair2) + 1 (pair3)
    private final List<List<double[]>> routes = new ArrayList<>();

    private boolean started = false;
    private boolean holding = false;

    private int routeCursor = 0; // 0..5, switch on STOP
    private int pointCursor = 0;

    public GpsSimulationService() {
        // -------------------------
        // Pair 1: 3 routes
        // start fixed: (45.4781, 9.2271)
        // -------------------------
        routes.add(Arrays.asList(
                new double[]{45.4781, 9.2271},
                new double[]{45.478740, 9.226692},
                new double[]{45.479224, 9.226605},
                new double[]{45.479230, 9.2277},
                new double[]{45.479236, 9.2288},
                new double[]{45.479241, 9.230009},
                new double[]{45.4801, 9.230009},
                new double[]{45.480904, 9.230009},
                new double[]{45.4816, 9.230009},
                new double[]{45.482344, 9.230009}
        ));

        routes.add(Arrays.asList(
                new double[]{45.4781, 9.2271},
                new double[]{45.478740, 9.226692},
                new double[]{45.479224, 9.226605},
                new double[]{45.479226, 9.225701},
                new double[]{45.479642, 9.224994},
                new double[]{45.479989, 9.224321},
                new double[]{45.480478, 9.224374},
                new double[]{45.480946, 9.225773},
                new double[]{45.481389, 9.226999},
                new double[]{45.481879, 9.228425},
                new double[]{45.482344, 9.230009}
        ));

        routes.add(Arrays.asList(
                new double[]{45.4781, 9.2271},
                new double[]{45.478740, 9.226692},
                new double[]{45.479224, 9.226605},
                new double[]{45.479230, 9.2277},
                new double[]{45.479236, 9.2288},
                new double[]{45.479241, 9.230009},
                new double[]{45.479269, 9.231645},
                new double[]{45.479835, 9.233127},
                new double[]{45.480339, 9.234350},
                new double[]{45.481535, 9.234291},
                new double[]{45.482656, 9.234312},
                new double[]{45.483917, 9.234280},
                new double[]{45.483411, 9.232773},
                new double[]{45.482889, 9.231286},
                new double[]{45.482344, 9.230009}
        ));

        // -------------------------
        // Pair 2: 2 routes (example points; you can change)
        // -------------------------
        routes.add(Arrays.asList(
                new double[]{45.476887, 9.217441},
                new double[]{45.477511, 9.218640},
                new double[]{45.478394, 9.220275},
                new double[]{45.478862, 9.221162},
                new double[]{45.479244, 9.221282},
                new double[]{45.479813, 9.221261},
                new double[]{45.480299, 9.221265}

        ));

        routes.add(Arrays.asList(
                new double[]{45.476887, 9.217441},
                new double[]{45.477725, 9.217268},
                new double[]{45.478495, 9.217259},
                new double[]{45.479119, 9.217265},
                new double[]{45.479119, 9.218475},
                new double[]{45.479119, 9.219414},
                new double[]{45.479701, 9.219419},
                new double[]{45.480285, 9.219416},
                new double[]{45.480289, 9.220447},
                new double[]{45.480299, 9.221265}
        ));

        // -------------------------
        // Pair 3: 1 route
        // -------------------------
        routes.add(Arrays.asList(
                new double[]{45.476466, 9.230025},
                new double[]{45.476916, 9.230046},
                new double[]{45.476907, 9.229544},
                new double[]{45.476900, 9.228975},
                new double[]{45.476897, 9.228152},
                new double[]{45.476893, 9.227407},
                new double[]{45.476888, 9.226788},
                new double[]{45.476895, 9.225888},
                new double[]{45.476588, 9.225156},
                new double[]{45.476359, 9.224588},
                new double[]{45.476125, 9.223973}
        ));
    }

    public synchronized Map<String, Object> start() {
        started = true;
        holding = false;
        pointCursor = 0;
        return status(null);
    }

    public synchronized Map<String, Object> stop() {
        boolean wasStarted = started;

        started = false;
        holding = false;
        pointCursor = 0;

        // ⭐ switch to next route after STOP
        if (!routes.isEmpty()) {
            routeCursor = (routeCursor + 1) % routes.size();
        }

        Map<String, Object> m = new HashMap<>();
        m.put("stopped", true);
        m.put("wasStarted", wasStarted);
        m.put("nextRouteIndex", routeCursor);
        m.put("timestamp", System.currentTimeMillis());
        return m;
    }

    public synchronized Map<String, Object> next() {
        if (!started) return status("not_started");

        if (routes.isEmpty()) return status("no_routes");

        List<double[]> route = routes.get(routeCursor);
        if (route == null || route.isEmpty()) {
            holding = true;
            return status("empty_route");
        }

        // holding: always return the end point
        if (holding) {
            double[] end = route.get(route.size() - 1);
            return point(end[0], end[1], route.size() - 1, true, true);
        }

        double[] p = route.get(pointCursor);

        // reach end => holding
        if (pointCursor >= route.size() - 1) {
            holding = true;
            return point(p[0], p[1], pointCursor, true, true);
        }

        // normal step
        Map<String, Object> res = point(p[0], p[1], pointCursor, false, false);
        pointCursor++;
        return res;
    }

    private Map<String, Object> point(double lat, double lon, int pointIndex, boolean finished, boolean holdingNow) {
        Map<String, Object> m = new HashMap<>();
        m.put("lat", lat);
        m.put("lon", lon);
        m.put("timestamp", System.currentTimeMillis());

        // helpful for debug + future UI
        m.put("routeIndex", routeCursor);
        m.put("pointIndex", pointIndex);

        m.put("finished", finished);
        m.put("holding", holdingNow);
        return m;
    }

    private Map<String, Object> status(String reason) {
        Map<String, Object> m = new HashMap<>();
        m.put("started", started);
        m.put("holding", holding);
        m.put("routeIndex", routeCursor);
        m.put("pointIndex", pointCursor);
        m.put("timestamp", System.currentTimeMillis());
        if (reason != null) m.put("reason", reason);
        return m;
    }
}
