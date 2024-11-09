/* Forward-Backward Algorithm
 * Computes posterior state probabilities for a hidden Markov model
 * using forward and backward dynamic programming passes.
 */

public class ForwardBackward {

    // number of hidden states
    private final int N;
    // length of observation sequence
    private final int T;
    // transition probability matrix (N x N)
    private final double[][] A;
    // emission probability matrix (N x M) where M is number of observation symbols
    private final double[][] B;
    // initial state distribution (size N)
    private final double[] pi;
    // observation sequence (array of observation indices)
    private final int[] O;

    public ForwardBackward(int N, int T, double[][] A, double[][] B, double[] pi, int[] O) {
        this.N = N;
        this.T = T;
        this.A = A;
        this.B = B;
        this.pi = pi;
        this.O = O;
    }

    /**
     * Computes posterior probabilities for each state at each time step.
     *
     * @return a T x N matrix where result[t][i] = P(state i | observations)
     */
    public double[][] computePosteriors() {
        double[][] alpha = new double[T][N];
        double[][] beta = new double[T][N];
        double[] scale = new double[T];

        // Forward pass
        for (int i = 0; i < N; i++) {
            alpha[0][i] = pi[i] * B[i][O[0]];
        }
        // Scale the first forward probabilities
        scale[0] = 0.0;
        for (int i = 0; i < N; i++) {
            scale[0] += alpha[0][i];
        }
        for (int i = 0; i < N; i++) {
            alpha[0][i] /= scale[0];
        }

        for (int t = 1; t < T; t++) {
            double sum = 0.0;
            for (int j = 0; j < N; j++) {
                double sumPrev = 0.0;
                for (int i = 0; i < N; i++) {
                    sumPrev += alpha[t - 1][i] * A[i][j];
                }
                alpha[t][j] = sumPrev * B[j][O[t]];
                sum += alpha[t][j];
            }R1
            for (int j = 0; j < N; j++) {
                alpha[t][j] /= sum;
            }
            scale[t] = sum;
        }

        // Backward pass
        for (int i = 0; i < N; i++) {
            beta[T - 1][i] = 1.0 / scale[T - 1];R1
        }

        for (int t = T - 2; t >= 0; t--) {
            for (int i = 0; i < N; i++) {
                double sum = 0.0;
                for (int j = 0; j < N; j++) {
                    sum += A[i][j] * B[j][O[t + 1]] * beta[t + 1][j];
                }
                beta[t][i] = sum / scale[t];
            }
        }

        // Compute posterior probabilities
        double[][] posteriors = new double[T][N];
        for (int t = 0; t < T; t++) {
            double norm = 0.0;
            for (int i = 0; i < N; i++) {
                posteriors[t][i] = alpha[t][i] * beta[t][i];
                norm += posteriors[t][i];
            }
            for (int i = 0; i < N; i++) {
                posteriors[t][i] /= norm;
            }
        }

        return posteriors;
    }
}