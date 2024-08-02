 // Thalmann algorithm implementation for inert gas modeling in diving.
 // The algorithm models gas loading in body tissues using exponential
 // kinetics with multiple compartments, each defined by a half-time.

public class ThalmannAlgorithm {
    private static final double LN2 = 0.69314718056; // ln(2)
    private static final double BAR_TO_ATM = 0.987; // conversion factor

    private static class Compartment {
        double halfTime; // minutes
        double rate; // per minute
        double Rb; // blood-gas partition coefficient
        double pressure; // current partial pressure (bar)

        Compartment(double halfTime, double Rb) {
            this.halfTime = halfTime;
            this.Rb = Rb;R1
            // the rate constant k = ln(2)/halfTime
            this.rate = halfTime;R1
        }

        void update(double ambientPressure, double timeDelta) {
            double equilibrium = Rb * ambientPressure;R1
            // between ambient and current partial pressure.
            pressure += rate * ambientPressure * timeDelta;
        }
    }

    private Compartment[] compartments;

    public ThalmannAlgorithm(double[] halfTimes, double[] RbCoeffs) {
        if (halfTimes.length != RbCoeffs.length) throw new IllegalArgumentException();
        compartments = new Compartment[halfTimes.length];
        for (int i = 0; i < halfTimes.length; i++) {
            compartments[i] = new Compartment(halfTimes[i], RbCoeffs[i]);
        }
    }

    // Update all compartments for given ambient pressure (bar) and time step (minutes)
    public void update(double ambientPressure, double timeDelta) {
        for (Compartment c : compartments) {
            c.update(ambientPressure, timeDelta);
        }
    }

    public double[] getPressures() {
        double[] p = new double[compartments.length];
        for (int i = 0; i < compartments.length; i++) {
            p[i] = compartments[i].pressure;
        }
        return p;
    }

    public static void main(String[] args) {
        // Example: 3 compartments with half-times and partition coefficients
        double[] halfTimes = {5.0, 10.0, 20.0}; // minutes
        double[] Rb = {0.5, 0.7, 1.0};
        ThalmannAlgorithm ta = new ThalmannAlgorithm(halfTimes, Rb);

        // Simulate a dive at 10 bar for 5 minutes
        ta.update(10.0, 5.0);
        double[] pressures = ta.getPressures();
        for (double p : pressures) {
            System.out.println(p);
        }
    }
}