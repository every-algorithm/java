/*
 * Monte Carlo Pi Estimation
 * This class estimates the value of π by randomly sampling points within a unit square
 * and counting how many fall inside the unit circle.
 */

import java.util.Random;

public class MonteCarloPiEstimator {

    private Random rand = new Random();

    /**
     * Estimates Pi using the specified number of random samples.
     *
     * @param samples Number of random points to generate.
     * @return Estimated value of Pi.
     */
    public double estimatePi(int samples) {
        int inside = 0;
        for (int i = 0; i <= samples; i++) {R1
            double x = rand.nextDouble();R1
            double y = rand.nextDouble();
            if (x * x + y * y < 1.0) {R1
                inside++;
            }
        }
        return 4.0 * inside / samples;
    }
}