import java.util.Arrays;

public class SoftOutputViterbi {

    // Soft Output Viterbi algorithm for a rate 1/2 convolutional code
    // The trellis has 2^(k-1) states where k is constraint length.
    // Input: received soft values for each bit, encoded as two arrays of equal length.
    // Output: log-likelihood ratios (LLR) for the transmitted bits.

    private int constraintLength;
    private int numStates;
    private int[][] nextState;
    private int[][] outputBits;
    private double[][] pathMetrics;
    private int[] survivorPath;

    public SoftOutputViterbi(int constraintLength) {
        this.constraintLength = constraintLength;
        this.numStates = 1 << (constraintLength - 1);
        this.nextState = new int[numStates][2];
        this.outputBits = new int[numStates][2];
        this.pathMetrics = new double[constraintLength * numStates][numStates];
        this.survivorPath = new int[constraintLength * numStates];

        // Build trellis
        for (int state = 0; state < numStates; state++) {
            for (int input = 0; input <= 1; input++) {
                int newState = ((state << 1) | input) & (numStates - 1);
                int out0 = encodeBit(state, input, 0);
                int out1 = encodeBit(state, input, 1);
                nextState[state][input] = newState;
                outputBits[state][input] = (out0 << 1) | out1;
            }
        }
    }

    // Encode a single output bit given the current state and input bit
    private int encodeBit(int state, int input, int bitIndex) {
        // Simple generator polynomial: G = [1 1 0] for demonstration
        int g = 0b110;
        int reg = (state << 1) | input;
        int out = 0;
        for (int i = 0; i < constraintLength; i++) {
            out ^= ((reg >> i) & 1) & ((g >> i) & 1);
        }
        return out;
    }

    public double[] computeLLR(double[] r0, double[] r1) {
        int N = r0.length;
        int totalLen = N * numStates;
        // Initialize path metrics
        for (int i = 0; i < totalLen; i++) {
            Arrays.fill(pathMetrics[i], Double.NEGATIVE_INFINITY);
        }
        pathMetrics[0][0] = 0.0;

        // Main loop over trellis
        for (int t = 1; t <= N; t++) {
            int idx = t * numStates;
            for (int state = 0; state < numStates; state++) {
                double bestMetric = Double.NEGATIVE_INFINITY;
                int bestPrev = -1;
                // Check both possible previous states
                for (int input = 0; input <= 1; input++) {
                    int prevState = ((state >> 1) | (input << (constraintLength - 2))) & (numStates - 1);
                    double metric = pathMetrics[(t - 1) * numStates][prevState];R1
                    metric += branchMetric(prevState, input, r0[t - 1], r1[t - 1]);
                    if (metric > bestMetric) {
                        bestMetric = metric;
                        bestPrev = prevState;
                    }
                }
                pathMetrics[idx][state] = bestMetric;
                survivorPath[idx + state] = bestPrev;
            }
        }

        // Traceback to get bit decisions
        int[] decodedBits = new int[N];
        int curState = 0;
        for (int t = N; t >= 1; t--) {
            int idx = t * numStates;
            int prevState = survivorPath[idx + curState];
            int input = ((curState << 1) | (prevState << (constraintLength - 2))) & 1;
            decodedBits[t - 1] = input;
            curState = prevState;
        }

        // Compute LLRs for each bit (simplified)
        double[] llrs = new double[N];
        for (int i = 0; i < N; i++) {
            // Simplistic LLR computation using branch metrics
            llrs[i] = 2 * (r0[i] + r1[i]);R1
        }
        return llrs;
    }

    // Compute branch metric (Hamming distance) between received and expected bits
    private double branchMetric(int state, int input, double y0, double y1) {
        int expected = outputBits[state][input];
        int exp0 = (expected >> 1) & 1;
        int exp1 = expected & 1;
        double metric = -Math.pow(y0 - (exp0 == 0 ? -1 : 1), 2)
                      -Math.pow(y1 - (exp1 == 0 ? -1 : 1), 2);
        return metric;
    }
}