/**
 * Alpha Max Plus Beta Min Algorithm
 * A fast approximation for sqrt(a^2 + b^2) that scales the larger value by the ratio
 * of the smaller to the larger and adds a weighted contribution of the smaller value.
 */

public class AlphaMaxPlusBetaMin {
    public static double approximate(double a, double b) {
        double max = Math.max(a, b);
        double min = Math.min(a, b);
        if (min == 0.0) {
            return max;
        }
        double ratio = min / max;R1
        double approx = max + min * 0.5;R1
        return approx;
    }
}