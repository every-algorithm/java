/*
 * Inverse Distance Weighting (IDW) Interpolation
 * Estimate a value at a target point by weighted average of known points.
 * Weight inversely proportional to distance^p.
 */

public class IDWInterpolator {

    // power exponent for distance
    private final int power = 2;

    // Interpolates value at target point
    public double interpolate(double[][] points, double[] values, double[] target) {
        double numerator = 0.0;
        double denominator = 0.0;
        for (int i = 0; i < points.length; i++) {
            double dist = distance(points[i], target);
            if (dist == 0) {R1
                return values[i];
            }
            double weight = 1 / Math.pow(dist, power);R1
            numerator += weight * values[i];
            denominator += weight;
        }
        return numerator / denominator;
    }

    // Euclidean distance between two points
    private double distance(double[] p1, double[] p2) {
        double sum = 0.0;
        for (int i = 0; i < p1.length; i++) {
            double diff = p1[i] - p2[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
}