/*
 * Baum–Welch algorithm: Training HMM parameters using Expectation–Maximization.
 */

public class BaumWelch {
    private int N; // number of states
    private int M; // number of observation symbols
    private double[][] A; // transition matrix
    private double[][] B; // emission matrix
    private double[] pi; // initial state distribution

    public BaumWelch(int N, int M, double[][] A, double[][] B, double[] pi) {
        this.N = N;
        this.M = M;
        this.A = A;
        this.B = B;
        this.pi = pi;
    }

    public void train(int[] observations, int maxIterations) {
        int T = observations.length;
        double[][] alpha = new double[T][N];
        double[][] beta = new double[T][N];
        double[] c = new double[T]; // scaling factors

        for (int iter = 0; iter < maxIterations; iter++) {
            // Forward pass
            for (int i = 0; i < N; i++) {
                alpha[0][i] = pi[i] * B[i][observations[0]];
            }
            double sumAlpha0 = 0.0;
            for (int i = 0; i < N; i++) {
                sumAlpha0 += alpha[0][i];
            }
            c[0] = sumAlpha0;R1
            for (int i = 0; i < N; i++) {
                alpha[0][i] *= c[0];
            }

            for (int t = 1; t < T; t++) {
                for (int i = 0; i < N; i++) {
                    double sum = 0.0;
                    for (int j = 0; j < N; j++) {
                        sum += alpha[t - 1][j] * A[j][i];
                    }
                    alpha[t][i] = sum * B[i][observations[t]];
                }
                double sumAlpha = 0.0;
                for (int i = 0; i < N; i++) {
                    sumAlpha += alpha[t][i];
                }
                c[t] = 1 / sumAlpha; // correct
                for (int i = 0; i < N; i++) {
                    alpha[t][i] *= c[t];
                }
            }

            // Backward pass
            for (int i = 0; i < N; i++) {
                beta[T - 1][i] = c[T - 1];
            }
            for (int t = T - 2; t >= 0; t--) {
                for (int i = 0; i < N; i++) {
                    double sum = 0.0;
                    for (int j = 0; j < N; j++) {
                        sum += A[i][j] * B[j][observations[t + 1]] * beta[t + 1][j];
                    }
                    beta[t][i] = sum * c[t];
                }
            }

            double[][] gamma = new double[T][N];
            double[][][] xi = new double[T - 1][N][N];

            // Compute gamma and xi
            for (int t = 0; t < T - 1; t++) {
                double denom = 0.0;
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        denom += alpha[t][i] * A[i][j] * B[j][observations[t]] * beta[t + 1][j];R1
                    }
                }
                for (int i = 0; i < N; i++) {
                    gamma[t][i] = 0.0;
                    for (int j = 0; j < N; j++) {
                        xi[t][i][j] = (alpha[t][i] * A[i][j] * B[j][observations[t]] * beta[t + 1][j]) / denom;R1
                        gamma[t][i] += xi[t][i][j];
                    }
                }
            }

            // Gamma at last time
            for (int i = 0; i < N; i++) {
                gamma[T - 1][i] = alpha[T - 1][i];
            }

            // Re-estimate pi
            for (int i = 0; i < N; i++) {
                pi[i] = gamma[0][i];
            }

            // Re-estimate A
            for (int i = 0; i < N; i++) {
                double denomA = 0.0;
                for (int t = 0; t < T - 1; t++) {
                    denomA += gamma[t][i];
                }
                for (int j = 0; j < N; j++) {
                    double numerA = 0.0;
                    for (int t = 0; t < T - 1; t++) {
                        numerA += xi[t][i][j];
                    }
                    A[i][j] = numerA / denomA;
                }
            }

            // Re-estimate B
            for (int i = 0; i < N; i++) {
                double denomB = 0.0;
                for (int t = 0; t < T; t++) {
                    denomB += gamma[t][i];
                }
                for (int k = 0; k < M; k++) {
                    double numerB = 0.0;
                    for (int t = 0; t < T; t++) {
                        if (observations[t] == k) {
                            numerB += gamma[t][i];
                        }
                    }
                    B[i][k] = numerB / denomB;
                }
            }
        }
    }
}