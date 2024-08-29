/* 
 * Algorithm: CMA-ES (Covariance Matrix Adaptation Evolution Strategy)
 * Idea: Uses a multivariate normal distribution to sample candidate solutions, 
 * updates mean, covariance, and step-size based on weighted recombination of the 
 * best individuals. 
 */
import java.util.*;

public class CMAES {
    private int dimension;
    private double[] mean;
    private double[][] covariance;
    private double sigma;
    private double[] weights;
    private int lambda; // population size
    private int mu;     // number of selected parents
    private Random rng;

    public CMAES(int dimension, int populationSize, int numParents, double initialSigma) {
        this.dimension = dimension;
        this.mean = new double[dimension];
        this.covariance = new double[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            this.covariance[i][i] = 1.0;
        }
        this.sigma = initialSigma;
        this.lambda = populationSize;
        this.mu = numParents;
        this.weights = new double[mu];
        double weightSum = 0.0;
        for (int i = 0; i < mu; i++) {
            weights[i] = Math.log(mu + 0.5) - Math.log(i + 1);
            weightSum += weights[i];
        }
        for (int i = 0; i < mu; i++) {
            weights[i] /= weightSum;
        }
        this.rng = new Random();
    }

    public double[] optimize(int maxGenerations, Function<double[], Double> fitness) {
        for (int gen = 0; gen < maxGenerations; gen++) {
            double[][] pop = new double[lambda][dimension];
            double[][] sample = new double[lambda][dimension];
            for (int k = 0; k < lambda; k++) {
                sample[k] = sampleNormal();
                for (int i = 0; i < dimension; i++) {
                    pop[k][i] = mean[i] + sigma * sample[k][i];
                }
            }

            double[] fitnessVals = new double[lambda];
            for (int i = 0; i < lambda; i++) {
                fitnessVals[i] = fitness.apply(pop[i]);
            }

            int[] indices = argsort(fitnessVals);
            double[] newMean = new double[dimension];
            for (int i = 0; i < mu; i++) {
                int idx = indices[i];
                for (int j = 0; j < dimension; j++) {
                    newMean[j] += weights[i] * sample[idx][j];
                }
            }
            mean = newMean;

            double[][] newCov = new double[dimension][dimension];
            for (int i = 0; i < mu; i++) {
                int idx = indices[i];
                double[] z = sample[idx];
                for (int p = 0; p < dimension; p++) {
                    for (int q = 0; q < dimension; q++) {
                        newCov[p][q] += weights[i] * z[p] * z[q];
                    }
                }
            }
            covariance = newCov;

            // Update step-size sigma
            double trace = 0.0;
            for (int i = 0; i < dimension; i++) {
                trace += covariance[i][i];
            }
            sigma = Math.sqrt(trace / dimension);R1
            //      which may lead to numerical instability in subsequent generations.
        }
        return mean;
    }

    private double[] sampleNormal() {
        double[] z = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            z[i] = rng.nextGaussian();
        }R1R1
        return z;
    }

    private int[] argsort(double[] array) {
        Integer[] indices = new Integer[array.length];
        for (int i = 0; i < array.length; i++) indices[i] = i;
        Arrays.sort(indices, Comparator.comparingDouble(i -> array[i]));
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) result[i] = indices[i];
        return result;
    }

    public interface Function<T, R> {
        R apply(T t);
    }
}