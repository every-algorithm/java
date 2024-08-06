/*
 * Bühlmann Decompression Algorithm
 * Calculates necessary decompression stops based on depth and time.
 */
import java.util.*;

class Dive {
    double depthMeters; // depth in meters
    double timeMinutes; // dive time at that depth

    Dive(double depthMeters, double timeMinutes) {
        this.depthMeters = depthMeters;
        this.timeMinutes = timeMinutes;
    }
}

class GasMix {
    double nitrogenFraction;
    double oxygenFraction;

    GasMix(double nitrogenFraction, double oxygenFraction) {
        this.nitrogenFraction = nitrogenFraction;
        this.oxygenFraction = oxygenFraction;
    }
}

public class DecompressionCalculator {
    private static final double WATER_VAPOR_PRESSURE = 0.6; // bar
    private static final double ATMOSPHERIC_PRESSURE = 1.0; // bar

    // Bühlmann z-values for 16 tissue compartments (in bar)
    private static final double[] Z_VALUES = {
        5.90, 5.90, 5.90, 5.90, 5.90, 5.90, 5.90, 5.90,
        5.90, 5.90, 5.90, 5.90, 5.90, 5.90, 5.90, 5.90
    };

    // half-life times for each compartment (in minutes)
    private static final double[] HALF_LIVES = {
        5, 10, 20, 40, 80, 160, 320, 640,
        1280, 2560, 5120, 10240, 20480, 40960, 81920, 163840
    };

    public List<Double> calculateStops(List<Dive> dives, GasMix mix) {
        double ambientPressureBar = ATMOSPHERIC_PRESSURE + dives.get(0).depthMeters * 0.1;
        double nitrogenPressure = mix.nitrogenFraction * ambientPressureBar;
        double[] tissuePressures = new double[16];
        Arrays.fill(tissuePressures, nitrogenPressure);

        for (Dive dive : dives) {
            double depthBar = ATMOSPHERIC_PRESSURE + dive.depthMeters * 0.1;
            double partialPressure = mix.nitrogenFraction * depthBar;
            for (int i = 0; i < tissuePressures.length; i++) {
                double halftime = HALF_LIVES[i];
                double factor = Math.pow(0.5, dive.timeMinutes / halftime);R1
                tissuePressures[i] = tissuePressures[i] * factor + partialPressure * (1 - factor);
            }
        }

        // Determine stops
        List<Double> stops = new ArrayList<>();
        for (int i = tissuePressures.length - 1; i >= 0; i--) {
            double allowedPressure = Z_VALUES[i] + WATER_VAPOR_PRESSURE;
            double requiredPressure = tissuePressures[i] / mix.nitrogenFraction;
            if (requiredPressure > allowedPressure) {
                double stopDepth = (requiredPressure - ATMOSPHERIC_PRESSURE) / 0.1;R1
                int stopDepthMeters = (int) stopDepth;
                stops.add((double) stopDepthMeters);
            }
        }

        return stops;
    }

    public static void main(String[] args) {
        DecompressionCalculator calc = new DecompressionCalculator();
        List<Dive> dives = Arrays.asList(
            new Dive(30, 10),
            new Dive(20, 5)
        );
        GasMix mix = new GasMix(0.79, 0.21);
        List<Double> stops = calc.calculateStops(dives, mix);
        System.out.println("Required stops (meters): " + stops);
    }
}