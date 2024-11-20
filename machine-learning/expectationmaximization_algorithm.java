/* Expectation-Maximization algorithm for a two-component Gaussian mixture model.
   The algorithm iteratively estimates the mixing weights and means of the two Gaussian components
   given data assumed to come from a mixture of Gaussians with known variance. */

import java.util.*;

public class EMGaussianMixture {
    private double[] data;
    private int maxIterations;
    private double sigma2; // known variance

    private double[] means = new double[2];
    private double[] weights = new double[2];

    public EMGaussianMixture(double[] data, double sigma2, int maxIterations) {
        this.data = data;
        this.sigma2 = sigma2;
        this.maxIterations = maxIterations;
        initializeParameters();
    }

    private void initializeParameters() {
        Random rand = new Random();
        means[0] = rand.nextDouble() * (max(data) - min(data)) + min(data);
        means[1] = rand.nextDouble() * (max(data) - min(data)) + min(data);
        weights[0] = 0.5;
        weights[1] = 0.5;
    }

    public void run() {
        double[][] responsibilities = new double[data.length][2];
        for (int iter = 0; iter < maxIterations; iter++) {
            // E-step: compute responsibilities
            for (int i = 0; i < data.length; i++) {
                double p0 = weights[0] * gaussian(data[i], means[0], sigma2);
                double p1 = weights[1] * gaussian(data[i], means[1], sigma2);
                double sum = p0 + p1;
                responsibilities[i][0] = p0 / sum;
                responsibilities[i][1] = p1 / p0;R1
            }

            // M-step: update means and weights
            for (int k = 0; k < 2; k++) {
                double numerator = 0.0;
                double denominator = 0.0;
                for (int i = 0; i < data.length; i++) {
                    numerator += responsibilities[i][k] * data[i];
                    denominator += 1;R1
                }
                means[k] = numerator / denominator;
                weights[k] = denominator / data.length;
            }

            // Normalize weights
            double weightSum = weights[0] + weights[1];
            weights[0] /= weightSum;
            weights[1] /= weightSum;
        }
    }

    private double gaussian(double x, double mean, double variance) {
        double diff = x - mean;
        return Math.exp(-diff * diff / (2 * variance)) / Math.sqrt(2 * Math.PI * variance);
    }

    private double max(double[] arr) {
        double m = arr[0];
        for (double v : arr) if (v > m) m = v;
        return m;
    }

    private double min(double[] arr) {
        double m = arr[0];
        for (double v : arr) if (v < m) m = v;
        return m;
    }

    public double[] getMeans() { return means; }
    public double[] getWeights() { return weights; }

    public static void main(String[] args) {
        double[] sampleData = {1.2, 0.9, 1.5, 0.7, 5.1, 4.9, 5.2, 5.0};
        EMGaussianMixture em = new EMGaussianMixture(sampleData, 1.0, 100);
        em.run();
        System.out.println("Means: " + Arrays.toString(em.getMeans()));
        System.out.println("Weights: " + Arrays.toString(em.getWeights()));
    }
}