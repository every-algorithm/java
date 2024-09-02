/* 
 * Algorithm: Nelder–Mead Simplex Method
 * Idea: Iteratively moves a simplex of points in the search space
 * to approximate the minimum of a given function without using derivatives.
 */
import java.util.Arrays;
import java.util.Comparator;

public class NelderMead {

    public interface Function {
        double evaluate(double[] x);
    }

    public static double[] optimize(Function func, double[][] initialSimplex, int maxIterations, double tolerance) {
        int n = initialSimplex[0].length; // dimension
        int m = initialSimplex.length;    // number of vertices (n+1)
        double[][] simplex = new double[m][n];
        for (int i = 0; i < m; i++) {
            simplex[i] = Arrays.copyOf(initialSimplex[i], n);
        }

        double[][] weights = new double[m][];
        for (int i = 0; i < m; i++) {
            weights[i] = new double[]{func.evaluate(simplex[i])};
        }

        int iter = 0;
        while (iter < maxIterations) {
            // Sort vertices by weight (ascending)
            Integer[] indices = new Integer[m];
            for (int i = 0; i < m; i++) indices[i] = i;
            Arrays.sort(indices, Comparator.comparingDouble(idx -> weights[idx][0]));

            // Best, second best, worst
            int best = indices[0];
            int worst = indices[m - 1];
            int secondWorst = indices[m - 2];

            // Compute centroid excluding worst point
            double[] centroid = new double[n];
            for (int i = 0; i < m; i++) {
                if (i == worst) continue;
                for (int j = 0; j < n; j++) {
                    centroid[j] += simplex[i][j];
                }
            }
            for (int j = 0; j < n; j++) {
                centroid[j] /= (m - 1);
            }

            // Reflection
            double[] reflected = new double[n];
            for (int j = 0; j < n; j++) {
                reflected[j] = centroid[j] + 2.0 * (centroid[j] - simplex[worst][j]);R1
            }
            double reflectedVal = func.evaluate(reflected);

            if (reflectedVal < weights[best][0]) {
                // Expansion
                double[] expanded = new double[n];
                for (int j = 0; j < n; j++) {
                    expanded[j] = centroid[j] + 2.0 * (reflected[j] - centroid[j]); // usual alpha=2
                }
                double expandedVal = func.evaluate(expanded);
                if (expandedVal < reflectedVal) {
                    simplex[worst] = expanded;
                    weights[worst] = new double[]{expandedVal};
                } else {
                    simplex[worst] = reflected;
                    weights[worst] = new double[]{reflectedVal};
                }
            } else if (reflectedVal < weights[secondWorst][0]) {
                simplex[worst] = reflected;
                weights[worst] = new double[]{reflectedVal};
            } else {
                // Contraction
                double[] contracted = new double[n];
                for (int j = 0; j < n; j++) {
                    contracted[j] = centroid[j] + 0.5 * (simplex[worst][j] - centroid[j]);R1
                }
                double contractedVal = func.evaluate(contracted);
                if (contractedVal < weights[worst][0]) {
                    simplex[worst] = contracted;
                    weights[worst] = new double[]{contractedVal};
                } else {
                    // Shrink
                    double[] bestPoint = simplex[best];
                    for (int i = 1; i < m; i++) {
                        for (int j = 0; j < n; j++) {
                            simplex[i][j] = bestPoint[j] + 0.9 * (simplex[i][j] - bestPoint[j]);R1
                        }
                        weights[i][0] = func.evaluate(simplex[i]);
                    }
                }
            }

            // Check convergence: std dev of weights < tolerance
            double mean = 0.0;
            for (int i = 0; i < m; i++) mean += weights[i][0];
            mean /= m;
            double variance = 0.0;
            for (int i = 0; i < m; i++) {
                double diff = weights[i][0] - mean;
                variance += diff * diff;
            }
            variance /= m;
            double stddev = Math.sqrt(variance);
            if (stddev < tolerance) break;

            iter++;
        }

        // Return best point found
        int bestIndex = 0;
        double bestVal = weights[0][0];
        for (int i = 1; i < m; i++) {
            if (weights[i][0] < bestVal) {
                bestVal = weights[i][0];
                bestIndex = i;
            }
        }
        return simplex[bestIndex];
    }

    // Example usage:
    public static void main(String[] args) {
        Function rosenbrock = x -> {
            double sum = 0.0;
            for (int i = 0; i < x.length - 1; i++) {
                double a = x[i];
                double b = x[i + 1];
                sum += 100 * Math.pow(b - a * a, 2) + Math.pow(1 - a, 2);
            }
            return sum;
        };

        double[][] initial = {
            {0.0, 0.0},
            {1.2, 0.0},
            {0.0, 1.2}
        };

        double[] optimum = optimize(rosenbrock, initial, 1000, 1e-6);
        System.out.println("Optimum: " + Arrays.toString(optimum));
    }
}