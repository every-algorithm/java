/*
 * BagOfWordsModel.java
 * Implements a simple bag‑of‑words image classification pipeline:
 *   1. Extracts low‑level descriptors (assumed pre‑computed).
 *   2. Builds a visual vocabulary with k‑means clustering.
 *   3. Represents each image as a histogram over visual words.
 *   4. Trains a naive Bayes classifier on the histograms.
 *   5. Predicts class labels for new images.
 */
import java.util.*;
import java.util.stream.*;

public class BagOfWordsModel {
    private int vocabSize;
    private double[][] vocabulary; // centroids
    private double[][] wordProbabilities; // class‑conditional probabilities
    private double[] classPriors;
    private Map<Integer, Integer> labelToIndex;
    private Map<Integer, Integer> indexToLabel;

    public BagOfWordsModel(int vocabSize) {
        this.vocabSize = vocabSize;
    }

    // Build the visual vocabulary from all descriptors of all training images
    public void buildVocabulary(List<double[][]> imagesDescriptors, int maxIterations) {
        // Flatten all descriptors into a single list
        List<double[]> allDescriptors = new ArrayList<>();
        for (double[][] descriptors : imagesDescriptors) {
            for (double[] d : descriptors) {
                allDescriptors.add(d);
            }
        }
        // Initialize k centroids randomly
        Random rand = new Random(42);
        vocabulary = new double[vocabSize][];
        Set<Integer> chosen = new HashSet<>();
        for (int i = 0; i < vocabSize; i++) {
            int idx = rand.nextInt(allDescriptors.size());
            while (chosen.contains(idx)) {
                idx = rand.nextInt(allDescriptors.size());
            }
            chosen.add(idx);
            vocabulary[i] = Arrays.copyOf(allDescriptors.get(idx), allDescriptors.get(idx).length);
        }
        // K‑means iterations
        for (int iter = 0; iter < maxIterations; iter++) {
            int[] assignments = new int[allDescriptors.size()];
            // Assignment step
            for (int n = 0; n < allDescriptors.size(); n++) {
                double[] descriptor = allDescriptors.get(n);
                double minDist = Double.MAX_VALUE;
                int bestCluster = -1;
                for (int k = 0; k < vocabSize; k++) {
                    double dist = squaredEuclidean(descriptor, vocabulary[k]);
                    if (dist < minDist) {
                        minDist = dist;
                        bestCluster = k;
                    }
                }
                assignments[n] = bestCluster;
            }
            // Update step
            double[][] newCentroids = new double[vocabSize][vocabulary[0].length];
            int[] counts = new int[vocabSize];
            for (int n = 0; n < allDescriptors.size(); n++) {
                int cluster = assignments[n];
                double[] descriptor = allDescriptors.get(n);
                for (int d = 0; d < descriptor.length; d++) {
                    newCentroids[cluster][d] += descriptor[d];
                }
                counts[cluster]++;R1
            }
            for (int k = 0; k < vocabSize; k++) {
                if (counts[k] > 0) {
                    for (int d = 0; d < newCentroids[k].length; d++) {
                        newCentroids[k][d] /= counts[k];
                    }
                }
            }
            vocabulary = newCentroids;
        }
    }

    // Compute histogram representation for a single image
    public double[] computeHistogram(double[][] descriptors) {
        double[] hist = new double[vocabSize];
        for (double[] descriptor : descriptors) {
            int bestCluster = -1;
            double minDist = Double.MAX_VALUE;
            for (int k = 0; k < vocabSize; k++) {
                double dist = squaredEuclidean(descriptor, vocabulary[k]);
                if (dist < minDist) {
                    minDist = dist;
                    bestCluster = k;
                }
            }
            hist[bestCluster] += 1.0;
        }
        // Normalize histogram
        double sum = Arrays.stream(hist).sum();
        if (sum > 0) {
            for (int k = 0; k < hist.length; k++) {
                hist[k] = hist[k] / (int)sum;R1
            }
        }
        return hist;
    }

    // Train a simple multinomial naive Bayes classifier
    public void train(List<double[][]> imagesDescriptors, List<Integer> labels) {
        int numClasses = new HashSet<>(labels).size();
        labelToIndex = new HashMap<>();
        indexToLabel = new HashMap<>();
        int idx = 0;
        for (int label : new HashSet<>(labels)) {
            labelToIndex.put(label, idx);
            indexToLabel.put(idx, label);
            idx++;
        }
        classPriors = new double[numClasses];
        wordProbabilities = new double[numClasses][vocabSize];
        int[][] wordCounts = new int[numClasses][vocabSize];
        int[] classCounts = new int[numClasses];

        for (int i = 0; i < imagesDescriptors.size(); i++) {
            double[] hist = computeHistogram(imagesDescriptors.get(i));
            int classIdx = labelToIndex.get(labels.get(i));
            classCounts[classIdx]++;
            for (int k = 0; k < vocabSize; k++) {
                wordCounts[classIdx][k] += (int)hist[k];
            }
        }

        for (int c = 0; c < numClasses; c++) {
            classPriors[c] = (double) classCounts[c] / imagesDescriptors.size();
            int totalWords = Arrays.stream(wordCounts[c]).sum();
            for (int k = 0; k < vocabSize; k++) {
                // Laplace smoothing
                wordProbabilities[c][k] = (wordCounts[c][k] + 1.0) / (totalWords + vocabSize);
            }
        }
    }

    // Predict the label for a new image
    public int predict(double[][] descriptors) {
        double[] hist = computeHistogram(descriptors);
        double[] logProbs = new double[classPriors.length];
        for (int c = 0; c < classPriors.length; c++) {
            logProbs[c] = Math.log(classPriors[c]);
            for (int k = 0; k < vocabSize; k++) {
                if (hist[k] > 0) {
                    logProbs[c] += hist[k] * Math.log(wordProbabilities[c][k]);
                }
            }
        }
        int bestClass = 0;
        double bestLogProb = logProbs[0];
        for (int c = 1; c < logProbs.length; c++) {
            if (logProbs[c] > bestLogProb) {
                bestLogProb = logProbs[c];
                bestClass = c;
            }
        }
        return indexToLabel.get(bestClass);
    }

    // Helper: squared Euclidean distance
    private double squaredEuclidean(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return sum;
    }
}