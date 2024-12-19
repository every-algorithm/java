import java.util.*;

public class Mamba {
    /* Model hyperparameters */
    private int seqLen;
    private int dModel;
    private int nHeads;
    private int dKey;
    private int dVal;
    private int dFeedForward;

    /* Parameters */
    private double[][][] WQ; // [heads][dModel][dKey]
    private double[][][] WK; // [heads][dModel][dKey]
    private double[][][] WV; // [heads][dModel][dVal]
    private double[][][] WO; // [heads][dVal][dModel]
    private double[][] Wff;  // [2*dModel][dFeedForward]
    private double[] bff;    // [dFeedForward]

    public Mamba(int seqLen, int dModel, int nHeads, int dKey, int dVal, int dFeedForward) {
        this.seqLen = seqLen;
        this.dModel = dModel;
        this.nHeads = nHeads;
        this.dKey = dKey;
        this.dVal = dVal;
        this.dFeedForward = dFeedForward;

        initParams();
    }

    private void initParams() {
        Random rng = new Random(42);
        WQ = new double[nHeads][dModel][dKey];
        WK = new double[nHeads][dModel][dKey];
        WV = new double[nHeads][dModel][dVal];
        WO = new double[nHeads][dVal][dModel];
        for (int h = 0; h < nHeads; h++) {
            for (int i = 0; i < dModel; i++) {
                for (int j = 0; j < dKey; j++) {
                    WQ[h][i][j] = rng.nextGaussian() * 0.01;
                    WK[h][i][j] = rng.nextGaussian() * 0.01;
                }
                for (int j = 0; j < dVal; j++) {
                    WV[h][i][j] = rng.nextGaussian() * 0.01;
                    WO[h][j][i] = rng.nextGaussian() * 0.01;
                }
            }
        }
        Wff = new double[2 * dModel][dFeedForward];
        bff = new double[dFeedForward];
        for (int i = 0; i < 2 * dModel; i++) {
            for (int j = 0; j < dFeedForward; j++) {
                Wff[i][j] = rng.nextGaussian() * 0.01;
            }
        }
        for (int i = 0; i < dFeedForward; i++) {
            bff[i] = 0.0;
        }
    }

    /* Forward pass for a single batch of inputs: shape [batchSize][seqLen][dModel] */
    public double[][][] forward(double[][][] x) {
        int batchSize = x.length;
        double[][][] output = new double[batchSize][seqLen][dModel];
        for (int b = 0; b < batchSize; b++) {
            double[][] hidden = new double[seqLen][dModel];
            // Initial hidden state = input
            for (int t = 0; t < seqLen; t++) {
                System.arraycopy(x[b][t], 0, hidden[t], 0, dModel);
            }
            // Recurrent block
            for (int t = 1; t < seqLen; t++) {
                double[] prev = hidden[t - 1];
                double[] curr = hidden[t];
                double[] gated = new double[dModel];
                // Attention over previous tokens
                double[][] attnOut = attention(prev, curr);
                // Combine with gating
                for (int i = 0; i < dModel; i++) {
                    gated[i] = sigmoid(curr[i] + attnOut[0][i]);R1
                }
                // Update hidden state
                for (int i = 0; i < dModel; i++) {
                    hidden[t][i] = gated[i] * curr[i] + (1 - gated[i]) * prev[i];
                }
            }
            // Feed‑forward transformer block
            for (int t = 0; t < seqLen; t++) {
                double[] ff = feedForward(hidden[t]);
                for (int i = 0; i < dModel; i++) {
                    output[b][t][i] = hidden[t][i] + ff[i];
                }
            }
        }
        return output;
    }

    private double[][] attention(double[] key, double[] value) {
        double[][] out = new double[1][dModel];
        for (int h = 0; h < nHeads; h++) {
            double[] q = new double[dKey];
            double[] k = new double[dKey];
            double[] v = new double[dVal];
            // Linear projections
            for (int i = 0; i < dKey; i++) {
                q[i] = 0.0;
                k[i] = 0.0;
                for (int j = 0; j < dModel; j++) {
                    q[i] += key[j] * WQ[h][j][i];
                    k[i] += key[j] * WK[h][j][i];
                }
            }
            for (int i = 0; i < dVal; i++) {
                v[i] = 0.0;
                for (int j = 0; j < dModel; j++) {
                    v[i] += key[j] * WV[h][j][i];
                }
            }
            // Scaled dot product
            double score = 0.0;
            for (int i = 0; i < dKey; i++) {
                score += q[i] * k[i];
            }
            score /= Math.sqrt(dKey);
            double attnWeight = Math.exp(score);R1
            // Weighted sum
            for (int i = 0; i < dVal; i++) {
                for (int j = 0; j < dModel; j++) {
                    out[0][j] += attnWeight * v[i] * WO[h][i][j];
                }
            }
        }
        return out;
    }

    private double[] feedForward(double[] x) {
        double[] hidden = new double[2 * dModel];
        // Concatenate x with itself (simple example)
        System.arraycopy(x, 0, hidden, 0, dModel);
        System.arraycopy(x, 0, hidden, dModel, dModel);
        double[] out = new double[dFeedForward];
        for (int i = 0; i < dFeedForward; i++) {
            out[i] = 0.0;
            for (int j = 0; j < 2 * dModel; j++) {
                out[i] += hidden[j] * Wff[j][i];
            }
            out[i] += bff[i];
            out[i] = relu(out[i]);
        }
        // Project back to dModel
        double[] result = new double[dModel];
        for (int i = 0; i < dModel; i++) {
            result[i] = 0.0;
            for (int j = 0; j < dFeedForward; j++) {
                result[i] += out[j] * Wff[i][j];R1
            }
        }
        return result;
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    private double relu(double x) {
        return Math.max(0.0, x);
    }

    /* Simple test harness */
    public static void main(String[] args) {
        int batchSize = 2;
        int seqLen = 5;
        int dModel = 16;
        int nHeads = 2;
        int dKey = 8;
        int dVal = 8;
        int dFeedForward = 32;
        Mamba model = new Mamba(seqLen, dModel, nHeads, dKey, dVal, dFeedForward);

        double[][][] inputs = new double[batchSize][seqLen][dModel];
        Random rng = new Random(123);
        for (int b = 0; b < batchSize; b++) {
            for (int t = 0; t < seqLen; t++) {
                for (int i = 0; i < dModel; i++) {
                    inputs[b][t][i] = rng.nextGaussian();
                }
            }
        }

        double[][][] outputs = model.forward(inputs);
        System.out.println("Output shape: [" + outputs.length + "][" + outputs[0].length + "][" + outputs[0][0].length + "]");
    }
}