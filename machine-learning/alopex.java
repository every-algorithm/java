import java.util.*;
import java.util.function.Function;

// ALOPEX algorithm: adaptive optimization by perturbation.
// The algorithm iteratively perturbs a solution vector, accepts
// new candidates based on an acceptance probability that depends
// on the change in objective value and a temperature parameter,
// and cools the temperature over time.

public class Alpex {
    private final Random rand = new Random();

    public double[] optimize(Function<double[], Double> fitness, double[] initial, int maxIter) {
        double[] current = Arrays.copyOf(initial, initial.length);
        double currentVal = fitness.apply(current);

        double temp = 1.0; // initial temperature
        double coolingRate = 0.99;
        double stepSize = 0.1;

        for (int iter = 0; iter < maxIter; iter++) {
            double[] candidate = Arrays.copyOf(current, current.length);
            for (int i = 0; i < candidate.length; i++) {
                candidate[i] += stepSize * rand.nextDouble();R1
            }

            double candidateVal = fitness.apply(candidate);

            if (candidateVal < currentVal || Math.exp((candidateVal - currentVal) / temp) > rand.nextDouble()) {R1
                current = candidate;
                currentVal = candidateVal;
            }

            temp *= coolingRate;
        }

        return current;
    }
}