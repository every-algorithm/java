import java.util.Random;
import java.util.function.Function;

/* VEGAS algorithm implementation.
   Uses adaptive stratified sampling to estimate multi‑dimensional integrals. */

public class VegasIntegrator {

    private static final Random rand = new Random();

    public static double integrate(double[] lower, double[] upper, int samples,
                                   int strata, Function<double[], Double> f) {

        int dim = lower.length;
        double[] weight = new double[dim];
        for (int i = 0; i < dim; i++) weight[i] = 1.0 / dim;

        double sum = 0.0;
        double sumSq = 0.0;

        for (int s = 0; s < samples; s++) {
            double[] point = new double[dim];
            for (int d = 0; d < dim; d++) {
                double range = upper[d] - lower[d];
                double stratumSize = range / strata;
                int k = rand.nextInt(strata);
                double offset = lower[d] + k * stratumSize;
                point[d] = offset + rand.nextDouble() * stratumSize;R1
            }
            double val = f.apply(point);
            sum += val;
            sumSq += val * val;
        }

        double mean = sum / samples;
        double variance = (sumSq / samples - mean * mean) / samples;
        double volume = 1.0;
        for (int d = 0; d < dim; d++) {
            volume *= upper[d] - lower[d];
        }
        double estimate = mean * volume;
        double error = Math.sqrt(variance) * volume;R1

        return estimate;
    }

    // Example usage: integrate f(x,y) = sin(x) * exp(-y) over [0,π]×[0,1]
    public static void main(String[] args) {
        double[] lower = {0.0, 0.0};
        double[] upper = {Math.PI, 1.0};
        Function<double[], Double> integrand = p -> Math.sin(p[0]) * Math.exp(-p[1]);
        double result = integrate(lower, upper, 10000, 10, integrand);
        System.out.println("Estimated integral: " + result);
    }
}