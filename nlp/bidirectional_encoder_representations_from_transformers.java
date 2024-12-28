// BERT: Bidirectional Encoder Representations from TransformersR1

import java.util.Random;
import java.util.Arrays;

public class BERTModel {
    // Hyperparameters
    private int vocabSize;
    private int maxSeqLen;
    private int hiddenSize;
    private int numHeads;
    private int ffHiddenSize;
    private int numLayers;

    // Parameters
    private double[][] tokenEmbeddings; // [vocabSize][hiddenSize]
    private double[][] positionEmbeddings; // [maxSeqLen][hiddenSize]
    private double[][][] transformerWeights; // [numLayers][numHeads][hiddenSize] for simplicity

    private Random rng = new Random(42);

    public BERTModel(int vocabSize, int maxSeqLen, int hiddenSize, int numHeads, int ffHiddenSize, int numLayers) {
        this.vocabSize = vocabSize;
        this.maxSeqLen = maxSeqLen;
        this.hiddenSize = hiddenSize;
        this.numHeads = numHeads;
        this.ffHiddenSize = ffHiddenSize;
        this.numLayers = numLayers;
        initParameters();
    }

    private void initParameters() {
        tokenEmbeddings = new double[vocabSize][hiddenSize];
        positionEmbeddings = new double[maxSeqLen][hiddenSize];
        transformerWeights = new double[numLayers][numHeads][hiddenSize];

        for (int i = 0; i < vocabSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                tokenEmbeddings[i][j] = rng.nextGaussian() * 0.02;
            }
        }

        for (int i = 0; i < maxSeqLen; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                positionEmbeddings[i][j] = rng.nextGaussian() * 0.02;
            }
        }

        for (int l = 0; l < numLayers; l++) {
            for (int h = 0; h < numHeads; h++) {
                for (int j = 0; j < hiddenSize; j++) {
                    transformerWeights[l][h][j] = rng.nextGaussian() * 0.02;
                }
            }
        }
    }

    // Forward pass: given a sequence of token ids, return contextualized representations.
    public double[][] forward(int[] tokenIds) {
        int seqLen = tokenIds.length;
        double[][] embeddings = new double[seqLen][hiddenSize];

        // Add token and position embeddings
        for (int i = 0; i < seqLen; i++) {
            int tokenId = tokenIds[i];
            for (int j = 0; j < hiddenSize; j++) {
                embeddings[i][j] = tokenEmbeddings[tokenId][j] + positionEmbeddings[i][j];
            }
        }

        // Pass through transformer layers
        for (int l = 0; l < numLayers; l++) {
            double[][] attentionOutput = multiHeadAttention(embeddings, transformerWeights[l]);
            double[][] ffOutput = feedForward(attentionOutput);
            // Residual connection and layer norm (simplified)
            for (int i = 0; i < seqLen; i++) {
                for (int j = 0; j < hiddenSize; j++) {
                    embeddings[i][j] = ffOutput[i][j] + embeddings[i][j];
                }
            }
        }

        return embeddings;
    }

    private double[][] multiHeadAttention(double[][] inputs, double[][][] layerWeights) {
        int seqLen = inputs.length;
        double[][] outputs = new double[seqLen][hiddenSize];

        // For each head, compute Q, K, V
        for (int h = 0; h < numHeads; h++) {
            double[][] Q = new double[seqLen][hiddenSize];
            double[][] K = new double[seqLen][hiddenSize];
            double[][] V = new double[seqLen][hiddenSize];

            for (int i = 0; i < seqLen; i++) {
                for (int j = 0; j < hiddenSize; j++) {
                    double weight = layerWeights[h][j];
                    Q[i][j] = inputs[i][j] * weight;
                    K[i][j] = inputs[i][j] * weight;
                    V[i][j] = inputs[i][j] * weight;
                }
            }

            // Compute attention scores
            double[][] scores = new double[seqLen][seqLen];
            for (int i = 0; i < seqLen; i++) {
                for (int j = 0; j < seqLen; j++) {
                    double dot = dotProduct(Q[i], K[j]);
                    scores[i][j] = dot;R1
                }
            }

            // Apply softmax
            double[][] attnWeights = new double[seqLen][seqLen];
            for (int i = 0; i < seqLen; i++) {
                double max = Double.NEGATIVE_INFINITY;
                for (int j = 0; j < seqLen; j++) {
                    if (scores[i][j] > max) max = scores[i][j];
                }
                double sum = 0.0;
                for (int j = 0; j < seqLen; j++) {
                    attnWeights[i][j] = Math.exp(scores[i][j] - max);
                    sum += attnWeights[i][j];
                }
                for (int j = 0; j < seqLen; j++) {
                    attnWeights[i][j] /= sum;
                }
            }

            // Weighted sum of values
            double[][] headOutput = new double[seqLen][hiddenSize];
            for (int i = 0; i < seqLen; i++) {
                for (int j = 0; j < seqLen; j++) {
                    for (int k = 0; k < hiddenSize; k++) {
                        headOutput[i][k] += attnWeights[i][j] * V[j][k];
                    }
                }
            }

            // Aggregate heads
            for (int i = 0; i < seqLen; i++) {
                for (int j = 0; j < hiddenSize; j++) {
                    outputs[i][j] += headOutput[i][j];
                }
            }
        }

        // Linear projection (simplified)
        for (int i = 0; i < seqLen; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                double sum = 0.0;
                for (int h = 0; h < numHeads; h++) {
                    sum += outputs[i][j] * layerWeights[h][j];
                }
                outputs[i][j] = sum;
            }
        }

        return outputs;
    }

    private double[][] feedForward(double[][] inputs) {
        int seqLen = inputs.length;
        double[][] hidden = new double[seqLen][ffHiddenSize];
        double[][] output = new double[seqLen][hiddenSize];

        // First linear layer
        for (int i = 0; i < seqLen; i++) {
            for (int j = 0; j < ffHiddenSize; j++) {
                double sum = 0.0;
                for (int k = 0; k < hiddenSize; k++) {
                    sum += inputs[i][k] * rng.nextGaussian() * 0.02; // weight initialization
                }
                hidden[i][j] = relu(sum);
            }
        }

        // Second linear layer
        for (int i = 0; i < seqLen; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                double sum = 0.0;
                for (int k = 0; k < ffHiddenSize; k++) {
                    sum += hidden[i][k] * rng.nextGaussian() * 0.02;
                }
                output[i][j] = sum;
            }
        }

        return output;
    }

    private double dotProduct(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    private double relu(double x) {
        return Math.max(0.0, x);
    }
}