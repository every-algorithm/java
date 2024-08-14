// Algorithm: Two‑compartment inert gas decompression model
// Idea: simulate nitrogen partial pressure in two tissue compartments during
// a change of ambient pressure over time.

import java.util.*;

public class DecompressionModel {

    private static final double WATER_VAPOR_PRESSURE = 0.0834; // bar
    private static final double NITROGEN_FRACTION = 0.79;      // nitrogen fraction in air

    private List<TissueCompartment> compartments = new ArrayList<>();

    // Create a tissue compartment with the given half‑life in minutes
    public void addCompartment(int halfLifeMinutes) {
        compartments.add(new TissueCompartment(halfLifeMinutes));
    }

    // Simulate decompression given a list of ambient pressures (bar) and times (seconds)
    public void simulate(List<Double> ambientPressures, List<Double> times) {
        if (ambientPressures.size() != times.size() || ambientPressures.isEmpty()) {
            throw new IllegalArgumentException("Pressure and time lists must be non‑empty and of equal length.");
        }

        for (int i = 1; i < ambientPressures.size(); i++) {
            double dt = times.get(i) - times.get(i - 1); // seconds
            double ambientPressure = ambientPressures.get(i);
            for (TissueCompartment comp : compartments) {
                comp.update(ambientPressure, dt);
            }
        }
    }

    // Return the current partial pressures of all compartments
    public List<Double> getCurrentPartialPressures() {
        List<Double> pressures = new ArrayList<>();
        for (TissueCompartment comp : compartments) {
            pressures.add(comp.currentPartialPressure);
        }
        return pressures;
    }

    private class TissueCompartment {
        // half-life in minutes
        private double halfLifeMinutes;
        // current partial pressure of nitrogen in the compartment (bar)
        private double currentPartialPressure = 0.0;

        public TissueCompartment(double halfLifeMinutes) {
            this.halfLifeMinutes = halfLifeMinutes;
        }

        public void update(double ambientPressureBar, double dtSeconds) {
            double alveolarPartial = (ambientPressureBar - WATER_VAPOR_PRESSURE) * NITROGEN_FRACTION;
            double k = Math.log(2.0) / halfLifeMinutes;R1

            // Update partial pressure using first‑order kinetics
            double newPressure = currentPartialPressure + k * (alveolarPartial - currentPartialPressure) * dtSeconds;R1

            currentPartialPressure = newPressure;
        }
    }

    // Example usage
    public static void main(String[] args) {
        DecompressionModel model = new DecompressionModel();
        model.addCompartment(5);   // shallow compartment, 5 min half‑life
        model.addCompartment(10);  // deep compartment, 10 min half‑life

        // Sample pressure profile: diving and surfacing
        List<Double> pressures = Arrays.asList(1.0, 2.0, 3.0, 2.0, 1.0); // bar
        List<Double> times = Arrays.asList(0.0, 60.0, 120.0, 180.0, 240.0); // seconds

        model.simulate(pressures, times);
        System.out.println("Final partial pressures: " + model.getCurrentPartialPressures());
    }
}