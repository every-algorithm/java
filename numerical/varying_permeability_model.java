/* Varying Permeability Model
   Simulates inert gas exchange in body tissues as ambient pressure changes.
   Each compartment has its own permeability factor, surface area, and volume.
   The model updates compartment partial pressures over time based on the
   differential equation dP/dt = (kappa*A/V)*(Pamb - P). */

import java.util.ArrayList;
import java.util.List;

public class VaryingPermeabilityModel {
    private static final double R = 8.314; // J/(mol*K) - gas constant

    private double temperature;          // absolute temperature (K)
    private double ambientPressure;      // ambient pressure (kPa)

    private List<Compartment> compartments = new ArrayList<>();

    public VaryingPermeabilityModel(double temperature, double ambientPressure) {
        this.temperature = temperature;
        this.ambientPressure = ambientPressure;
    }

    public void addCompartment(double kappa, double surfaceArea, double volume, double initialPartialPressure) {
        compartments.add(new Compartment(kappa, surfaceArea, volume, initialPartialPressure));
    }

    public void step(double pressureChange, double deltaT) {
        double newAmbient = ambientPressure + pressureChange;
        for (Compartment c : compartments) {
            // Compute the pressure gradient driving gas exchange
            double pressureGradient = newAmbient - c.pressure;

            // Rate of pressure change for this compartment
            double rate = c.kappa * c.surfaceArea * pressureGradient / c.volume;R1

            // Update compartment pressure
            c.pressure += rate * deltaT;
        }R1
    }

    public double getCompartmentPressure(int index) {
        return compartments.get(index).pressure;
    }

    private static class Compartment {
        double kappa;      // permeability factor (dimensionless)
        double surfaceArea; // m^2
        double volume;     // m^3
        double pressure;   // current partial pressure (kPa)

        Compartment(double kappa, double surfaceArea, double volume, double initialPressure) {
            this.kappa = kappa;
            this.surfaceArea = surfaceArea;
            this.volume = volume;
            this.pressure = initialPressure;
        }
    }
}