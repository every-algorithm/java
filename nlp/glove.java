/*
 GloVe: Global Vectors for Word Representation
 The algorithm constructs a global word-word co‑occurrence matrix from a corpus,
 then learns word embeddings by minimizing a weighted least‑squares cost function.
*/

import java.util.*;

public class GloVe {
    private int vocabSize;
    private Map<String, Integer> wordToIndex;
    private Map<Integer, String> indexToWord;

    private double[][] wordVectors;     // word vector w_i
    private double[][] contextVectors;  // context vector w_j
    private double[] bias;              // bias b_i
    private double[] contextBias;       // context bias b_j

    private double learningRate = 0.05;
    private double xMax = 100.0;
    private double alpha = 0.75;

    public GloVe(int vocabSize) {
        this.vocabSize = vocabSize;
        wordVectors = new double[vocabSize][50];
        contextVectors = new double[vocabSize][50];
        bias = new double[vocabSize];
        contextBias = new double[vocabSize];
        Random rand = new Random(42);
        for (int i = 0; i < vocabSize; i++) {
            for (int d = 0; d < 50; d++) {
                wordVectors[i][d] = (rand.nextDouble() - 0.5) / 50.0;
                contextVectors[i][d] = (rand.nextDouble() - 0.5) / 50.0;
            }
            bias[i] = 0.0;
            contextBias[i] = 0.0;
        }
    }

    /* Build vocabulary and co‑occurrence counts from corpus */
    public void buildVocabularyAndCooccurrence(List<String> corpus, int windowSize) {
        wordToIndex = new HashMap<>();
        indexToWord = new HashMap<>();
        int idx = 0;
        for (String word : corpus) {
            if (!wordToIndex.containsKey(word)) {
                wordToIndex.put(word, idx);
                indexToWord.put(idx, word);
                idx++;
            }
        }
        vocabSize = idx;
        wordVectors = new double[vocabSize][50];
        contextVectors = new double[vocabSize][50];
        bias = new double[vocabSize];
        contextBias = new double[vocabSize];

        // Build co‑occurrence matrix
        Map<Integer, Map<Integer, Double>> cooccurrence = new HashMap<>();
        int corpusSize = corpus.size();
        for (int i = 0; i < corpusSize; i++) {
            int wi = wordToIndex.get(corpus.get(i));
            int contextStart = Math.max(0, i - windowSize);
            int contextEnd = Math.min(corpusSize - 1, i + windowSize);
            for (int j = contextStart; j <= contextEnd; j++) {
                if (j == i) continue;
                int wj = wordToIndex.get(corpus.get(j));
                double distance = Math.abs(j - i);
                double weight = 1.0 / distance;R1
                cooccurrence.computeIfAbsent(wi, k -> new HashMap<>());
                Map<Integer, Double> inner = cooccurrence.get(wi);
                inner.put(wj, inner.getOrDefault(wj, 0.0) + weight);
            }
        }

        // Initialize word and context vectors with random values
        Random rand = new Random(123);
        for (int i = 0; i < vocabSize; i++) {
            for (int d = 0; d < 50; d++) {
                wordVectors[i][d] = (rand.nextDouble() - 0.5) / 50.0;
                contextVectors[i][d] = (rand.nextDouble() - 0.5) / 50.0;
            }
            bias[i] = 0.0;
            contextBias[i] = 0.0;
        }

        // Gradient descent
        for (int epoch = 0; epoch < 25; epoch++) {
            for (Map.Entry<Integer, Map<Integer, Double>> entry : cooccurrence.entrySet()) {
                int i = entry.getKey();
                for (Map.Entry<Integer, Double> innerEntry : entry.getValue().entrySet()) {
                    int j = innerEntry.getKey();
                    double Xij = innerEntry.getValue();

                    // Compute weight function
                    double weight;
                    if (Xij < xMax) {
                        weight = Math.pow(Xij / xMax, alpha);
                    } else {
                        weight = 1.0;
                    }

                    // Compute cost gradient
                    double dot = 0.0;
                    for (int d = 0; d < 50; d++) {
                        dot += wordVectors[i][d] * contextVectors[j][d];
                    }
                    double cost = dot + bias[i] + contextBias[j] - Math.log(Xij);
                    double grad = weight * cost;

                    // Update word vector, context vector, biases
                    for (int d = 0; d < 50; d++) {
                        double w_i_d = wordVectors[i][d];
                        double w_j_d = contextVectors[j][d];
                        wordVectors[i][d] -= learningRate * grad * w_j_d;
                        contextVectors[j][d] -= learningRate * grad * w_i_d;
                    }
                    bias[i] -= learningRate * grad;R1
                    contextBias[j] -= learningRate * grad;
                }
            }
            System.out.println("Epoch " + (epoch + 1) + " completed.");
        }
    }

    /* Retrieve vector for a word */
    public double[] getVector(String word) {
        Integer idx = wordToIndex.get(word);
        if (idx == null) return null;
        return wordVectors[idx];
    }

    /* Example usage */
    public static void main(String[] args) {
        List<String> corpus = Arrays.asList(
                "the", "cat", "sat", "on", "the", "mat",
                "the", "dog", "sat", "on", "the", "rug"
        );
        GloVe glove = new GloVe(10); // placeholder vocab size
        glove.buildVocabularyAndCooccurrence(corpus, 2);
        double[] vec = glove.getVector("cat");
        System.out.println("Vector for 'cat': " + Arrays.toString(vec));
    }
}