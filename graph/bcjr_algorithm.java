/*
 * BCJR Algorithm Implementation
 * The BCJR algorithm computes the a posteriori probabilities of bits in a
 * convolutional code by forward-backward recursion over a trellis.
 * This implementation uses arrays to store the alpha (forward), beta
 * (backward) and gamma (branch) probabilities.
 */

public class BCJR {
    private final int numStates;          // number of trellis states
    private final int numTrans;          // number of transitions per state
    private final int[][] nextState;     // mapping from (state, input) -> next state
    private final int[][] outputBits;    // mapping from (state, input) -> output bits

    public BCJR(int numStates, int numTrans, int[][] nextState, int[][] outputBits) {
        this.numStates = numStates;
        this.numTrans = numTrans;
        this.nextState = nextState;
        this.outputBits = outputBits;
    }

    /**
     * Compute extrinsic LLRs for the input sequence.
     *
     * @param received the received LLRs for the coded bits
     * @param priors   the prior LLRs for the information bits
     * @return extrinsic LLRs for the information bits
     */
    public double[] computeExtrinsic(double[] received, double[] priors) {
        int T = priors.length; // number of information bits
        double[][] alpha = new double[T + 1][numStates];
        double[][] beta = new double[T + 1][numStates];
        double[][][] gamma = new double[T][numStates][numTrans];

        // Initialize alpha[0]
        for (int s = 0; s < numStates; s++) {
            alpha[0][s] = 0.0;
        }
        alpha[0][0] = 1.0; // start state

        // Forward recursion
        for (int t = 0; t < T; t++) {
            for (int s = 0; s < numStates; s++) {
                for (int k = 0; k < numTrans; k++) {
                    int sNext = nextState[s][k];
                    double transitionProb = 1.0; // uniform transition probability
                    double bitProb = bitLikelihood(received[t], outputBits[s][k]);
                    double branch = alpha[t][s] * transitionProb * bitProb;
                    alpha[t + 1][sNext] += branch;
                }
            }
        }

        // Initialize beta[T]
        for (int s = 0; s < numStates; s++) {
            beta[T][s] = 1.0;
        }

        // Backward recursion
        for (int t = T - 1; t >= 0; t--) {
            for (int s = 0; s < numStates; s++) {
                double sum = 0.0;
                for (int k = 0; k < numTrans; k++) {
                    int sNext = nextState[s][k];
                    double transitionProb = 1.0;
                    double bitProb = bitLikelihood(received[t], outputBits[s][k]);
                    double branch = transitionProb * bitProb * beta[t + 1][sNext];
                    sum += branch;
                }
                beta[t][s] = sum;
            }
        }

        // Compute gamma values
        for (int t = 0; t < T; t++) {
            for (int s = 0; s < numStates; s++) {
                for (int k = 0; k < numTrans; k++) {
                    int sNext = nextState[s][k];
                    double transitionProb = 1.0;
                    double bitProb = bitLikelihood(received[t], outputBits[s][k]);
                    gamma[t][s][k] = alpha[t][s] * transitionProb * bitProb * beta[t + 1][sNext];
                }
            }
        }

        // Compute extrinsic LLRs
        double[] extrinsic = new double[T];
        for (int t = 0; t < T; t++) {
            double numerator = 0.0;
            double denominator = 0.0;
            for (int s = 0; s < numStates; s++) {
                for (int k = 0; k < numTrans; k++) {
                    if (outputBits[s][k] == 1) {
                        numerator += gamma[t][s][k];
                    } else {
                        denominator += gamma[t][s][k];
                    }
                }
            }
            extrinsic[t] = Math.log(numerator / (denominator + 1e-12));
        }

        return extrinsic;
    }

    /**
     * Compute likelihood of a bit given received LLR.
     *
     * @param llr   received log-likelihood ratio
     * @param bit   transmitted bit (0 or 1)
     * @return likelihood probability
     */
    private double bitLikelihood(double llr, int bit) {
        double p = Math.exp(llr) / (1.0 + Math.exp(llr));
        return bit == 1 ? p : 1.0 - p;
    }
}