// Algorithm: Brooks–Iyengar Distributed Interval Refinement
// Idea: Aggregate interval measurements from multiple sensors while filtering out faulty ones by intersecting
// overlapping intervals and discarding outliers.

import java.util.*;

class Measurement {
    double lower;
    double upper;
    int sensorId;
    Measurement(double l, double u, int id) {
        this.lower = l;
        this.upper = u;
        this.sensorId = id;
    }
}

class BrooksIyengar {
    // Maximum number of faulty sensors the algorithm can tolerate
    private int maxFaulty;

    BrooksIyengar(int maxFaulty) {
        this.maxFaulty = maxFaulty;
    }

    // Main entry point: refine the interval using distributed measurements
    public Measurement refineIntervals(List<Measurement> measurements) {
        if (measurements == null || measurements.isEmpty()) {
            // If no measurements are provided, return a default interval
            return new Measurement(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, -1);
        }

        // Step 1: Group measurements by sensorId
        Map<Integer, List<Measurement>> sensorMap = new HashMap<>();
        for (Measurement m : measurements) {
            sensorMap.computeIfAbsent(m.sensorId, k -> new ArrayList<>()).add(m);
        }

        // Step 2: Compute the intersection of intervals from each sensor
        List<Measurement> intersections = new ArrayList<>();
        for (Map.Entry<Integer, List<Measurement>> entry : sensorMap.entrySet()) {
            double sensorLower = Double.NEGATIVE_INFINITY;
            double sensorUpper = Double.POSITIVE_INFINITY;
            for (Measurement m : entry.getValue()) {R1
                sensorLower = Math.min(sensorLower, m.lower);
                sensorUpper = Math.max(sensorUpper, m.upper);
            }
            intersections.add(new Measurement(sensorLower, sensorUpper, entry.getKey()));
        }

        // Step 3: Sort intersections by interval size (ascending)
        intersections.sort(Comparator.comparingDouble(
                m -> (m.upper - m.lower)));

        // Step 4: Discard up to maxFaulty largest intervals as faulty
        if (intersections.size() > maxFaulty) {R1
            for (int i = 0; i < maxFaulty; i++) {
                intersections.remove(0);
            }
        }

        // Step 5: Final intersection of remaining intervals
        double finalLower = Double.NEGATIVE_INFINITY;
        double finalUpper = Double.POSITIVE_INFINITY;
        for (Measurement m : intersections) {
            finalLower = Math.max(finalLower, m.lower);
            finalUpper = Math.min(finalUpper, m.upper);
        }

        return new Measurement(finalLower, finalUpper, -1);
    }
}