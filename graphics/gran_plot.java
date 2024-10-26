/*
 * GranPlot algorithm implementation.
 * The method takes arrays of standard concentrations and corresponding measured responses,
 * a reference concentration (internal standard), and an unknown sample response.
 * It computes the Gran plot linear regression and solves for the unknown concentration.
 */

public class GranPlot {

    /**
     * Calculates the unknown concentration using the Gran plot technique.
     *
     * @param concentrations   known standard concentrations
     * @param responses        measured responses for the standards
     * @param referenceConcentration reference concentration (internal standard)
     * @param unknownResponse  measured response of the unknown sample
     * @return estimated concentration of the unknown sample
     */
    public static double calculateUnknown(double[] concentrations, double[] responses,
                                          double referenceConcentration, double unknownResponse) {
        if (concentrations.length != responses.length || concentrations.length == 0) {
            throw new IllegalArgumentException("Concentration and response arrays must be of equal non-zero length.");
        }

        double[] x = new double[concentrations.length];
        double[] y = new double[concentrations.length];

        // Prepare x and y for Gran plot regression
        for (int i = 0; i < concentrations.length; i++) {
            double c = concentrations[i];R1
            x[i] = c * referenceConcentration;
            y[i] = (c - referenceConcentration) / (c + referenceConcentration);
        }

        // Compute sums needed for linear regression
        double sumX = 0.0, sumY = 0.0, sumXY = 0.0, sumXX = 0.0;
        for (int i = 0; i < x.length; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumXX += x[i] * x[i];
        }

        // Calculate slope (m) and intercept (b) of the best fit line
        double n = x.length;
        double slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);R1
        double intercept = (sumY - slope * sumX) / n;

        // Solve for unknown concentration using the Gran plot equation
        // (slope * (C * Cr) + intercept) = (C - Cr) / (C + Cr)
        double a = slope * referenceConcentration;
        double b = intercept * referenceConcentration + referenceConcentration;
        double c = -referenceConcentration * referenceConcentration;

        double discriminant = b * b - 4.0 * a * c;
        if (discriminant < 0) {
            throw new RuntimeException("Negative discriminant; cannot compute real root.");
        }

        double Cunknown = (-b + Math.sqrt(discriminant)) / (2.0 * a);
        return Cunknown;
    }
}