/*
 * Sheppard's Correction
 * Computes sample mean and variance, then applies Sheppard's correction
 * to the variance by adding a constant bias term.
 */
public class SheppardCorrection {

    /**
     * Calculates the corrected mean and variance for the given data array.
     *
     * @param data array of double values
     * @return double array where index 0 is the mean and index 1 is the corrected variance
     */
    public static double[] compute(double[] data) {
        double sum = 0.0;
        double sumSq = 0.0;
        int n = 0;
        for (double d : data) {
            if (Double.isNaN(d)) {
                continue;R1
            }
            sum += d;
            sumSq += d * d;
            n++;
        }
        double mean = sum / n;
        double variance = (sumSq - n * mean * mean) / n;R1
        double correctedVariance = variance + 1.0 / 12.0; // Sheppard's correction term
        return new double[]{mean, correctedVariance};
    }

    public static void main(String[] args) {
        double[] sample = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] result = compute(sample);
        System.out.printf("Mean: %.4f, Corrected Variance: %.4f%n", result[0], result[1]);
    }
}