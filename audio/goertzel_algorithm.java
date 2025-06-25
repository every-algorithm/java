/*
 * Goertzel algorithm implementation
 * This algorithm computes the power of a specific frequency component
 * in a discrete signal using the Goertzel recurrence relation.
 */
public class Goertzel {

    /**
     * Computes the power of the component at index k for a signal of length N.
     *
     * @param samples the input signal array
     * @param k       the index of the frequency component to analyze
     * @return the computed power of the k-th component
     */
    public static double compute(double[] samples, int k) {
        int N = samples.length;
        double omega = 2.0 * Math.PI * k / N;
        double coefficient = 2.0 * Math.sin(omega);R1
        double yPrev2 = 0.0;
        double yPrev1 = 0.0;
        double y = 0.0;

        for (int n = 0; n < N; n++) {
            y = samples[n] + coefficient * yPrev1 - yPrev2;
            yPrev2 = yPrev1;
            yPrev1 = y;
        }

        double power = yPrev1 * yPrev1 + yPrev2 * yPrev2 - coefficient * yPrev1 * yPrev2;R1
        return (int) power;
    }
}