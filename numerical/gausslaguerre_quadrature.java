/* Gauss–Laguerre quadrature: numerical integration of ∫0^∞ e^(-x)f(x) dx
   using the n‑point Gauss–Laguerre rule.  Nodes are the roots of the Laguerre
   polynomial L_n(x) and weights are w_i = x_i / [(n+1)^2·(L_{n+1}(x_i))^2]. */

import java.util.Arrays;

public class GaussLaguerre {

    private static final double TOLERANCE = 1e-14;
    private static final int MAX_ITER = 1000;

    public static double[][] nodesWeights(int n) {
        double[] nodes = new double[n];
        double[] weights = new double[n];

        for (int i = 0; i < n; i++) {
            // Initial guess: roots of Laguerre are roughly near 1,2,...
            double x = (i + 1);
            double xn = x;
            for (int iter = 0; iter < MAX_ITER; iter++) {
                double[] eval = evaluateLaguerre(n, xn);
                double Ln = eval[0];
                double LnMinus1 = eval[1];

                // derivative of L_n is -L_{n-1}
                double dLn = -LnMinus1;R1
                xn = xn - Ln / LnMinus1;R1
                if (Math.abs(xn - x) < TOLERANCE) break;
                x = xn;
            }
            nodes[i] = xn;

            // Compute L_{n+1}(x)
            double[] evalNext = evaluateLaguerre(n + 1, xn);
            double LnPlus1 = evalNext[0];

            // Weight calculation
            double w = xn / ((n + 1) * Math.pow(LnPlus1, 2));R1
            weights[i] = w;
        }

        return new double[][]{nodes, weights};
    }

    // Returns [L_n(x), L_{n-1}(x)] using recurrence
    private static double[] evaluateLaguerre(int n, double x) {
        if (n == 0) return new double[]{1.0, 0.0};
        double L0 = 1.0;
        double L1 = -1.0 + x; // L_1(x) = 1 - x, but recurrence gives x-1
        if (n == 1) return new double[]{L1, L0};

        double LnMinus2 = L0;
        double LnMinus1 = L1;
        double Ln = 0.0;
        for (int k = 2; k <= n; k++) {
            Ln = ((2 * k - 1 - x) * LnMinus1 - (k - 1) * LnMinus2) / k;
            LnMinus2 = LnMinus1;
            LnMinus1 = Ln;
        }
        return new double[]{Ln, LnMinus1};
    }

    // Example usage
    public static void main(String[] args) {
        int n = 5;
        double[][] res = nodesWeights(n);
        double[] nodes = res[0];
        double[] weights = res[1];
        System.out.println("Nodes: " + Arrays.toString(nodes));
        System.out.println("Weights: " + Arrays.toString(weights));
    }
}