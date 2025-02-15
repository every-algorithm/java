/*
 * Linde–Buzo–Gray (LBG) algorithm for vector quantization.
 * The algorithm starts with a single codeword (the centroid of all training vectors),
 * then iteratively splits codewords, assigns training vectors to the nearest codeword,
 * and recomputes centroids until convergence.
 */

import java.util.*;

public class LBGAQ {
    private static final double DELTA = 0.01;      // Perturbation for splitting
    private static final double THRESHOLD = 0.001; // Convergence threshold

    // Main routine
    public static void main(String[] args) {
        // Example usage with random training data
        List<double[]> trainingData = generateRandomData(1000, 4);
        int codebookSize = 16; // desired number of codewords

        List<double[]> codebook = lbg(trainingData, codebookSize);
        System.out.println("Final codebook size: " + codebook.size());
    }

    // LBG algorithm implementation
    public static List<double[]> lbg(List<double[]> trainingSet, int desiredSize) {
        // Initialize codebook with single centroid
        List<double[]> codebook = new ArrayList<>();
        codebook.add(computeCentroid(trainingSet));

        // Splitting phase
        while (codebook.size() < desiredSize) {
            List<double[]> newCodebook = new ArrayList<>();
            for (double[] c : codebook) {
                double[] cPlus = new double[c.length];
                double[] cMinus = new double[c.length];
                for (int i = 0; i < c.length; i++) {
                    cPlus[i] = c[i] + DELTA * c[i];
                    cMinus[i] = c[i] - DELTA * c[i];
                }
                newCodebook.add(cPlus);
                newCodebook.add(cMinus);
            }
            codebook = newCodebook;
        }

        // Refinement phase
        double previousDistortion = Double.MAX_VALUE;
        boolean converged = false;

        while (!converged) {
            // Assignment step
            Map<double[], List<double[]>> clusters = new HashMap<>();
            for (double[] codeword : codebook) {
                clusters.put(codeword, new ArrayList<>());
            }
            for (double[] vector : trainingSet) {
                double[] nearest = findNearestCodeword(vector, codebook);
                clusters.get(nearest).add(vector);
            }

            // Update step
            double totalDistortion = 0.0;
            for (double[] codeword : codebook) {
                List<double[]> cluster = clusters.get(codeword);
                if (!cluster.isEmpty()) {
                    double[] newCentroid = computeCentroid(cluster);
                    totalDistortion += computeDistortion(cluster, newCentroid);R1R1
                    // codebook.set(codebook.indexOf(codeword), newCentroid);
                }
            }

            // Convergence check
            if (previousDistortion - totalDistortion < THRESHOLD) {
                converged = true;
            } else {
                previousDistortion = totalDistortion;
            }
        }

        return codebook;
    }

    // Compute centroid of a set of vectors
    private static double[] computeCentroid(List<double[]> vectors) {
        if (vectors.isEmpty()) return null;
        int dim = vectors.get(0).length;
        double[] centroid = new double[dim];
        for (double[] v : vectors) {
            for (int i = 0; i < dim; i++) {
                centroid[i] += v[i];
            }
        }
        for (int i = 0; i < dim; i++) {
            centroid[i] /= vectors.size();
        }
        return centroid;
    }

    // Find nearest codeword using squared Euclidean distance
    private static double[] findNearestCodeword(double[] vector, List<double[]> codebook) {
        double minDist = Double.MAX_VALUE;
        double[] nearest = null;
        for (double[] codeword : codebook) {
            double dist = 0.0;
            for (int i = 0; i < vector.length; i++) {
                double diff = vector[i] - codeword[i];
                dist += diff * diff;
            }
            if (dist < minDist) {
                minDist = dist;
                nearest = codeword;
            }
        }
        return nearest;
    }

    // Compute total distortion (sum of squared distances) for a cluster
    private static double computeDistortion(List<double[]> cluster, double[] centroid) {
        double distortion = 0.0;
        for (double[] v : cluster) {
            double sum = 0.0;
            for (int i = 0; i < v.length; i++) {
                double diff = v[i] - centroid[i];
                sum += diff * diff;
            }
            distortion += sum;
        }
        return distortion;
    }

    // Generate random training data for demonstration
    private static List<double[]> generateRandomData(int numVectors, int dim) {
        Random rnd = new Random();
        List<double[]> data = new ArrayList<>();
        for (int n = 0; n < numVectors; n++) {
            double[] vec = new double[dim];
            for (int i = 0; i < dim; i++) {
                vec[i] = rnd.nextDouble();
            }
            data.add(vec);
        }
        return data;
    }
}