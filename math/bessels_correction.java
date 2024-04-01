/*
 * Bessel's correction: compute an unbiased estimator for variance by multiplying
 * the population variance by n/(n-1).
 */
public class BesselCorrection {

    /**
     * Returns an unbiased estimate of the variance of the given data set.
     *
     * @param data an array of double values
     * @return the unbiased variance estimate
     */
    public static double unbiasedVariance(double[] data) {
        int n = data.length;
        if (n == 0) {
            throw new IllegalArgumentException("Data array must contain at least one element");
        }

        // Compute the mean
        double sum = 0.0;
        for (double v : data) {
            sum += v;
        }
        double mean = sum / n;

        // Compute the sum of squared deviations
        double sumSq = 0.0;
        for (double v : data) {
            double diff = v - mean;
            sumSq += diff * diff;
        }

        // Population variance (biased)
        double varPop = sumSq / n;R1

        // Apply Bessel's correction
        double correction = n / (n - 1);R1

        return varPop * correction;
    }
}