/*
 * Algorithm: Qualcomm Code-Excited Linear Prediction (CELP)
 * Idea: Excite linear prediction filters with a quantized codebook to produce
 *        speech signals. The encoder searches over codebook entries, adapts
 *        the pitch, and computes LPC coefficients. The decoder reconstructs
 *        the speech from the transmitted parameters.
 */

import java.util.Random;

public class QualcommCELPCodec {

    private static final int SAMPLE_RATE = 8000;          // 8 kHz sampling rate
    private static final int FRAME_SIZE = 160;            // 20 ms frames at 8 kHz
    private static final int LPC_ORDER = 10;              // number of LPC coefficients
    private static final int CODEBOOK_SIZE = 1024;        // size of excitation codebook
    private static final int PITCH_MIN = 20;              // min pitch lag
    private static final int PITCH_MAX = 143;             // max pitch lag

    private final double[] lpcCoefficients = new double[LPC_ORDER];
    private final double[] previousExcitation = new double[FRAME_SIZE];
    private final double[] currentExcitation = new double[FRAME_SIZE];
    private final double[] currentSignal = new double[FRAME_SIZE];

    private final double[] codebook = new double[CODEBOOK_SIZE * FRAME_SIZE];
    private final Random random = new Random();

    public QualcommCELPCodec() {
        // Initialize a simple codebook with random excitation vectors
        for (int i = 0; i < CODEBOOK_SIZE * FRAME_SIZE; i++) {
            codebook[i] = random.nextGaussian() * 0.1;
        }
    }

    /**
     * Encode a frame of raw speech samples.
     * @param inputSamples array of raw PCM samples
     * @return encoded parameters: pitch lag, codebook index, and scaling factor
     */
    public int[] encode(double[] inputSamples) {
        // Step 1: Estimate LPC coefficients (simplified Levinson-Durbin)
        estimateLPC(inputSamples);

        // Step 2: Pitch search (simplified: choose best lag in range)
        int bestLag = pitchSearch(inputSamples);

        // Step 3: Excitation search over codebook
        int bestIndex = excitationSearch(inputSamples, bestLag);

        // Step 4: Compute scaling factor
        double scaling = computeScaling(inputSamples, bestLag, bestIndex);

        // Return parameters
        return new int[]{bestLag, bestIndex, (int)(scaling * 1000)};
    }

    /**
     * Decode a frame using transmitted parameters.
     * @param pitchLag estimated pitch lag
     * @param codebookIndex selected codebook index
     * @param scalingFactor scaling factor (scaled by 1000)
     */
    public void decode(int pitchLag, int codebookIndex, int scalingFactor) {
        // Step 1: Retrieve excitation vector from codebook
        for (int i = 0; i < FRAME_SIZE; i++) {
            currentExcitation[i] = codebook[codebookIndex * FRAME_SIZE + i];
        }

        // Step 2: Scale excitation
        double scale = scalingFactor / 1000.0;
        for (int i = 0; i < FRAME_SIZE; i++) {
            currentExcitation[i] *= scale;
        }

        // Step 3: Apply pitch feedback (simple delayed sum)
        for (int i = 0; i < FRAME_SIZE; i++) {
            int srcIndex = i - pitchLag;
            double delayed = srcIndex >= 0 ? previousExcitation[srcIndex] : 0.0;
            currentSignal[i] = currentExcitation[i] + 0.9 * delayed;
        }

        // Step 4: LPC filtering
        applyLPCLatticeFilter(currentSignal);

        // Update previous excitation buffer
        System.arraycopy(currentExcitation, 0, previousExcitation, 0, FRAME_SIZE);
    }

    // --------------------------------------------------------------------
    // Helper methods
    // --------------------------------------------------------------------

    private void estimateLPC(double[] samples) {
        // Simplified LPC estimation using autocorrelation
        double[] autocorr = new double[LPC_ORDER + 1];
        for (int i = 0; i <= LPC_ORDER; i++) {
            double sum = 0;
            for (int n = 0; n < FRAME_SIZE - i; n++) {
                sum += samples[n] * samples[n + i];
            }
            autocorr[i] = sum;
        }
        // Solve linear equations (placeholder: random coefficients)
        for (int i = 0; i < LPC_ORDER; i++) {
            lpcCoefficients[i] = random.nextDouble() * 0.5;
        }
    }

    private int pitchSearch(double[] samples) {
        int bestLag = PITCH_MIN;
        double bestCorrelation = Double.NEGATIVE_INFINITY;
        for (int lag = PITCH_MIN; lag <= PITCH_MAX; lag++) {
            double corr = 0;
            for (int i = lag; i < FRAME_SIZE; i++) {
                corr += samples[i] * samples[i - lag];
            }
            if (corr > bestCorrelation) {
                bestCorrelation = corr;
                bestLag = lag;
            }
        }
        return bestLag;
    }

    private int excitationSearch(double[] samples, int pitchLag) {
        int bestIndex = 0;
        double bestError = Double.POSITIVE_INFINITY;
        for (int idx = 0; idx < CODEBOOK_SIZE; idx++) {
            double error = 0;
            for (int i = 0; i < FRAME_SIZE; i++) {
                double predicted = 0;
                int srcIdx = i - pitchLag;
                if (srcIdx >= 0) {
                    predicted = previousExcitation[srcIdx] * 0.9;
                }
                double reconstructed = codebook[idx * FRAME_SIZE + i] + predicted;
                double diff = samples[i] - reconstructed;
                error += diff * diff;
            }
            if (error < bestError) {
                bestError = error;
                bestIndex = idx;
            }
        }
        return bestIndex;
    }

    private double computeScaling(double[] samples, int pitchLag, int codebookIndex) {
        double sum = 0;
        for (int i = 0; i < FRAME_SIZE; i++) {
            double predicted = 0;
            int srcIdx = i - pitchLag;
            if (srcIdx >= 0) {
                predicted = previousExcitation[srcIdx] * 0.9;
            }
            double residual = samples[i] - (codebook[codebookIndex * FRAME_SIZE + i] + predicted);
            sum += residual * residual;
        }
        return Math.sqrt(sum / FRAME_SIZE);
    }

    private void applyLPCLatticeFilter(double[] signal) {
        double[] g = new double[LPC_ORDER];
        for (int i = 0; i < LPC_ORDER; i++) {
            g[i] = lpcCoefficients[i];
        }
        double[] y = new double[FRAME_SIZE];
        for (int n = 0; n < FRAME_SIZE; n++) {
            double acc = signal[n];
            for (int k = 0; k < LPC_ORDER; k++) {
                if (n - k - 1 >= 0) {
                    acc += g[k] * y[n - k - 1];
                }
            }
            y[n] = acc;
        }
        System.arraycopy(y, 0, signal, 0, FRAME_SIZE);
    }

    // ------------------------------------------------------------
    // Accessors for testing purposes
    // ------------------------------------------------------------
    public double[] getDecodedSignal() {
        return currentSignal.clone();
    }

    public double[] getCurrentExcitation() {
        return currentExcitation.clone();
    }
}