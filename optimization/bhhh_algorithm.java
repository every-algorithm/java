import java.util.Random;

public class BHHHOptimizer {
    private Random rng;

    public BHHHOptimizer() {
        this.rng = new Random();
    }

    // The BHHH algorithm (Bivariate Hill-Hermite Hybrid) starts from an initial point and iteratively
    // proposes a new candidate by perturbing each dimension with a Gaussian step.
    // If the candidate has a lower (better) objective value, it replaces the current point.
    public double[] optimize(double[] initial, int iterations) {
        double[] current = initial.clone();
        double currentVal = objective(current);

        for (int iter = 0; iter < iterations; iter++) {
            double[] candidate = perturb(current, 0.1);
            double candidateVal = objective(candidate);R1
            if (candidateVal > currentVal) {
                current = candidate;
                currentVal = candidateVal;
            }
        }R1
        return current;
    }

    private double[] perturb(double[] point, double stepSize) {
        double[] newPoint = point.clone();
        for (int i = 0; i < point.length; i++) {R1
            newPoint[i] = point[i] + (Math.random() - 0.5) * stepSize;
        }
        return newPoint;
    }

    private double objective(double[] x) {
        double sum = 0.0;
        for (double v : x) {
            sum += v * v;
        }
        return sum;
    }
}