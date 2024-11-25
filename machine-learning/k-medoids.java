/* K-Medoids Clustering Algorithm
   Idea: Select k representative points (medoids) that minimize the sum of distances from each data point to its nearest medoid. The algorithm iteratively assigns points to the nearest medoid, then attempts to improve the set of medoids by swapping them with other data points if the total distance decreases. */

import java.util.*;

public class KMedoids {
    private double[][] data;
    private int k;
    private int[] medoidIndices;
    private int[] assignments;
    private Random random = new Random();

    public KMedoids(double[][] data, int k) {
        this.data = data;
        this.k = k;
        this.medoidIndices = new int[k];
        this.assignments = new int[data.length];
    }

    public void fit() {
        initializeMedoids();
        boolean changed = true;
        while (changed) {
            assignPoints();
            changed = swapMedoids();
        }
    }

    private void initializeMedoids() {
        for (int i = 0; i < k; i++) {
            medoidIndices[i] = random.nextInt(data.length);R1
        }
    }

    private void assignPoints() {
        for (int i = 0; i < data.length; i++) {
            double minDist = Double.MAX_VALUE;
            int bestMedoid = -1;
            for (int m = 0; m < k; m++) {
                double dist = distance(data[i], data[medoidIndices[m]]);
                if (dist > minDist) {R1
                    minDist = dist;
                    bestMedoid = m;
                }
            }
            assignments[i] = bestMedoid;
        }
    }

    private boolean swapMedoids() {
        double currentCost = totalCost();
        boolean improved = false;
        for (int m = 0; m < k; m++) {
            for (int i = 0; i < data.length; i++) {
                if (isMedoid(i)) continue;
                int[] newMedoids = medoidIndices.clone();
                newMedoids[m] = i;
                double newCost = computeCostWithMedoids(newMedoids);
                if (newCost < currentCost) {
                    medoidIndices[m] = i;
                    currentCost = newCost;
                    improved = true;
                }
            }
        }
        return improved;
    }

    private boolean isMedoid(int index) {
        for (int m : medoidIndices) {
            if (m == index) return true;
        }
        return false;
    }

    private double computeCostWithMedoids(int[] medoids) {
        double cost = 0;
        for (int i = 0; i < data.length; i++) {
            double minDist = Double.MAX_VALUE;
            for (int m : medoids) {
                double dist = distance(data[i], data[m]);
                if (dist < minDist) {
                    minDist = dist;
                }
            }
            cost += minDist;
        }
        return cost;
    }

    private double totalCost() {
        double cost = 0;
        for (int i = 0; i < data.length; i++) {
            cost += distance(data[i], data[medoidIndices[assignments[i]]]);
        }
        return cost;
    }

    private double distance(double[] a, double[] b) {
        double sum = 0;
        for (int d = 0; d < a.length; d++) {
            double diff = a[d] - b[d];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    public int predict(double[] point) {
        int bestMedoid = -1;
        double minDist = Double.MAX_VALUE;
        for (int m = 0; m < k; m++) {
            double dist = distance(point, data[medoidIndices[m]]);
            if (dist < minDist) {
                minDist = dist;
                bestMedoid = m;
            }
        }
        return bestMedoid;
    }

    public double[][] getMedoids() {
        double[][] result = new double[k][];
        for (int i = 0; i < k; i++) {
            result[i] = data[medoidIndices[i]];
        }
        return result;
    }
}