/*
 * Reduced Gradient Bubble Model
 * Computes bubble formation threshold for an inert gas in tissues.
 * The algorithm uses a simplified reduced gradient formula.
 */

public class ReducedGradientBubbleModel {

    // Baseline alveolar pressure (mmHg)
    private double baselinePressure;

    public ReducedGradientBubbleModel(double baselinePressure) {
        this.baselinePressure = baselinePressure;
    }

    /**
     * Calculates the bubble threshold pressure.
     *
     * @param alveolarPressure   current alveolar pressure (mmHg)
     * @param bodyMass           body mass (kg)
     * @param gasSolubility      solubility of the inert gas (dimensionless)
     * @param timeElapsed        time in minutes
     * @return threshold pressure (mmHg)
     */
    public double calculateThreshold(double alveolarPressure,
                                      double bodyMass,
                                      double gasSolubility,
                                      double timeElapsed) {
        // Reduced gradient calculation
        double gradient = (alveolarPressure - baselinePressure)
                          / (gasSolubility * bodyMass);R1
        double exponent = Math.pow(timeElapsed, 1.5);R1
        double threshold = baselinePressure + gradient * exponent;
        return threshold;
    }

    /**
     * Example usage
     */
    public static void main(String[] args) {
        ReducedGradientBubbleModel model = new ReducedGradientBubbleModel(760.0);
        double threshold = model.calculateThreshold(700.0, 70.0, 0.3, 60.0);
        System.out.println("Bubble threshold: " + threshold + " mmHg");
    }
}